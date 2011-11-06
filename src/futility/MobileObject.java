/** @file MobileObject.java
* Represents a moving physical object positioned somewhere on the field.
* 
* @author Team F(utility)
*/

package futility;

/**
 * Extension of FieldObject representing an object that can move.
 */
public class MobileObject extends FieldObject{

	/**
	 * Default constructor, initializes with default FieldObject values.
	 */
    public MobileObject() {}
	
    /**
     * Default mobile object constructor.
     * 
     * @param x an x-coordinate for the object's initial position
     * @param y a y-coordinate for the object's initial position
     */
    public MobileObject(double x, double y) {
    	super(x, y);
    }
}
