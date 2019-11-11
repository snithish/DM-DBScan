package ulb.bdma.dm.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

    @Test
    void shouldExpandClusterWhenBorderPointIsAlsoACorePoint() {
        Point point = new Point(0.0, 0.0);
        Point closePoint =
                new Point(
                        1.0,
                        0.0); // another core point with neighbors as point and closeToBorderPoint
        Point anotherClosePoint = new Point(-1.0, 0.0);
        Point closeToBorderPoint = new Point(2.0, 0.0);

        DBScan naiveDBScan =
                new NaiveDBScan(
                        (float) 1.0,
                        3,
                        new ArrayList<>(
                                Set.of(point, closePoint, anotherClosePoint, closeToBorderPoint)));

        List<List<DistanceMeasurable>> clusters = naiveDBScan.cluster();

        assertThat(clusters.size()).isEqualTo(1);
        assertThat(clusters.get(0))
                .containsExactlyInAnyOrderElementsOf(
                        List.of(point, closePoint, anotherClosePoint, closeToBorderPoint));
    }

    /**
     * A graph to illustrate the inputs for easy readability
     *
     * <pre>
     *         +
     *         |
     *         |
     *         |
     *         |
     *         |
     *         |          distance = 1                       distance = 1
     *         |<-----------------------------------> <------------------------->
     *         |closeToPointAUp(0,0.1)                                         closeToPointBUp(2,0.1)
     * +---------------------------------------borderPoint(1,0)----------------pointB(2,0)
     *         |pointA(0,0)                                                    closeToPointBDown(2,-0.1)
     *         |closeToPointADown(0,-0.1)
     *         |
     *         |
     *         |
     *         |
     *         |
     *         |
     *         |
     *         |
     *         |
     *         +
     *  </pre>
     */
    @Test
    void shouldPutBorderPointBetweenTwoClusterOnlyInOneOfTheCluster() {
        Point pointA = new Point(0.0, 0.0);
        Point closeToPointAUp = new Point(0.0, 0.1);
        Point closeToPointADown = new Point(0.0, -0.1);
        Point borderPoint = new Point(1.0, 0.0);
        Point pointB = new Point(2.0, 0.0);
        Point closeToPointBUp = new Point(2.0, 0.1);
        Point closeToPointBDown = new Point(2.0, -0.1);

        List<DistanceMeasurable> allPoints =
                new ArrayList<>(
                        Set.of(
                                pointB,
                                pointA,
                                closeToPointAUp,
                                closeToPointADown,
                                borderPoint,
                                closeToPointBUp,
                                closeToPointBDown));
        DBScan naiveDBScan = new NaiveDBScan((float) 1.0, 4, allPoints);

        List<List<DistanceMeasurable>> clusters = naiveDBScan.cluster();

        assertThat(clusters.size()).isEqualTo(2);
        List<List<DistanceMeasurable>> clustersContainingBorderPoint =
                clusters.stream().filter(x -> x.contains(borderPoint)).collect(Collectors.toList());
        assertThat(clustersContainingBorderPoint).hasSize(1);
        assertThat(clustersContainingBorderPoint.get(0)).hasSize(4);
    }
}
