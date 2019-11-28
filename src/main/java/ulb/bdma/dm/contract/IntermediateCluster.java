package ulb.bdma.dm.contract;

import java.util.List;
import ulb.bdma.dm.models.ClusterPoint;

public class IntermediateCluster {
    List<ClusterPoint> tempCluster;
    List<ClusterPoint> unseenPoints;

    public List<ClusterPoint> getUnseenPoints() {
        return unseenPoints;
    }

    public IntermediateCluster(List<ClusterPoint> tempCluster, List<ClusterPoint> unseenPoints) {
        this.tempCluster = tempCluster;
        this.unseenPoints = unseenPoints;
    }
}
