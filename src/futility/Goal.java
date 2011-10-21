/** @file Goal.java
 * Representation of a visible goal object.
 * @author Team F(utility)
 */

package futility;

/** @class Goal
 * Extension of StationaryObject to represent a visible goal on the playing
 * field.
 */
public class Goal extends StationaryObject {
    
	/**
	 * Builds a goal object according to the side of the field it belongs on.
	 * 
	 * @param id literal string identifier, as sent by the server.
	 */
    public Goal(String id) {
        this.id = id;
        double x = -1.0;
        double y = Settings.FIELD().getCenter().getY();
        
        // Determine the side this goal belongs on:
        if (id.charAt(3) == 'l') {
            x = Settings.FIELD().getLeft();
        }
        else if (id.charAt(3) == 'r') {
            x = Settings.FIELD().getRight();
        }
        else {
            System.out.println("Couldn't parse goal id "+id);
        }
        
        // Set our understanding of the goal's position:
        position = new PositionEstimate(x, y, 1.0, true);
    }
}
