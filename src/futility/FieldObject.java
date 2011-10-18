// Represents a physical object positioned somewhere on the field. Used by the
// client to model states.

package futility;

public abstract class FieldObject extends GameObject {

    public double distanceTo = 0;
    public double lastSeenAngleTo = 0; // Assume things face east, degrees
    public double distanceChange = 0;
    public DirectionEstimate direction = new DirectionEstimate(0, true);
    public double directionChange = 0;
    public double bodyFacing = 0; //degrees
    public double headFacing = 0; 
    public Point position;
    public String id = "UNKNOWN_NO_ID";
    public int timeLastSeen = -1;
    public double angleLastSeen = -1;
    
    public FieldObject() {
        position = new Point();
    }
    
    public FieldObject(double x, double y) {
        position = new Point(x, y);
    }
    
    public FieldObject(FieldObject copy){
    	copyFieldObject(copy);
    }
    
    public void copyFieldObject(FieldObject copy){
    	this.directionTo = copy.directionTo;
    	this.distanceTo = copy.distanceTo;
    	this.position.x = copy.position.x;
    	this.position.y = copy.position.y;
    	this.distanceChange = copy.distanceChange;
    	this.directionChange = copy.directionChange;
    	this.bodyFacingDir = copy.bodyFacingDir;
    	this.headFacingDir = copy.headFacingDir;
    	this.id = copy.id;
    	this.timeLastSeen = copy.timeLastSeen;
    }
    
    /** Get the angle from the current object to another
     * 
     * Assumes base angle is this object's body angle, if not specified
     */
    public final double angleTo(FieldObject object) {
        double angle = Math.atan(deltaY(object)/deltaX(object)) - this.direction.getDirection();
        if (Double.isNaN(angle)) {
            angle = 0;
        }
        return angle;
    }
    
    /** Return a circle for triangulation
     * 
     * The circle's radius is the last known distance to the object.
     * 
     * @return
     */
    public final Circle asCircle() {
        return new Circle(this.position, this.distanceTo);
    }
    
    /** Get the angle from the current object to a global angle
     * 
     * @param direction the global angle
     * @return the angle in degrees to turn in order to face the global angle
     */
    public final double angleTo(double direction) {
        double deltaAngle = direction - this.direction.getDirection();
        while (deltaAngle < 0) {
            deltaAngle += 360;
        }
        while (deltaAngle > 360) {
            deltaAngle -= 360;
        }
        return deltaAngle;
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
        return object.position.getX() - this.position.getX();
    }
    
    /** Get the difference in y coordinates from the current object to another
     * 
     * @return the difference in y coordinates from the current object to another
     */
    public double deltaY(FieldObject object) {
        return object.position.getY() - this.position.getY();
    }
    
    /** Check if the field object is within a given rectangle boundary
     * 
     * @param rectangle a Rectangle object to check if the object is in
     * @return true if in the specified Rectangle
     */
    public boolean inRectangle(Rectangle rectangle) {
        return rectangle.contains(this);
    }
    
    public boolean isStationaryObject() {
        return false;
    }
}
