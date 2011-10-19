package futility;

public class Estimate {
    protected double initialConfidence;
    protected int timeEstimated = -1;
    protected boolean keepConfidenceForever = false;
    
    public int getTimeEstimated() {
        return timeEstimated;
    }
}
