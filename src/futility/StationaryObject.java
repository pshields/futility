/** @file StationaryObject.java
 * Represents a physical object positioned somewhere on the field. This object
 * does not move.
 * 
 * @author Team F(utility)
 */ 

package futility;

/** @class StationaryObject
 * Data structure extension of FieldObject that represents a stationary object
 * on the playing field.
 */
public class StationaryObject extends FieldObject {
    
	/**
	 * Default constructor; builds a StationaryObject with default FieldObject
	 * values.
	 */
    public StationaryObject() {
    }
    
    /**
     * Constructor; Builds a StationaryObject with a unique identifying string
     * and the provided coordinates.
     * 
     * @param id identifying string literal
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public StationaryObject(String id, double x, double y) {
        this.id = id;
        this.position = new PositionEstimate(x, y, 1.0, true);
    }
    
    /**
     * Returns true if this is a stationary object.
     * 
     * @return true
     */
    public boolean isStationaryObject() {
        return true;
    }
}
