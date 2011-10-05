// Represents a physical object positioned somewhere on the field. Used by the
// client to model states.

package futility;

public abstract class FieldObject extends GameObject {
    double angle = 0; // Assume all things face east.
    Point position;
    int timeLastSeen = -1;
    
    public FieldObject() {
        position = new Point();
    }
    
    public FieldObject(double x, double y) {
        position = new Point(x, y);
    }
    
    /** Get the angle from the current object to another
     * 
     * Assumes base angle is this object's body angle, if not specified
     */
    public double angleTo(FieldObject object) {
        double angle = Math.atan(deltaY(object)/deltaX(object)) - this.angle;
        if (Double.isNaN(angle)) {
            angle = 0;
        }
        return angle;
    }
    
    /** Get the distance from the current object to another
     * 
     * @param object
     * @return the distance from the current object to another
     */
    public double distanceTo(FieldObject object) {
        return Math.hypot(deltaX(object), deltaY(object)); 
    }
    
    /** Get the difference in x coordinates from the current object to another
     * 
     * @return the difference in x coordinates from the current object to another
     */
    public double deltaX(FieldObject object) {
        return object.position.x - this.position.x;
    }
    
    /** Get the difference in y coordinates from the current object to another
     * 
     * @return the difference in y coordinates from the current object to another
     */
    public double deltaY(FieldObject object) {
        return object.position.y - this.position.y;
    }
    
    public boolean inRectangle(Rectangle rectangle) {
        return rectangle.contains(this);
    }
}
