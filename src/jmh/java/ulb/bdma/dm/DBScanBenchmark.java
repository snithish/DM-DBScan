package ulb.bdma.dm;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ulb.bdma.dm.contract.DBScan;
import ulb.bdma.dm.contract.DistanceMeasurable;
import ulb.bdma.dm.impl.NaiveDBScan;
import ulb.bdma.dm.models.Point;

public class DBScanBenchmark {
    @State(Scope.Thread)
    public static class DataState {
        List<DistanceMeasurable> dataPoints;
        float epsilon;
        int minimumPoints;

        @Setup(Level.Trial)
        public void setUp() {
            Random random = new Random();
            dataPoints =
                    IntStream.rangeClosed(0, 20000)
                            .mapToObj(
                                    x ->
                                            new Point(
                                                    random.nextDouble() * 1000,
                                                    random.nextDouble() * 1000))
                            .collect(Collectors.toList());
            epsilon = random.nextFloat() * 50;
            minimumPoints = random.nextInt(10);
        }
    }

    @Benchmark
    @Warmup(iterations = 1)
    @Fork(value = 3, warmups = 1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureNaiveDBScan(Blackhole blackhole, DataState data) {
        DBScan dbScan = new NaiveDBScan(data.epsilon, data.minimumPoints, data.dataPoints);
        blackhole.consume(dbScan.cluster());
    }
}
