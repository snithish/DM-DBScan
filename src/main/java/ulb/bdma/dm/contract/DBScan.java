package ulb.bdma.dm.contract;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import ulb.bdma.dm.models.ClusterPoint;

/** Representation of DBScan Clustering algorithms */
public abstract class DBScan {
    protected float epsilon;
    protected int minimumPoints;
    protected List<ClusterPoint> clusterPoints;

    /**
     * For use by sub classes
     *
     * @param epsilon maximum distance between points to be considered neighbours
     * @param minimumPoints minimum number of points required to determine core points (density)
     * @param dataPoints data that needs to be clustered
     */
    protected DBScan(float epsilon, int minimumPoints, List<DistanceMeasurable> dataPoints) {
        this.epsilon = epsilon;
        this.minimumPoints = minimumPoints;
        this.clusterPoints =
                dataPoints.stream().map(ClusterPoint::new).collect(Collectors.toList());
    }

    protected boolean isCorePoint(Collection<ClusterPoint> neighbours) {
        return (neighbours.size() + 1) >= minimumPoints;
    }

    protected List<ClusterPoint> getNeighboursByVisiting(ClusterPoint clusterPoint) {
        clusterPoint.visit();
        return clusterPoint.getNearestNeighbours(clusterPoints, epsilon);
    }

    /**
     * Clusters the given data points whose distance can be measured
     *
     * @return List of Clusters
     */
    public abstract List<List<DistanceMeasurable>> cluster();
}
