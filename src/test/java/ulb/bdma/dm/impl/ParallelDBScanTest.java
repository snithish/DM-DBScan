package ulb.bdma.dm.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import ulb.bdma.dm.models.Point;

class ParallelDBScanTest {
    @Test
    void name() {
        ParallelDBScan parallelDBScan =
                new ParallelDBScan(
                        (float) 1.0,
                        2,
                        List.of(new Point(0.0, 0.0), new Point(0.0, 1.0), new Point(0.0, 2.0)));
        parallelDBScan.cluster();
    }
}
