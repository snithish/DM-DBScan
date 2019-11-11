package ulb.bdma.dm.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import ulb.bdma.dm.contract.DBScan;
import ulb.bdma.dm.contract.DistanceMeasurable;
import ulb.bdma.dm.models.Point;

class NaiveDBScanTest {

    @Test
    void shouldReturnEmptyClustersWhenNoDataPoints() {
        DBScan naiveDBScan = new NaiveDBScan((float) 1.4, 3, Collections.EMPTY_LIST);

        List<List<DistanceMeasurable>> clusters = naiveDBScan.cluster();

        assertThat(clusters).isEmpty();
    }

    @Test
    void shouldReturnAClusterWithSinglePointWhenOnlyOnePointIsSent() {
        Point point = new Point(3.0, 4.0);
        DBScan naiveDBScan = new NaiveDBScan((float) 1.4, 3, List.of(point));

        List<List<DistanceMeasurable>> clusters = naiveDBScan.cluster();

        assertThat(clusters).containsOnly(List.of(point));
    }

    @Test
    void shouldReturnTreatDataPointAsNoiseWhenMinimumPointsNotPresentWithinEpsilon() {
        Point point = new Point(3.0, 4.0);
        Point closePoint = new Point(3.1, 4.0);
        DBScan naiveDBScan = new NaiveDBScan((float) 1.4, 3, List.of(point, closePoint));

        List<List<DistanceMeasurable>> clusters = naiveDBScan.cluster();

        assertThat(clusters).containsExactlyInAnyOrder(List.of(point), List.of(closePoint));
    }

    @Test
    void shouldConsiderTheSourcePointForMinimumNeighbourComputation() {
        Point point = new Point(3.0, 4.0);
        Point closePoint = new Point(3.1, 4.0);
        Point pointWithDistance = new Point(3.2, 4.5);
        DBScan naiveDBScan =
                new NaiveDBScan((float) 1.4, 3, List.of(point, closePoint, pointWithDistance));

        List<List<DistanceMeasurable>> clusters = naiveDBScan.cluster();

        assertThat(clusters)
                .containsExactlyInAnyOrder(List.of(point, closePoint, pointWithDistance));
    }
}
