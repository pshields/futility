package futility;

public class DirectionEstimate {
    private double direction;
    private double initialConfidence;
    private int timeStepEstimated = -1;
    private boolean keepConfidenceForever = true;
    
    public DirectionEstimate() {
        direction = 0;
        initialConfidence = 0;
    }
    
    public DirectionEstimate(double direction, boolean forever) {
        if (forever) {
            this.setForever(direction);
        }
        else {
            this.direction = direction;
        }
    }
    
    public DirectionEstimate(double direction, int time) {
        this.direction = direction;
        this.timeStepEstimated = time;
    }
    
    public double getConfidence(int time) {
        if (keepConfidenceForever) {
            return initialConfidence;
        }
        else return 5 * initialConfidence / (5 + Math.abs(time - timeStepEstimated));
    }
    
    public double getDirection() {
        return this.direction;
    }
    
    /** Set a direction permanently with complete certainty
     * 
     * This can be used to set the direction of things which only have
     * direction by convention, such as stationary objects that we say
     * always face east.
     * 
     * @param direction
     */
    public void setForever(double direction) {
        this.direction = direction;
        this.initialConfidence = 1;
        keepConfidenceForever = false;
    }
    
    public void update(double direction, double confidence, int time) {
        this.direction = normalizeDirection(direction);
        this.timeStepEstimated = time;
    }
    
    public double normalizeDirection(double direction) {
        while (direction < -180) {
            direction += 360;
        }
        while (direction > 180) {
            direction -= 360;
        }
        return direction;
    }
}
