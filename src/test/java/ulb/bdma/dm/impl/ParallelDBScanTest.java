package ulb.bdma.dm.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
                        List.of(
                                new Point(0.0, 0.0),
                                new Point(0.0, 100.0),
                                new Point(0.0, 1.0),
                                new Point(0.0, 2.0)));
        parallelDBScan.cluster();
    }

    @Test
    void namehash() {
        Set<Integer> a = new HashSet<>();
        a.add(1);
        a.add(2);
        Set<Integer> b = new HashSet<>();
        b.add(2);
        b.add(1);
        System.out.println(a.equals(b));
        System.out.println(a.hashCode());
        System.out.println(b.hashCode());
    }
}
