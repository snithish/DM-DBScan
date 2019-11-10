package ulb.bdma.dm.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PointTest {

    @Test
    void distanceIsEuclidean() {
        Point point = new Point(0.0, 0.0);
        Point other = new Point(1.0, 0.0);
        Double actualDistance = point.distance(other);
        assertEquals(1.0, actualDistance);
    }
}
