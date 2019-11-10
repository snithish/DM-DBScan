package ulb.bdma.dm;

import org.openjdk.jmh.annotations.*;
import ulb.bdma.dm.models.Point;

public class SampleBenchmark {

    @State(Scope.Thread)
    public static class PointState {
        Point somePoint = new Point(100.0, 10.0);
        Point otherPoint = new Point(95000.0, 78555.0);
    }

    @Benchmark
    @Fork(value = 2, warmups = 1)
    @BenchmarkMode(Mode.AverageTime)
    public void measureName(PointState state) {
        state.somePoint.distance(state.otherPoint);
    }
}
