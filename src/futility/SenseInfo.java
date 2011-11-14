/** @file SenseInfo.java
 * Represents the information encoded in a `sense_body` message.
 * 
 * @author Team F(utility)
 */ 
package futility;

/**
 *  Encapsulates information from `sense_body` messages from the server.
 */
public class SenseInfo extends Info {

	double stamina;
	double effort;
	double staminaCapacity;
	double amountOfSpeed;
	double directionOfSpeed;
	double headAngle;
	String collision;
	String viewQuality;
	String viewWidth;
	
	/**
	 * Resets this object's data.
	 */
	@Override
	public void reset() {
		super.reset();
		stamina = Double.NaN;
		effort = Double.NaN;
		staminaCapacity = Double.NaN;
		amountOfSpeed = Double.NaN;
		directionOfSpeed = Double.NaN;
		headAngle = Double.NaN;
		collision = "none";
		viewQuality = null;
		viewWidth = null;
	}
	
	/**
	 * Copies this info to another sense info.
	 * 
	 * @param info the sense info to copy to
	 */
	public void copy(SenseInfo info){
		info.time = time;
		info.stamina = stamina;
		info.effort = effort;
		info.staminaCapacity = staminaCapacity;
		info.amountOfSpeed = amountOfSpeed;
		info.directionOfSpeed = directionOfSpeed;
		info.headAngle = headAngle;
		info.collision = collision;
		info.viewQuality = viewQuality;
		info.viewWidth = viewWidth;
	}
}
