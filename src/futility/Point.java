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
        x = Double.NaN;
        y = Double.NaN;
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
    
    ///////////////////////////////////////////////////////////////////////////
    // STATIC FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Returns a Point with NaN values.
     * 
     * @return a Point with NaN values.
     */
    public final static Point Unknown() {
        return new Point(Double.NaN, Double.NaN);
    }
    
    /** 
     * Gets the angle between this Point and another Point object.
     * Uses the formula \f$angle = \arctan\left(\frac{y_2-y_1}{x_2-x_1}\right)\f$.
     * 
     * @param otherPoint the Point object to consider
     * @return the angle between this Point and otherPoint
     */
    public final double absoluteAngleTo(Point otherPoint) {
        double dx = this.deltaX(otherPoint);
        double dy = this.deltaY(otherPoint);
        // Handle Math.atan() failure case
        if (dx == 0.0) {
            if (dy >= 0.0) {
                return 90.0;
            }
            else {
                return -90.0;
            }
        }
        double angle = Math.toDegrees(Math.atan(dy/dx));
        if (dx > 0) {
            return angle;
        }
        else {
            return 180.0 + angle;
        }
    }
    
    /**
     * Returns a 2D vector representing the point.
     * 
     * @return a 2D vector representing the point
     */
    public final Vector2D asVector() {
        return new Vector2D(this.x, this.y);
    }
    
    /**
     * Returns true if either of this point's x and y are unknown.
     * 
     * @return true if either of this point's x and y are unknown
     */
    public final boolean isUnknown() {
        return Double.isNaN(this.x) || Double.isNaN(this.y);
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
     * Returns the midpoint between this point and another.
     * 
     * @param p the other point
     * @return the midpoint between this point and the other
     */
    public final Point midpointTo(Point p) {
        return new Point( (this.x + p.getX()) / 2.0, (this.y + p.getY()) / 2.0);
    }
    
    /**
     * Builds a formatted textual representation of this Point object.
     * 
     * @return a formatted string representation
     */
    public String render() {
        return String.format("(%f, %f)", this.x, this.y);
    }
}
