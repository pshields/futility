/** @file Circle.java
 * A circle class facilitating player triangulation.
 * @author Team F(utility)
 */

package futility;

/** @class Circle
 * Circle class facilitating player triangulation.
 */
public class Circle {
    private Point centroid;
    private double radius;
    
    /**
     * Primary circle constructor.
     * 
     * @param centroid the center of the circle
     * @param radius the radius of the circle
     */
    public Circle(Point centroid, double radius) {
        this.centroid = centroid;
        this.radius = radius;
    }
    
    /**
     * Gets the distance to the specified point from the border of the circle.
     * 
     * @param point the given point
     * @return the distance to the given point from this circle's border
     */
    public double closestDistanceTo(Point point) {
        return this.centroid.distanceTo(point) - this.radius;
    }
    
    /**
     * Gets if this circle completely contains a given circle.
     * 
     * @param otherCircle the given circle
     * @return whether this circle completely contains the given circle
     */
    public boolean completelyContains(Circle otherCircle){
        return this.centroid.distanceTo(otherCircle.centroid) + otherCircle.getRadius() <= this.getRadius();
    }
    
    /**
     * Gets the point on the border of the circle at the given direction.
     * 
     * @param direction the direction in degrees at which to get the border point
     * @return the point at the border at the given direction
     */
    public Point getBorderPoint(double direction) {
        double x = this.centroid.getX();
        double y = this.centroid.getY();
        x += Math.cos(Math.toRadians(direction)) * this.radius;
        y += Math.sin(Math.toRadians(direction)) * this.radius;
        return new Point(x, y);
    }
    
    /**
     * Gets this circle's radius.
     * 
     * @return this circle's radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Calculates the points of intersection between this circle and another.
     * 
     * http://paulbourke.net/geometry/2circle/ was used as a math reference.
     * 
     * Note: In the extremely rare case that the circles are the same (and
     * therefore intersect at *every* point on the circle, we still return
     * an empty array of points because for this software, that cases should
     * be handled the same as non-intersection cases.
     * 
     * @param circle
     * @return an array of intersection points
     */
    public Point[] intersectionPointsWith(Circle otherCircle) {
        if (!this.intersects(otherCircle) || this.completelyContains(otherCircle) || otherCircle.completelyContains(this) || this.isEqual(otherCircle)) {
            return new Point[] {};
        }
        else if (this.touches(otherCircle)){
            double dx = Math.cos(Math.toRadians(this.centroid.angleTo(otherCircle.centroid))) * this.getRadius();
            double dy = Math.sin(Math.toRadians(this.centroid.angleTo(otherCircle.centroid))) * this.getRadius();
            Point point = new Point(this.centroid.getX() + dx, this.centroid.getY() + dy);
            return new Point[] {point};
        }
        else {
            // x0, y0 and x1, y1 represent the centroids of the two circles
            double x0 = this.centroid.getX();
            double y0 = this.centroid.getY();
            double x1 = otherCircle.centroid.getX();
            double y1 = otherCircle.centroid.getY();
            // r0 and r1 are the radiuses of the two circles
            double r0 = this.getRadius();
            double r1 = otherCircle.getRadius();
            // d is the distance between the two circle's centroids
            double d = this.centroid.distanceTo(otherCircle.centroid);
            // a is the distance from this circle's centroid to the center of the kite
            double a = (Math.pow(r0, 2) - Math.pow(r1, 2) + Math.pow(d, 2)) / (2 * d);
            // x2 and y2 are the center of the kite
            double x2 = x0 + a * (x1 - x0) / d;
            double y2 = y0 + a * (y1 - y0) / d;
            // h is the distance from a to either of the two points of intersection, p3
            double h = Math.sqrt(Math.pow(r0, 2) - Math.pow(a, 2));
            // dx and dy are the offset (+/-) to the two points
            double dx = h * (y1 - y0) / d;
            double dy = h * (x1 - x0) / d;
            // p3 and p4 are the points of intersection
            Point p3 = new Point(x2 + dx, y2 + dy);
            Point p4 = new Point(x2 - dx, y2 - dy);
            return new Point[] {p3, p4};
        }
    }
    
    /**
     * Gets whether this circle intersects a given circle in any part.
     * 
     * @param otherCircle the given circle
     * @return whether the two circles intersect at any part
     */
    public boolean intersects(Circle otherCircle) {
        return this.getRadius() + otherCircle.getRadius() > this.centroid.distanceTo(otherCircle.centroid);
    }
    
    /**
     * Gets whether this circle and a given circle are equal.
     * 
     * @param otherCircle the give circle
     * @return whether they are equal or not
     */
    public boolean isEqual(Circle otherCircle) {
        return this.getRadius() == otherCircle.getRadius() && this.centroid.isEqual(otherCircle.centroid);
    }
    
    /**
     * Gets whether this circle touches a given circle.
     * 
     * @param otherCircle the given circle
     * @return whether they touch or not
     */
    public boolean touches(Circle otherCircle) {
        return this.centroid.distanceTo(otherCircle.centroid) - this.getRadius() - otherCircle.getRadius() == 0;
    }
}
