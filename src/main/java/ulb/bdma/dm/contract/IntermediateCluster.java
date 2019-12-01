package ulb.bdma.dm.contract;

import java.util.List;

import ulb.bdma.dm.models.ClusterPoint;

public class IntermediateCluster {
    private ClusterPoint corePoint;
    private List<ClusterPoint> tempCluster;
    private List<ClusterPoint> unseenPoints;

    public IntermediateCluster(ClusterPoint corePoint, List<ClusterPoint> tempCluster, List<ClusterPoint> unseenPoints) {
        this.corePoint = corePoint;
        this.tempCluster = tempCluster;
        this.unseenPoints = unseenPoints;
    }

    public List<ClusterPoint> getUnseenPoints() {
        return unseenPoints;
    }

    public ClusterPoint getCorePoint() {
        return corePoint;
    }
}
