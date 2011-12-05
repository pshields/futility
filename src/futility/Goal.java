/** @file Goal.java
 * Representation of a goal.
 *
 * @author Team F(utility)
 */

package futility;

/**
 * Extension of StationaryObject that represents an object on the visible playing field.
 */
public class Goal extends StationaryObject {
    
    /**
     * Goal constructor. Automatically assigns the correct position.
     * 
     * @param id the ObjectId of the goal
     */
    public Goal(String id) {
        this.id = id;
        this.position = this.setPosition();
    }

    /**
     * Returns the position of a goal, using it's ObjectId.
     * 
     * @return the goal's position
     */
    protected PositionEstimate setPosition() {
    	//A switch{} would be better, but not sure if we will have JRE 1.7 available
    	if (this.id == "(goal l)") {
            return new PositionEstimate(-52.5, 0, 1.0, -1);
        }
    	else if (this.id == "(goal r)") {
            return new PositionEstimate(52.5, 0, 1.0, -1);
        }
    	// Poor error handling
    	return new PositionEstimate(-1.0, -1.0, 0.0, -1);
    }
}
