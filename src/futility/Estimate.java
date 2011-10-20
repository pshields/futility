package futility;

public class Estimate {
    protected double initialConfidence;
    protected int timeEstimated = -1;
    protected boolean keepConfidenceForever = false;
    
    public double getInitialConfidence() {
        return this.initialConfidence;
    }
    
    public boolean getKeepConfidenceForever() {
        return this.keepConfidenceForever;
    }
    
    public int getTimeEstimated() {
        return this.timeEstimated;
    }
}
