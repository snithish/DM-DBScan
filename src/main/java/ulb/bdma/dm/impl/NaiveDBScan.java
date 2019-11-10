package ulb.bdma.dm.impl;

import java.util.List;
import ulb.bdma.dm.contract.DBScan;
import ulb.bdma.dm.contract.DistanceMeasurable;

/**
 * Implementation of DBScan based on Ester, Martin, Hans-Peter Kriegel, Jörg Sander, and Xiaowei Xu.
 * "A density-based algorithm for discovering clusters in large spatial databases with noise." In
 * Kdd, vol. 96, no. 34, pp. 226-231. 1996.
 *
 * @see <a href="https://www.aaai.org/Papers/KDD/1996/KDD96-037.pdf">A density-based algorithm for
 *     discovering clusters in large spatial databases with noise.</a>
 */
public class NaiveDBScan implements DBScan {
    @Override
    public List<List<DistanceMeasurable>> cluster(
            float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints) {
        return null;
    }
}
