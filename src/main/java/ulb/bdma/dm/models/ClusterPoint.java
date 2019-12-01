package ulb.bdma.dm.models;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import ulb.bdma.dm.contract.DistanceMeasurable;

public class ClusterPoint {
    private UUID id;
    private boolean assignedToCluster;
    private DistanceMeasurable dataPoint;
    private State state;

    public ClusterPoint(DistanceMeasurable dataPoint) {
        id = UUID.randomUUID();
        this.dataPoint = dataPoint;
        this.state = State.UNVISITED;
    }

    public List<ClusterPoint> getNearestNeighbours(List<ClusterPoint> allPoints, float threshold) {
        return allPoints.stream()
                .filter(point -> this.getDataPoint().distance(point.getDataPoint()) <= threshold)
                .collect(Collectors.toList());
    }

    public DistanceMeasurable getDataPoint() {
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

    public boolean visited() {
        return this.state == State.VISITED;
    }

    public boolean unvisited() {
        return this.state == State.UNVISITED;
    }

    public boolean isAssignedToCluster() {
        return assignedToCluster;
    }

    public void assignToCluster() {
        this.assignedToCluster = true;
    }

    public boolean isNoise() {
        return this.state == State.NOISE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterPoint that = (ClusterPoint) o;
        return assignedToCluster == that.assignedToCluster
                && id.equals(that.id)
                && dataPoint.equals(that.dataPoint)
                && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataPoint);
    }
}
