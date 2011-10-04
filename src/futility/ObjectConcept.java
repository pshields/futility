// Represents a physical object positioned somewhere on the field. Used by the
// client to model states.

package futility;

abstract class ObjectConcept {
    // The origin is arbitrarily chosen to be the farthest bottom-left point
    // (given standard display conventions) that an object can occupy.
    public double x = -1.0;
    public double y = -1.0;
    
    public double angle = 0.0;
    
    /** Get the angle from the current object to another
     * 
     * Assumes base angle is this object's body angle, if not specified
     */
    public double angleTo(ObjectConcept object) {
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
    public double distanceTo(ObjectConcept object) {
        return Math.hypot(deltaX(object), deltaY(object)); 
    }
    
    /** Get the difference in x coordinates from the current object to another
     * 
     * @return the difference in x coordinates from the current object to another
     */
    public double deltaX(ObjectConcept object) {
        return object.x - this.x;
    }
    
    /** Get the difference in y coordinates from the current object to another
     * 
     * @return the difference in y coordinates from the current object to another
     */
    public double deltaY(ObjectConcept object) {
        return object.y - this.y;
    }
    
    public boolean inRectangle(Rectangle rectangle) {
        return rectangle.contains(this);
    }
}
