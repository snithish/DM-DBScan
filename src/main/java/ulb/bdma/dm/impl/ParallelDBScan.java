package ulb.bdma.dm.impl;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import ulb.bdma.dm.contract.DBScan;
import ulb.bdma.dm.contract.DistanceMeasurable;
import ulb.bdma.dm.contract.IntermediateCluster;
import ulb.bdma.dm.models.ClusterPoint;

/**
 * Implementation of DBScan based on Patwary, Mostofa Ali, Diana Palsetia, Ankit Agrawal, Wei-keng
 * Liao, Fredrik Manne, and Alok Choudhary. "A new scalable parallel DBSCAN algorithm using the
 * disjoint-set data structure." In Proceedings of the International Conference on High Performance
 * Computing, Networking, Storage and Analysis, p. 62. IEEE Computer Society Press, 2012.
 *
 * @see <a
 *     href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.269.2628&rep=rep1&type=pdf">A
 *     new scalable parallel DBSCAN algorithm using the disjoint-set data structure.</a>
 */
public class ParallelDBScan extends DBScan {
    private int chunkSize;
    private List<List<ClusterPoint>> partitions;
    HashMap<ClusterPoint, ClusterPoint> clusterAssignment;

    /**
     * Instantiate ParallelDBScan with parameters required for clustering
     *
     * @param epsilon maximum distance between points to be considered neighbours
     * @param minimumPoints minimum number of points required to determine core points (density)
     * @param dataPoints data that needs to be clustered
     */
    public ParallelDBScan(float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints) {
        super(epsilon, minimumPoints, dataPoints);
        // creating a HashMap with all the elements
        clusterAssignment = new HashMap<>(clusterPoints.size());
        clusterPoints.forEach(x -> clusterAssignment.put(x, null));
        partitions = new ArrayList<>();
        chunkSize = Math.max(2, dataPoints.size() / Runtime.getRuntime().availableProcessors());
        partitionData();
    }

    class UnseenPoint {
        private ClusterPoint corePoint;
        private ClusterPoint unseenPoint;

        public UnseenPoint(ClusterPoint corePoint, ClusterPoint unseenPoint) {
            this.corePoint = corePoint;
            this.unseenPoint = unseenPoint;
        }

        public ClusterPoint getCorePoint() {
            return corePoint;
        }

        public ClusterPoint getUnseenPoint() {
            return unseenPoint;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnseenPoint that = (UnseenPoint) o;
            return (Objects.equals(corePoint, that.corePoint)
                            && Objects.equals(unseenPoint, that.unseenPoint))
                    || Objects.equals(corePoint, that.unseenPoint)
                            && Objects.equals(unseenPoint, that.corePoint);
        }

        @Override
        public int hashCode() {
            return Objects.hash(corePoint) + Objects.hash(unseenPoint);
        }
    }

    @Override
    public List<List<DistanceMeasurable>> cluster() {
        List<IntermediateCluster> intermediateClusters =
                partitions.stream()
                        .parallel()
                        .flatMap(x -> naiveCluster(x).stream())
                        .collect(Collectors.toList());

        Set<UnseenPoint> unseenPoints = getUnseenPoints(intermediateClusters);

        Map<ClusterPoint, ReentrantLock> locks = new HashMap<>();
        unseenPoints.forEach(
                unseenPoint -> {
                    locks.put(unseenPoint.getCorePoint(), new ReentrantLock());
                    locks.put(unseenPoint.getUnseenPoint(), new ReentrantLock());
                });

        unseenPoints.stream()
                .parallel()
                .forEach(
                        unseenPoint ->
                                unionUsingLocks(
                                        locks,
                                        unseenPoint.getCorePoint(),
                                        unseenPoint.getUnseenPoint()));

        return identifyClusters();
    }

    private List<List<DistanceMeasurable>> identifyClusters() {
        Map<ClusterPoint, List<DistanceMeasurable>> clusters = new HashMap<>();
        clusterAssignment
                .keySet()
                .forEach(
                        key -> {
                            ClusterPoint parent = key;
                            while (!getParent(parent).equals(parent)) {
                                parent = getParent(parent);
                            }
                            if (Objects.isNull(clusters.get(parent))) {
                                clusters.put(parent, new ArrayList<>());
                            }
                            clusters.get(parent).add(key.getDataPoint());
                        });
        return new ArrayList<>(clusters.values());
    }

    private void unionUsingLocks(
            Map<ClusterPoint, ReentrantLock> locks, ClusterPoint x, ClusterPoint y) {
        while (!getParent(x).equals(getParent(y))) {
            System.out.println("trying to assign: " + x + " and " + y);
            if (getParent(x).hashCode() < getParent(y).hashCode()) {
                x = assignParent(locks, x, y);
            } else {
                y = assignParent(locks, y, x);
            }
        }
    }

    public ClusterPoint assignParent(
            Map<ClusterPoint, ReentrantLock> locks, ClusterPoint node, ClusterPoint parent) {
        if (node.equals(getParent(node))) {
            ReentrantLock lock = locks.get(getParent(node));
            try {
                while (!lock.tryLock()) {}
                if (node.equals(getParent(node))) {
                    clusterAssignment.put(node, getParent(parent));
                }
            } finally {
                lock.unlock();
            }
        }
        return getParent(node);
    }

    private ClusterPoint getParent(ClusterPoint point) {
        return Optional.ofNullable(clusterAssignment.get(point)).orElse(point);
    }

    private Set<UnseenPoint> getUnseenPoints(List<IntermediateCluster> intermediateClusters) {
        return intermediateClusters.stream()
                .parallel()
                .flatMap(
                        x ->
                                x.getUnseenPoints().stream()
                                        .parallel()
                                        .filter(
                                                unseenPoint ->
                                                        Objects.isNull(
                                                                clusterAssignment.get(unseenPoint)))
                                        .map(u -> new UnseenPoint(x.getCorePoint(), u)))
                .collect(Collectors.toSet());
    }

    private void lockAndAssignParent(
            Map<ClusterPoint, ReentrantLock> locks,
            ClusterPoint corePoint,
            ClusterPoint canBeAssigned) {
        ReentrantLock pointLock = locks.get(canBeAssigned);
        try {
            while (!pointLock.tryLock()) ;
            if (Objects.isNull(clusterAssignment.get(canBeAssigned))) {
                clusterAssignment.put(canBeAssigned, corePoint);
            }
        } finally {
            pointLock.unlock();
        }
    }

    public List<IntermediateCluster> naiveCluster(List<ClusterPoint> partition) {
        List<IntermediateCluster> in = new ArrayList<>();
        for (var clusterPoint : partition) {
            if (clusterPoint.visited()) {
                continue;
            }
            Queue<ClusterPoint> neighbours =
                    new LinkedList<>(getNeighboursByVisiting(clusterPoint));
            List<ClusterPoint> clusters = new ArrayList<>();
            List<ClusterPoint> notSeen = new ArrayList<>();
            if (!isCorePoint(neighbours)) {
                clusterPoint.noise();
                continue;
            }
            // The point is core point hence, get all its neighbours

            clusterPoint.assignToCluster();
            clusters.add(clusterPoint);
            while (!neighbours.isEmpty()) {
                var neighbour = neighbours.poll();
                if (!partition.contains(neighbour)) {
                    notSeen.add(neighbour);
                    continue;
                }
                if (!neighbour.visited()) {
                    if (partition.contains(neighbour)) {
                        List<ClusterPoint> neighbourOfNeighbours =
                                getNeighboursByVisiting(neighbour);
                        if (isCorePoint(neighbourOfNeighbours)) {
                            neighbours.addAll(neighbourOfNeighbours);
                        }
                    }
                    if (!neighbour.isAssignedToCluster()) {
                        neighbour.assignToCluster();
                        clusters.add(neighbour);
                        clusterAssignment.put(neighbour, clusterPoint);
                    }
                }
            }
            in.add(new IntermediateCluster(clusterPoint, clusters, notSeen));
        }
        return in;
    }

    private void partitionData() {
        // creating partitions of equal chunk size
        for (int start = 0, end = 0; end < clusterPoints.size(); start = start + chunkSize) {
            end = Math.min(clusterPoints.size(), start + chunkSize);
            partitions.add(clusterPoints.subList(start, end));
        }
    }
}
