package ulb.bdma.dm.impl;

import java.util.*;
import ulb.bdma.dm.contract.DBScan;
import ulb.bdma.dm.contract.DistanceMeasurable;
import ulb.bdma.dm.models.ClusterPoint;

/**
 * Implementation of DBScan based on Ester, Martin, Hans-Peter Kriegel, Jörg Sander, and Xiaowei Xu.
 * "A density-based algorithm for discovering clusters in large spatial databases with noise." In
 * Kdd, vol. 96, no. 34, pp. 226-231. 1996.
 *
 * @see <a href="https://www.aaai.org/Papers/KDD/1996/KDD96-037.pdf">A density-based algorithm for
 *     discovering clusters in large spatial databases with noise.</a>
 */
public class NaiveDBScan extends DBScan {
    /**
     * Instantiate NaiveDBScan with parameters required for clustering
     *
     * @param epsilon maximum distance between points to be considered neighbours
     * @param minimumPoints minimum number of points required to determine core points (density)
     * @param dataPoints data that needs to be clustered
     */
    public NaiveDBScan(float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints) {
        super(epsilon, minimumPoints, dataPoints);
    }

    @Override
    public List<List<DistanceMeasurable>> cluster() {
        List<List<DistanceMeasurable>> clusters = new ArrayList<>();
        for (var clusterPoint : clusterPoints) {
            if (!clusterPoint.unvisited()) {
                continue;
            }
            Queue<ClusterPoint> neighbours =
                    new LinkedList<>(getNeighboursByVisiting(clusterPoint));
            if (!isCorePoint(neighbours)) {
                clusterPoint.noise();
                continue;
            }
            List<DistanceMeasurable> newCluster = new ArrayList<>();
            addToCluster(newCluster, clusterPoint);
            while (!neighbours.isEmpty()) {
                var neighbour = neighbours.poll();
                if (!neighbour.visited()) {
                    List<ClusterPoint> neighbourOfNeighbours = getNeighboursByVisiting(neighbour);
                    if (isCorePoint(neighbourOfNeighbours)) {
                        neighbours.addAll(neighbourOfNeighbours);
                    }
                }
                if (!neighbour.isAssignedToCluster()) {
                    addToCluster(newCluster, neighbour);
                }
            }
            clusters.add(newCluster);
        }
        addNoiseAsClusters(clusters);
        return clusters;
    }

    private void addToCluster(Collection<DistanceMeasurable> newCluster, ClusterPoint neighbour) {
        neighbour.assignToCluster();
        newCluster.add(neighbour.getDataPoint());
    }

    private void addNoiseAsClusters(List<List<DistanceMeasurable>> clusters) {
        clusterPoints.stream()
                .filter(ClusterPoint::isNoise)
                .forEach(x -> clusters.add(List.of(x.getDataPoint())));
    }
}
