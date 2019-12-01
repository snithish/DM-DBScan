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
 * href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.269.2628&rep=rep1&type=pdf">A
 * new scalable parallel DBSCAN algorithm using the disjoint-set data structure.</a>
 */
public class ParallelDBScan extends DBScan {
    int chunkSize = 2;
    private List<List<ClusterPoint>> partitions;
    HashMap<ClusterPoint, ClusterPoint> clusterAssignment;

    /**
     * Instantiate ParallelDBScan with parameters required for clustering
     *
     * @param epsilon       maximum distance between points to be considered neighbours
     * @param minimumPoints minimum number of points required to determine core points (density)
     * @param dataPoints    data that needs to be clustered
     */
    public ParallelDBScan(float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints) {
        super(epsilon, minimumPoints, dataPoints);
        // creating a HashMap with all the elements
        clusterAssignment = new HashMap<>(clusterPoints.size());
        clusterPoints.stream().forEach(x -> clusterAssignment.put(x, null));
        partitions = new ArrayList<>();
        partitionData();
    }

    @Override
    public List<List<DistanceMeasurable>> cluster() {
        List<IntermediateCluster> intermediateClusters =
                partitions.stream()
                        .parallel()
                        .flatMap(x -> naiveCluster(x).stream())
                        .collect(Collectors.toList());
        var unseenPoints = intermediateClusters.stream()
                .parallel()
                .flatMap(
                        x -> x.getUnseenPoints().stream()
                                .parallel()
                                .filter(
                                        unseenPoint ->
                                                Objects.isNull(
                                                        clusterAssignment.get(unseenPoint)))).collect(Collectors.toSet());

        Map<ClusterPoint, ReentrantLock> locks = new HashMap<>();
        unseenPoints.forEach(unseenPoint -> locks.put(unseenPoint, new ReentrantLock()));
        intermediateClusters.stream().map(IntermediateCluster::getCorePoint).forEach(x -> locks.put(x, new ReentrantLock()));

        intermediateClusters.stream()
                .parallel()
                .forEach(
                        intermediateCluster -> {
                            intermediateCluster.getUnseenPoints().stream()
                                    .parallel()
                                    .filter(
                                            unseenPoint ->
                                                    Objects.isNull(
                                                            clusterAssignment.get(unseenPoint)))
                                    .forEach(canBeAssigned -> {
                                        if (Objects.hash(intermediateCluster.getCorePoint()) > Objects.hash(canBeAssigned)) {
                                            lockAndAssignParent(locks, intermediateCluster.getCorePoint(), canBeAssigned);
                                        }
                                    });
                        });


        /**
         * // // finding all nearest neighbours of the current point // array ->
         * clusterPoint.getNearestNeighbours(clusterPoints, epsilon); // check whether the current
         * point is a qualfies as a CORE POINT //isCorePoint(Collection<ClusterPoint> neighbours) //
         * iterate over the list // case a - it does belong to the current cluster // case a.1 - IF
         * the neighbor is CORE POINT itself, union the point to the cluster using UNION AND LOCK
         * --Refer Algorithm // case a.2 - IF the neighbor is NOT CORE POINT itself, add it to the
         * cluster and mark as a member // case b - merge the current cluster with that point`s
         * cluster //
         */
        return null;
    }

    private void lockAndAssignParent(Map<ClusterPoint, ReentrantLock> locks, ClusterPoint corePoint, ClusterPoint canBeAssigned) {
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
