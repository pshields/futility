// Represents a physical object positioned somewhere on the field. Used by the
// client to model states.

package futility;

public abstract class FieldObject extends GameObject {
    public double distanceTo = 0;
    public double lastSeenAngleTo = 0; // Assume things face east, degrees
    public double distanceChange = 0;
    public DirectionEstimate direction = new DirectionEstimate();
    public double directionChange = 0;
    public double bodyFacing = 0; //degrees
    public double headFacing = 0; 
    public PositionEstimate position = new PositionEstimate();
    public String id = "UNKNOWN_ID";
    
    // Variables used by the player with the brain
    public int timeLastSeen = -1;
    public DirectionEstimate angleToLastSeen = new DirectionEstimate();
    public double distanceToLastSeen = -1;
    
    public FieldObject() {
    }
    
    /**
     * Initializes a field object at the given coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public FieldObject(double x, double y) {
        position.setPosition(x, y);
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
    
    public final double absoluteAngleTo(FieldObject object) {
        double angle;
        double dx = this.deltaX(object);
        double dy = this.deltaY(object);
        // If the objects have the same x-coordinate, arctangent will fail
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
     * Gets the offset from the current object's direction to another object.
     * 
     * @return an offset in degrees from this object's direction to the other
     * object
     */
    public final double relativeAngleTo(FieldObject object) {
        double angle = this.absoluteAngleTo(object) - this.direction.getDirection();
        // Simplify the angle
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }
    
    /**
     * Gets a circle for triangulation. The circle's radius is the last known
     * distance to the object.
     * 
     * @return a circle with radius equal to the last known distance to this 
     * object from the player with the brain
     */
    public final Circle asCircle() {
        return new Circle(this.position.getPosition(), this.distanceTo);
    }
    
    /**
     * Gets the distance from this object to the given field object.
     * 
     * @param object the given field object
     * @return the distance from the this object to the given field object
     */
    public double distanceTo(FieldObject object) {
        double dx = this.deltaX(object);
        double dy = this.deltaY(object);
        return Math.hypot(dx, dy); 
    }
    
    /**
     * Gets the difference in x coordinates from this object to the given object.
     * 
     * @return the difference in x coordinates from this object to the given object.
     */
    public double deltaX(FieldObject object) {
        double x0 = this.position.getPosition().getX();
        double x1 = object.position.getPosition().getX();
        return x1 - x0;
    }
    
    /**
     * Gets the difference in y coordinates from this object to the given object.
     * 
     * @return the difference in y coordinates from this object to the given object
     */
    public double deltaY(FieldObject object) {
        double y0 = this.position.getPosition().getY();
        double y1 = object.position.getPosition().getY();
        return y1 - y0;
    }
    
    /**
     * Gets if this object is within the given rectangle boundary.
     * 
     * @param rectangle a rectangle to check if this object is in
     * @return true if this object is in the rectangle
     */
    public boolean inRectangle(Rectangle rectangle) {
        return rectangle.contains(this);
    }
    
    /**
     * Gets whether this object is a StationaryObject.
     * @return whether it is or not
     */
    public boolean isStationaryObject() {
        return false;
    }
    
    /**
     * Gets the angle between this object's direction and the given direction.
     * 
     * @param direction an angle in degrees on the standard unit circle
     * @return an offset which could be added to this object's direction to
     * yield the given direction
     */
    public final double relativeAngleTo(double direction) {
        double offset = direction - this.direction.getDirection();
        // Simplify the offset so that it is within [-180, 180] degrees
        while (offset > 180) {
            offset -= 360;
        }
        while (offset < -180) {
            offset += 360;
        }
        return offset;
    }
}
