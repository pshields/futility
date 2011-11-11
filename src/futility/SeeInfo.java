/** @file SeeInfo.java
 * Represents the information encoded in a `see` message.
 * 
 * @author Team F(utility)
 */ 

package futility;

/**
 * Encapsulates the data received in a `see` message.
 */
public class SeeInfo extends Info{
    
    double distance;
    double direction;
    double distChange;
    double dirChange;
    double bodyFacingDir;
    double headFacingDir;
    double pointingDir;
    boolean tackling;
    boolean kicking;
    
    /**
     * Primary constructor.
     */
    public SeeInfo() {
    	super();
    }
    
    /**
     * Resets this object.
     */
    @Override
    public void reset() {
    	super.reset();
        this.distance = -1.0;
        this.direction = -181.0;
        this.distChange = -1.0;
        this.dirChange = -1.0;
        this.bodyFacingDir = -1.0;
        this.headFacingDir = -1.0;
        this.pointingDir = -1.0;
        this.tackling = false;
        this.kicking = false;
    }
    
    /**
     * Copies another see info.
     * 
     * @param info to copy
     */
    public void copy(SeeInfo info){
    	info.time = time;
    	info.distance = distance;
    	info.direction = direction;
    	info.distChange = distChange;
    	info.dirChange = dirChange;
    	info.bodyFacingDir = bodyFacingDir;
    	info.headFacingDir = headFacingDir;
    	info.pointingDir = pointingDir;
    	info.tackling = tackling;
    	info.kicking = kicking;
    }
}
