package futility;

// A point on a 2D plane
public class Point {
    private double x;
    private double y;
    
    public Point() {
        x = Settings.INITIAL_POSITION.x;
        y = Settings.INITIAL_POSITION.y;
    }
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /** Get the angle from the current object to another
     * 
     * Assumes base angle is this object's body angle, if not specified
     */
    public final double angleTo(Point otherPoint) {
        double angle = Math.atan(this.deltaY(otherPoint)/this.deltaX(otherPoint));
        if (Double.isNaN(angle)) {
            angle = 0;
        }
        return angle;
    }
    
    public final Point closestOf(Point[] points) {
        if (points.length == 0) {
            return this;
        }
        else {
            Point closest = points[0];
            for (int i=1; i < points.length; i++) {
                if (this.distanceTo(points[i]) < this.distanceTo(closest)) {
                    closest = points[i];
                }
            }
            return closest;
        }
    }
    
    public void update(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void update(Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }
    
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
    
    public boolean isEqual(Point otherPoint) {
        return this.getX() == otherPoint.getX() && this.getY() == otherPoint.getY();
    }
    
    public double deltaX(Point otherPoint) {
        return otherPoint.getX() - this.getX();
    }
    
    public double deltaY(Point otherPoint) {
        return otherPoint.getY() - this.getY();
    }
    
    public double distanceTo(Point otherPoint) {
        return Math.hypot(this.deltaX(otherPoint), this.deltaY(otherPoint));
    }
    
    public Point midpointTo(Point otherPoint) {
        return new Point(this.getX() + this.deltaX(otherPoint) / 2, this.getY() + this.deltaY(otherPoint) /2);
    }
    
    public String render() {
        return String.format("(%f, %f)", this.x - Settings.CENTER_FIELD.getX(), this.y - Settings.CENTER_FIELD.getY());
    }
}
