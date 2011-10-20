package futility;

public class PositionEstimate extends Estimate {
    private Point position = new Point();
    
    public PositionEstimate() {
    }
    
    public PositionEstimate(PositionEstimate estimate) {
        this.position.update(estimate.getPosition());
        this.initialConfidence = estimate.getInitialConfidence();
        this.timeEstimated = estimate.getTimeEstimated();
        this.keepConfidenceForever = estimate.getKeepConfidenceForever();
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
    
    public final String render(int time) {
        return this.position.render() + " with " + Double.toString(this.getConfidence(time)) + " confidence.";
    }
    
    public final void setPosition(double x, double y) {
        this.position.update(x, y);
    }
    
    public final void setPosition(Point point) {
        this.position.update(point);
    }
    
    public final void update(Point p, double confidence, int time) {
        this.update(p.getX(), p.getY(), confidence, time);
    }
    
    public final void update(double x, double y, double confidence, int time) {
        Point oldPosition = new Point(this.position.getX(), this.position.getY());
        this.initialConfidence = confidence;
        this.timeEstimated = time;
        this.position.update(x, y);
        double distance = this.position.distanceTo(oldPosition);
        if (distance > 10) {
            System.err.println("Updated position by " + Double.toString(distance));
        }
    }
}
