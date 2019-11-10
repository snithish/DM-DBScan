package ulb.bdma.dm.contract;

/**
 * Classes implementing this interface can be clustered
 */
public interface DistanceMeasurable {
    /**
     * Compute distance between data points
     * @param other The point against which distance is to be measured
     * @return Distance between data points
     */
    Double distance(DistanceMeasurable other);
}
