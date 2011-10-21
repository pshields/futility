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
    protected int timeEstimated = -1;
    protected boolean keepConfidenceForever = false;
    
    /**
     * Gets the confidence as originally provided.
     * 
     * @return the confidence as originally provided
     */
    public double getInitialConfidence() {
        return this.initialConfidence;
    }
    
    /**
     * Gets whether or not this estimate keeps its confidence forever.
     * 
     * @return whether or not this estimate keeps its confidence forever
     */
    public boolean getKeepConfidenceForever() {
        return this.keepConfidenceForever;
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
