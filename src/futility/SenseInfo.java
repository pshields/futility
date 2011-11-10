package futility;

/**
 *  Class to encapsulate sense commands to the server
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
	 * copies the values of this into the parameter
	 * @param info to copy to
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
