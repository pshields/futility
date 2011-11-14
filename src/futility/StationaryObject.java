/** @file StationaryObject.java
 * Represents a physical object positioned somewhere on the field. This object
 * does not move.
 * 
 * @author Team F(utility)
 */ 

package futility;

/**
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
     * Primary constructor.
     * 
     * @param id identifying string literal
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public StationaryObject(String id, double x, double y) {
        if (!Futil.isCorrectlyFormatted(id)) {
            Log.e("id sent to stationary object constructor: " + id);
            return;
        }
        this.id = id;
        this.position = new PositionEstimate(x, y, 1.0, -1);
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
