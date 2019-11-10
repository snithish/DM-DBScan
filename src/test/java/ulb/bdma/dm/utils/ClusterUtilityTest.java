package ulb.bdma.dm.utils;

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
class ClusterUtilityTest {
    @Test
    void shouldExcludeSourcePoint() {
        DistanceMeasurable point = mock(DistanceMeasurable.class);
        List<DistanceMeasurable> allPoints = Collections.singletonList(point);
        List<DistanceMeasurable> actual =
                ClusterUtility.getNearestNeighbours(point, allPoints, 1.0);
        assertThat(actual.size()).isEqualTo(0);
    }

    @Test
    void shouldReturnDataPointsWithDistance() {
        DistanceMeasurable point = mock(DistanceMeasurable.class);
        DistanceMeasurable nearPoint = mock(DistanceMeasurable.class);
        DistanceMeasurable farPoint = mock(DistanceMeasurable.class);
        List<DistanceMeasurable> allPoints = List.of(point, nearPoint, farPoint);

        when(point.distance(nearPoint)).thenReturn(9.9999);
        when(point.distance(farPoint)).thenReturn(10.00001);

        List<DistanceMeasurable> actual =
                ClusterUtility.getNearestNeighbours(point, allPoints, 10.0);

        assertThat(actual).containsOnly(nearPoint);
    }
}
