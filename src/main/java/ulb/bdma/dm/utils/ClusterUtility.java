package ulb.bdma.dm.utils;

import java.util.List;
import java.util.stream.Collectors;
import ulb.bdma.dm.contract.DistanceMeasurable;

public class ClusterUtility {
    public static List<DistanceMeasurable> getNearestNeighbours(
            DistanceMeasurable sourcePoint, List<DistanceMeasurable> allPoints, Double threshold) {
        return allPoints.stream()
                .filter(
                        point ->
                                !sourcePoint.equals(point)
                                        && sourcePoint.distance(point) <= threshold)
                .collect(Collectors.toList());
    }
}
