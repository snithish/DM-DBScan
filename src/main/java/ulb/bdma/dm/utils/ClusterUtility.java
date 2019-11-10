package ulb.bdma.dm.utils;

import java.util.List;
import java.util.stream.Collectors;
import ulb.bdma.dm.models.ClusterPoint;

public class ClusterUtility {
    public static List<ClusterPoint> getNearestNeighbours(
            ClusterPoint sourcePoint, List<ClusterPoint> allPoints, Double threshold) {
        return allPoints.stream()
                .filter(
                        point ->
                                !sourcePoint.equals(point)
                                        && sourcePoint.getDataPoint().distance(point.getDataPoint())
                                                <= threshold)
                .collect(Collectors.toList());
    }
}
