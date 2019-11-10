package ulb.bdma.dm.impl;

import java.util.List;
import ulb.bdma.dm.contract.DBScan;
import ulb.bdma.dm.contract.DistanceMeasurable;

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

    /**
     * Instantiate ParallelDBScan with parameters required for clustering
     *
     * @param epsilon maximum distance between points to be considered neighbours
     * @param minimumPoints minimum number of points required to determine core points (density)
     * @param dataPoints data that needs to be clustered
     */
    public ParallelDBScan(float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints) {
        super(epsilon, minimumPoints, dataPoints);
    }

    @Override
    public List<List<DistanceMeasurable>> cluster() {
        return null;
    }
}
