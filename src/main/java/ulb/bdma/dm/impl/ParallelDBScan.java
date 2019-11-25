package ulb.bdma.dm.impl;

import java.util.*;

import ulb.bdma.dm.contract.DBScan;
import ulb.bdma.dm.contract.DistanceMeasurable;
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
    int chunkSize = 3;
    private List<List<ClusterPoint>> partitions;

    /**
     * Instantiate ParallelDBScan with parameters required for clustering
     *
     * @param epsilon       maximum distance between points to be considered neighbours
     * @param minimumPoints minimum number of points required to determine core points (density)
     * @param dataPoints    data that needs to be clustered
     */
    public ParallelDBScan(float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints) {
        super(epsilon, minimumPoints, dataPoints);
        partitionData();
    }

    @Override
    public List<List<DistanceMeasurable>> cluster() {
        partitions.stream().parallel().map(partition -> {
            blah(partition);
            return null;
        });
        /**
        //
        //  finding all nearest neighbours of the current point
        // array -> clusterPoint.getNearestNeighbours(clusterPoints, epsilon);
        // check whether the current point is a qualfies as a CORE POINT
        //isCorePoint(Collection<ClusterPoint> neighbours)
        // iterate over the list
        // case a - it does belong to the current cluster
        // case a.1 - IF the neighbor is CORE POINT itself, union the point to the cluster using UNION AND LOCK --Refer Algorithm
        // case a.2 - IF the neighbor is NOT CORE POINT itself, add it to the cluster and mark as a member
        // case b - merge the current cluster with that point`s cluster
        //
         */
        return null;
    }

    public void blah(List<ClusterPoint> partition) {
        for (var clusterPoint : partition){
            if (!clusterPoint.visited()) {
                continue;
            }
            getNeighboursByVisiting(clusterPoint);
            Queue<ClusterPoint> neighbours =
                    new LinkedList<>(getNeighboursByVisiting(clusterPoint));
            if (!isCorePoint(neighbours)) {
                clusterPoint.noise();
                continue;
            }
            List<DistanceMeasurable> newCluster = new ArrayList<>();
            HashMap<String, String> clusters = new HashMap<String, String>();
            clusters.put("a","a1");

        }
    }


    private void partitionData() {
        partitions = new ArrayList<>();
        for (int start = 0, end = 0; end < clusterPoints.size(); start = start + chunkSize) {
            end = Math.min(clusterPoints.size(), start + chunkSize);
            partitions.add(clusterPoints.subList(start, end));
        }
    }
}
