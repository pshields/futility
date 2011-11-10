/** @file FieldObject.java
 * Represents a physical object positioned somewhere on the field. Used by the
 * client to model states.
 * 
 * @author Team F(utility)
 */ 

package futility;

/**
 * Represents an object on the playing field.
 */
public abstract class FieldObject extends GameObject {
    public SeeInfo info = new SeeInfo();  // the last see info received about the object
    public DirectionEstimate direction = new DirectionEstimate();
    public PositionEstimate position = new PositionEstimate();
    public String id = "UNKNOWN_ID";
    
    /**
     * This default constructor initializes the field object with default values.
     */
    public FieldObject() {
    }
    
    /**
     * Initializes a field object at the given coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public FieldObject(double x, double y) {
        this.position.update(x, y, 1.0, -1);
    }
    
    /**
     * Creates the appropriate field object or subclass thereof, given a valid object id.
     * 
     * @param id the object's id
     * @return the corresponding FieldObject
     */
    public static final FieldObject create(String id) {
        if(id.startsWith("(b")){
            return new Ball();
        }
        else if(id.startsWith("(p")){
            return new Player(id);
        }
        else if(id.startsWith("(l")){
            //TODO return whatever an l is
            return null;
        }
        else if(id.startsWith("(B")){
            //TODO return whatever a B is
            return null;
        }
        else if(id.startsWith("(F")){
            //TODO return whatver an F is
            return null;
        }
        else if(id.startsWith("(G")){
            //TODO return whatever a G is
            return null;
        }
        else if(id.startsWith("(P")){
            //TODO return whatever a P is
            return null;
        }
        else{
            Log.e("invalid name detected for see parse");
            return null;
        }
    }
    
    /**
     * Calculates the absolute angle from this object to the given field
     * object.
     * 
     * @param object the given field object to calculate an angle against.
     * @return the angle to the object in degrees
     */
    public final double absoluteAngleTo(FieldObject object) {
        return this.position.getPosition().absoluteAngleTo(object.position.getPosition());
    }
    
    /**
     * Gets the offset from the current object's direction to another object.
     * 
     * @return an offset in degrees from this object's direction to the other
     * object
     */
    public final double relativeAngleTo(FieldObject object) {
        double angle = this.absoluteAngleTo(object) - this.direction.getDirection();
        return Futil.simplifyAngle(angle);
    }
    
    /**
     * Gets the distance from this object to the given field object.
     * Uses the formula
     * 
     * \f$dist = \sqrt{(x_2-x_1)^2 + (y_2-y_1)^2}\f$.
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
     * Gets the difference in x coordinates from this object to the given
     * object.
     * 
     * @return the difference in x coordinates from this object to the given
     * object
     */
    public double deltaX(FieldObject object) {
        double x0 = this.position.getPosition().getX();
        double x1 = object.position.getPosition().getX();
        return x1 - x0;
    }
    
    /**
     * Gets the difference in y coordinates from this object to the given
     * object.
     * 
     * @return the difference in y coordinates from this object to the given
     * object
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
        double angle = direction - this.direction.getDirection();
        return Futil.simplifyAngle(angle);
    }
    
    /**
     * Updates this field object's last see info.
     * 
     * @param player the player whose brain is modeling this object
     * @param info the object's info from the `see` message
     * @param time the soccer server time from the `see` message
     */
    public final void update(Player player, String info, int time) {
        this.info.reset();
        this.info.time = time;
        String[] args = Futil.extractArgs(info);
        int offset = 0;  // indicates number of optional parameters read so far
        if (args.length > 3 && args[args.length - 1].equals("t")) {
            this.info.tackling = true;
            offset++;
        }
        else if (args.length > 3 && args[args.length - 1].equals("k")) {
            this.info.kicking = true;
            offset++;
        }
        // If there was more than one argument and the number of arguments plus the offset mod 2
        // is equal to 1, then the last argument (minus the current offset) must be the pointingDir
        // argument.
        if (args.length >= 3 && (args.length + offset) % 2 == 1) {
            this.info.pointingDir = Double.valueOf(args[args.length - 1 - offset]);
            offset++;
        }
        switch(args.length - offset) {
        case 6:
            this.info.headFacingDir = Double.valueOf(args[5]);
        case 5:
            this.info.bodyFacingDir = Double.valueOf(args[4]);
        case 4:
            this.info.dirChange = Double.valueOf(args[3]);
        case 3:
            this.info.distChange = Double.valueOf(args[2]);
        case 2:
            this.info.direction = Double.valueOf(args[1]);
            this.info.distance = Double.valueOf(args[0]);
            // Calculate this object's probable position
            if (!this.isStationaryObject()) {
                double absDir = Math.toRadians(player.direction.getDirection() + this.info.direction);
                double dist = this.info.distance;
                double px = player.position.getX();
                double py = player.position.getY();
                double confidence = player.position.getConfidence(time) * 0.95;
                double x = px + dist * Math.cos(absDir);
                double y = py + dist * Math.sin(absDir);
                this.position.update(x, y, confidence, time);
            }
            break;   
        case 1:
            this.info.direction = Double.valueOf(args[0]);
            break;
        default:
            Log.e("Field object had " + args.length + " arguments.");
        }
    }
}
