/** @file PositionEstimate.java
 * Position estimates with confidence values.
 * 
 * @author Team F(utility)
 */

package futility;

/**
 * An estimate of a position, with confidence.
 */
public class PositionEstimate extends Estimate {
    private Point position = new Point();
    
    /**
     * Default constructor.
     */
    public PositionEstimate() {
    }
    
    /**
     * Create an estimate by copying an existing estimate.
     * 
     * @param estimate the estimate to copy
     */
    public PositionEstimate(PositionEstimate estimate) {
        this.position.update(estimate.getPosition());
        this.initialConfidence = estimate.getInitialConfidence();
        this.timeEstimated = estimate.getTimeEstimated();
    }
    
    /**
     * PositionEstimate constructor.
     * 
     * @param p point representing the position
     * @param confidence the confidence in the value
     * @param time the soccer server time step in which the estimate is made
     */
    public PositionEstimate(Point p, double confidence, int time) {
        this.position.update(p);
        this.initialConfidence = confidence;
        this.timeEstimated = time;
    }
    
    /**
     * Full position estimate constructor.
     * 
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @param confidence the confidence in the value
     * @param time the soccer server time step in which the estimate is made
     */
    public PositionEstimate(double x, double y, double confidence, int time) {
        this.position.update(x, y);
        this.initialConfidence = confidence;
        this.timeEstimated = time;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // STATIC FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Returns a PositionEstimate with no confidence and an unknown value.
     * 
     * @return a PositionEstimate with no confidence and an unknown value
     */
    public final static PositionEstimate Unknown() {
        return PositionEstimate.Unknown(-1);
    }
    
    /**
     * Returns a PositionEstimate with no confidence and an unknown value.
     * 
     * @param time the soccer server time step in which the estimate is made
     * @return a PositionEstimate with no confidence and an unknown value
     */
    public final static PositionEstimate Unknown(int time) {
        return new PositionEstimate(Point.Unknown(), 0.0, time);
    }
    
    /**
     * Get the confidence at a given time step.
     * 
     * @param time the current time step
     * @return the confidence at that time step
     */
    public final double getConfidence(int time) {
        if (this.keepConfidenceForever()) {
            return this.initialConfidence;
        }
        else {
            double confidence = (3 * this.initialConfidence) / (3 + Math.abs(time - this.timeEstimated)); 
            return confidence;
        }
    }
    
    /**
     * Gets the position of the estimate.
     * 
     * @return the position of the estimate
     */
    public final Point getPosition() {
        return this.position;
    }

    /**
     * Gets the x-coordinate of the estimate's position.
     * 
     * @return the x-coordinate of the estimate's position
     */
    public final double getX() {
    	return this.position.getX();
    }
    
    /**
     * Gets the y-coordinate of the estimate's position.
     * 
     * @return the y-coordinate of the estimate's position
     */
    public final double getY() {
    	return this.position.getY();
    }
    

    /**
     * Renders the estimate as a string (mainly useful for debugging.)
     * 
     * @param time
     * @return a string representation of the estimate
     */
    public final String render(int time) {
        return this.position.render() + " with " + Double.toString(this.getConfidence(time)) + " confidence.";
    }
    
    /**
     * Updates the estimate.
     * 
     * @param p the new position
     * @param confidence a new confidence value
     * @param time the time of the estimate
     */
    public final void update(Point p, double confidence, int time) {
        this.update(p.getX(), p.getY(), confidence, time);
    }
    
    /**
     * Updates the estimate.
     * 
     * @param x the x-coordinate of the new position
     * @param y the y-coordinate of the new position
     * @param confidence a new confidence value
     * @param time the time of the estimate
     */
    public final void update(double x, double y, double confidence, int time) {
        Point oldPosition = new Point(this.position.getX(), this.position.getY());
        this.initialConfidence = confidence;
        this.timeEstimated = time;
        this.position.update(x, y);
        double distance = this.position.distanceTo(oldPosition);
        if (distance > 10) {
            Log.i("Updated position by " + Double.toString(distance));
        }
    }
}
