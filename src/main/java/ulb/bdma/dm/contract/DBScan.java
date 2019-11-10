package ulb.bdma.dm.contract;

import java.util.List;

/** Representation of DBScan Clustering algorithms */
public interface DBScan {
    /**
     * Clusters the given data points whose distance can be measured
     *
     * @param epsilon maximum distance between points to be considered neighbours
     * @param minimumPoints minimum number of points required to determine core points (density)
     * @param dataPoints data that needs to be clustered
     * @return List of Clusters
     */
    List<List<DistanceMeasurable>> cluster(
            float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints);
}
