package futility;

public class Circle {
    private Point centroid;
    private double radius;
    
    public Circle(Point centroid, double radius) {
        this.centroid = centroid;
        this.radius = radius;
    }

    /** Calculate the points of intersection between this circle and another
     * 
     * http://paulbourke.net/geometry/2circle/ was used as a math reference.
     * 
     * Note: In the extremely rare case that the circles are the same (and
     * therefore intersect at *every* point on the circle, we still return
     * an empty array of points because for this software, that cases should
     * be handled the same as non-intersection cases.
     * 
     * @param circle
     * @return
     */
    public Point[] intersectionPointsWith(Circle otherCircle) {
        if (!this.intersects(otherCircle) || this.contains(otherCircle) || otherCircle.contains(this) || this.isEqual(otherCircle)) {
            return new Point[] {};
        }
        else if (this.touches(otherCircle)){
            double dx = Math.cos(this.centroid.angleTo(otherCircle.centroid)) * this.getRadius();
            double dy = Math.sin(this.centroid.angleTo(otherCircle.centroid)) * this.getRadius();
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
    
    public boolean contains(Circle otherCircle){
        return this.centroid.distanceTo(otherCircle.centroid) + otherCircle.getRadius() <= this.getRadius();
    }
    
    public double getRadius() {
        return radius;
    }

    public boolean intersects(Circle otherCircle) {
        return this.getRadius() + otherCircle.getRadius() > this.centroid.distanceTo(otherCircle.centroid);
    }
    
    public boolean isEqual(Circle otherCircle) {
        return this.getRadius() == otherCircle.getRadius() && this.centroid.isEqual(otherCircle.centroid);
    }
    
    public boolean touches(Circle otherCircle) {
        return this.centroid.distanceTo(otherCircle.centroid) - this.getRadius() - otherCircle.getRadius() == 0;
    }
}
