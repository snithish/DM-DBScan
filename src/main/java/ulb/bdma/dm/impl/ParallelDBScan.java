package ulb.bdma.dm.impl;

import ulb.bdma.dm.contract.DBScan;
import ulb.bdma.dm.contract.DistanceMeasurable;

import java.util.List;

/**
 * Implementation of DBScan based on
 * Patwary, Mostofa Ali, Diana Palsetia, Ankit Agrawal, Wei-keng Liao, Fredrik Manne, and Alok Choudhary. "A new scalable parallel DBSCAN algorithm using the disjoint-set data structure." In Proceedings of the International Conference on High Performance Computing, Networking, Storage and Analysis, p. 62. IEEE Computer Society Press, 2012.
 * @see <a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.269.2628&rep=rep1&type=pdf">A new scalable parallel DBSCAN algorithm using the disjoint-set data structure.</a>
 */
public class ParallelDBScan implements DBScan {
    @Override
    public List<List<DistanceMeasurable>> cluster(float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints) {
        return null;
    }
}
