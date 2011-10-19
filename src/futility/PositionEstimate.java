package futility;

public class PositionEstimate extends Estimate {
    private Point position = new Point();
    
    public PositionEstimate() {
    }
    
    public PositionEstimate(double x, double y, double confidence, boolean keepConfidenceForever) {
        this.position.update(x, y);
        this.initialConfidence = confidence;
        this.keepConfidenceForever = keepConfidenceForever;
    }
    
    public final double getConfidence(int time) {
        if (this.keepConfidenceForever) {
            return this.initialConfidence;
        }
        else {
            return this.initialConfidence * (3 / (3 + Math.abs(time - this.timeEstimated)));
        }
    }
    
    public final Point getPosition() {
        return position;
    }
    
    public final void setPosition(double x, double y) {
        this.position.update(x, y);
    }
    
    public final void setPosition(Point point) {
        this.position.update(point);
    }
    
    public final void update(double x, double y, double confidence, int time) {
        this.initialConfidence = confidence;
        this.timeEstimated = time;
        this.position.update(x, y);
    }
}
