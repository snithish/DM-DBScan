package ulb.bdma.dm.models;

import java.util.List;
import java.util.stream.Collectors;
import ulb.bdma.dm.contract.DistanceMeasurable;

public class ClusterPoint {
    private DistanceMeasurable dataPoint;
    private State state;

    public ClusterPoint(DistanceMeasurable dataPoint) {
        this.dataPoint = dataPoint;
        this.state = State.UNVISITED;
    }

    public List<ClusterPoint> getNearestNeighbours(List<ClusterPoint> allPoints, float threshold) {
        return allPoints.stream()
                .filter(
                        point ->
                                !this.equals(point)
                                        && this.getDataPoint().distance(point.getDataPoint())
                                                <= threshold)
                .collect(Collectors.toList());
    }

    private DistanceMeasurable getDataPoint() {
        return dataPoint;
    }

    public State getState() {
        return state;
    }

    public void visit() {
        this.state = State.VISITED;
    }

    public void noise() {
        this.state = State.NOISE;
    }

    public boolean unvisited() {
        return this.state == State.UNVISITED;
    }
}
