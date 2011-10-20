/** @file MobileObject.java
* Represents a moving physical object positioned somewhere on the field.
* 
* @author Team F(utility)
* @date 20 October 2011
*/ 

package futility;

/** @class MobileObject
* Extension of the FieldObject class that represents a moving object in
* the game space.
*/
public class MobileObject extends FieldObject{
	/**
	 * Default constructor, initializes with default FieldObject values.
	 */
    public MobileObject() {}
	
    /**
     * Constructor, builds a MobileObject given the specified coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public MobileObject(double x, double y) {
    	super(x, y);
    }
}