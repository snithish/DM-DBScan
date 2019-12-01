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

    @Test
    void nameComplex() {
        ParallelDBScan parallelDBScan =
                new ParallelDBScan(
                        (float) 1.0,
                        2,
                        List.of(new Point(0.0, 0.0), new Point(0.0, 100.0), new Point(0.0, 1.0), new Point(0.0, 2.0)));
        parallelDBScan.cluster();
    }


    hash a = 1
            hash b = 10
                    a > b
    10 > 1
            true




    false



            a [b,c ]

            a, b a,c

            b [a]

            c [a]


    1 > 10
    10 > 1e
    e

    hash(a) > hashb  (1)
    b -> a (2)

}
