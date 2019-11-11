package ulb.bdma.dm.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ulb.bdma.dm.contract.DistanceMeasurable;

@ExtendWith(MockitoExtension.class)
class ClusterPointTest {
    @Test
    void shouldReturnDataPointsWithDistance() {
        DistanceMeasurable point = mock(DistanceMeasurable.class);
        DistanceMeasurable nearPoint = mock(DistanceMeasurable.class);
        DistanceMeasurable farPoint = mock(DistanceMeasurable.class);
        ClusterPoint sourcePoint = new ClusterPoint(point);
        ClusterPoint nearClusterPoint = new ClusterPoint(nearPoint);

        List<ClusterPoint> allPoints =
                List.of(sourcePoint, nearClusterPoint, new ClusterPoint(farPoint));

        when(point.distance(nearPoint)).thenReturn(9.9999);
        when(point.distance(farPoint)).thenReturn(10.00001);
        when(point.distance(point)).thenReturn(0.0);

        List<ClusterPoint> actual = sourcePoint.getNearestNeighbours(allPoints, (float) 10.0);

        assertThat(actual).containsExactlyInAnyOrder(nearClusterPoint, sourcePoint);
    }
}
