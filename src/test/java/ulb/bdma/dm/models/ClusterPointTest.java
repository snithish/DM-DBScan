package ulb.bdma.dm.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ulb.bdma.dm.contract.DistanceMeasurable;

@ExtendWith(MockitoExtension.class)
class ClusterPointTest {
    @Test
    void shouldExcludeSourcePoint() {
        DistanceMeasurable point = mock(DistanceMeasurable.class);
        ClusterPoint clusterPoint = new ClusterPoint(point);
        List<ClusterPoint> allPoints = Collections.singletonList(clusterPoint);
        List<ClusterPoint> actual = clusterPoint.getNearestNeighbours(allPoints, (float) 1.0);
        assertThat(actual.size()).isEqualTo(0);
    }

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

        List<ClusterPoint> actual = sourcePoint.getNearestNeighbours(allPoints, (float) 10.0);

        assertThat(actual).containsOnly(nearClusterPoint);
    }
}
