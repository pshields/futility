/** @file Info.java
 * Encapsulation of an object's info at a specific time step.
 * 
 * @author Team F(utility)
 */ 

package futility;

/**
 *Encapsulates the data received in a see message 
 *{@link SeeInfo} & {@link SenseInfo} extend this class
 */
public abstract class Info {

	int time;
	
	/**
	 * This default constructor uses the reset method to initialize member variables.
	 */
	public Info(){
		reset();
	}
	
	/**
	 * Resets member variables in preparation for being updated.
	 */
	public void reset(){
		this.time = -1;
	}
	
}
