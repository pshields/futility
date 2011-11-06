package futility;

// Encapsulates the data received in a see message
public class SeeInfo {
    public int time;
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
        this.reset();
    }
    
    /**
     * Resets this object 
     */
    public void reset() {
        this.time = -1;
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
}
