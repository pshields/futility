/** @file Estimate.java
 * Estimate values with confidence.
 * 
 * @author Team F(utility)
 */

package futility;

/**
 * An estimate of a value.
 */
public class Estimate {
    protected double initialConfidence;
    protected int timeEstimated;
    
    /**
     * Gets the confidence as originally provided.
     * 
     * @return the confidence as originally provided
     */
    public double getInitialConfidence() {
        return this.initialConfidence;
    }
    
    /**
     * Returns whether this estimate should keeps its confidence forever.
     * By convention, this happens when timeEstimated < 0.
     * 
     * @return whether or not this estimate keeps its confidence forever
     */
    public boolean keepConfidenceForever() {
        return this.timeEstimated < 0;
    }
    
    /**
     * Gets the time this estimate was made.
     * 
     * @return an integer time step
     */
    public int getTimeEstimated() {
        return this.timeEstimated;
    }
}
