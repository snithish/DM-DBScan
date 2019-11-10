package ulb.bdma.dm.models;

import ulb.bdma.dm.contract.DistanceMeasurable;

public class ClusterPoint {
    private DistanceMeasurable dataPoint;
    private State state;

    public ClusterPoint(DistanceMeasurable dataPoint) {
        this.dataPoint = dataPoint;
        this.state = State.UNVISITED;
    }

    public DistanceMeasurable getDataPoint() {
        return dataPoint;
    }

    public State getState() {
        return state;
    }

    public void markAsVisited() {
        this.state = State.VISITED;
    }

    public boolean unvisited() {
        return this.state == State.UNVISITED;
    }
}
