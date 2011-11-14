/** @file DirectionEstimate.java
 * Confidence in directions.
 * 
 * @author Team F(utility)
 */

package futility;

/**
 * An estimate of a direction, with confidence.
 */
public class DirectionEstimate extends Estimate {
    private double direction;

    /**
     * Default constructor.
     */
    public DirectionEstimate() {
        direction = 0;
        initialConfidence = 0;
    }
    
    /**
     * Constructor to copy another direction estimate.
     * 
     * @param estimate the other direction estimate
     */
    public DirectionEstimate(DirectionEstimate estimate) {
        this.direction = estimate.getDirection();
        this.initialConfidence = estimate.getInitialConfidence();
        this.timeEstimated = estimate.getTimeEstimated();
    }
    
    /**
     * Constructor taking a direction and whether or not to keep the confidence
     * forever (useful for stationary objects.)
     * 
     * @param direction the direction of the estimate
     * @param forever whether to keep the confidence forever
     */
    public DirectionEstimate(double direction, boolean forever) {
        if (forever) {
            this.setForever(direction);
        }
        else {
            this.direction = direction;
        }
    }
    
    /**
     * Gets the confidence value of the estimate for the given time step.
     * 
     * @param time the time step for which to estimate the confidence
     * @return a measure of the confidence in the direction value
     */
    public double getConfidence(int time) {
        if (this.keepConfidenceForever()) {
            return initialConfidence;
        }
        else {
            double confidence = (5 * this.initialConfidence) / (5 + Math.abs(time - this.timeEstimated)); 
            return confidence;
        }
    }
    
    /**
     * Gets the direction of the estimate.
     * 
     * @return the direction of the estimate
     */
    public double getDirection() {
        return this.direction;
    }
    
    /** 
     * Sets a direction permanently with complete certainty.
     * 
     * This can be used to set the direction of things which only have
     * direction by convention, such as stationary objects that we say
     * always face east.
     * 
     * @param direction
     */
    public void setForever(double direction) {
        this.direction = direction;
        this.initialConfidence = 1.0;
        this.timeEstimated = -1;
    }
    
    /**
     * Updates an estimate with new direction and confidence.
     * 
     * @param direction a new direction value
     * @param confidence a confidence value
     * @param time
     */
    public void update(double direction, double confidence, int time) {
        this.direction = normalizeDirection(direction);
        this.initialConfidence = confidence;
        this.timeEstimated = time;
    }
    
    /**
     * Converts a direction into an equivalent direction between -180 and 180 degrees.
     * 
     * @param direction an original direction
     * @return an equivalent direction between -180 and 180 degrees
     */
    public double normalizeDirection(double direction) {
        while (direction < -180) {
            direction += 360;
        }
        while (direction > 180) {
            direction -= 360;
        }
        return direction;
    }
    
    /**
     * Renders a description of the estimate as a string.
     * 
     * @param time the current time step
     * @return a string representing the estimate
     */
    public String render(int time) {
        return Double.toString(this.direction) + " degrees with " + this.getConfidence(time) + " confidence";
    }
}
