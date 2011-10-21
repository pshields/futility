/** @file Point.java
 * Representation of an absolute-coordinate point on a 2D plane.
 * 
 * @author Team F(utility)
 */

package futility;

/**
 * Class representation of a point on a 2D plane, with helper functions for
 * finding distance and angles between Point objects.
 */
public class Point {
    private double x;
    private double y;
    
    /**
     * Default constructor, initializes this point to default values.
     */
    public Point() {
        x = Settings.INITIAL_POSITION.x;
        y = Settings.INITIAL_POSITION.y;
    }

    /**
     * Constructor, builds a Point object based on the provided coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /** 
     * Gets the angle between this Point and another Point object.
     * Uses the formula \f$angle = \arctan\left(\frac{y_2-y_1}{x_2-x_1}\right)\f$.
     * 
     * @param otherPoint the Point object to consider
     * @return the angle between this Point and otherPoint
     */
    public final double absoluteAngleTo(Point otherPoint) {
        double angle;
        double dx = this.deltaX(otherPoint);
        double dy = this.deltaY(otherPoint);
        // If the points have the same x-coordinate, arctangent will fail
        // We handle that case independently here
        if (dx == 0) {
            if (dy >= 0) {
                angle = 90;
            }
            else {
                angle = -90;
            }
        }
        // In all other cases, arctangent produces the correct angle
        else {
            angle = Math.toDegrees(Math.atan(dy/dx));
        }
        // Simply the angle
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }
    
    /**
     * Retrieves the closest Point object to this Point from the provided
     * collection of Point objects.
     * 
     * @param points array of Point objects to consider.
     * @return the Point object closest to this Point.
     */
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
    
    /**
     * Sets this Point's coordinates to the specified coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void update(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Copies the specified Point's coordinates to this Point.
     * 
     * @param point the Point object to copy coordinates from.
     */
    public void update(Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }
    
    /**
     * Gets the x-coordinate.
     * 
     * @return the x-coordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate.
     * 
     * @return the y-coordinate
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * Determines if this Point is equivalent to a specified Point object.
     * 
     * @param otherPoint the Point to compare this Point to.
     * @return true if the two Points have the same coordinates, otherwise false.
     */
    public boolean isEqual(Point otherPoint) {
        return this.getX() == otherPoint.getX() && this.getY() == otherPoint.getY();
    }
    
    /**
     * Retrieves the difference in x-coordinates between this Point and
     * another Point object. The formula used is \f$\Delta x = x_2 - x_1\f$.
     * 
     * @param otherPoint the Point object to compute against
     * @return Difference in x-coordinates
     */
    public double deltaX(Point otherPoint) {
        return otherPoint.getX() - this.getX();
    }
    
    /**
     * Retrieves the difference in x-coordinates between this Point and
     * another Point object. The formula used is \f$\Delta y = y_2 - y_1\f$.
     * 
     * @param otherPoint the Point object to compute against
     * @return Difference in x-coordinates
     */
    public double deltaY(Point otherPoint) {
        return otherPoint.getY() - this.getY();
    }
    
    /**
     * Retrieves the distance between this Point and another Point object.
     * The formula used is \f$dist = \sqrt{(\Delta x)^2 + (\Delta y)^2}\f$.
     * 
     * @param otherPoint the Point object to compute against
     * @return Distance between Point objects
     */
    public double distanceTo(Point otherPoint) {
        return Math.hypot(this.deltaX(otherPoint), this.deltaY(otherPoint));
    }
    
    /**
     * Builds a Point object representing the midpoint between this Point and
     * a specified Point object. The formula used is
     * \f$\left(x_n, y_n\right) = \left(x_1 + \frac{\Delta x}{2}, y_1 + \frac{\Delta y}{2}\right)\f$.
     * 
     * @param otherPoint the Point object to compute against
     * @return a Point object representing the midpoint
     */
    public Point midpointTo(Point otherPoint) {
        return new Point(this.getX() + this.deltaX(otherPoint) / 2, this.getY() + this.deltaY(otherPoint) /2);
    }
    
    /**
     * Builds a formatted textual representation of this Point object.
     * 
     * @return a formatted string representation
     */
    public String render() {
        return String.format("(%f, %f)", this.x - Settings.CENTER_FIELD.getX(),  - (this.y - Settings.CENTER_FIELD.getY()));
    }
}
