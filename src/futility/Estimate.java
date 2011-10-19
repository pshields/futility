package futility;

public class Estimate {
    protected double initialConfidence;
    protected int timeEstimated = -1;
    protected boolean keepConfidenceForever = true;
    
    public int getTimeEstimated() {
        return timeEstimated;
    }
}
