package futility;

public class VelocityVector extends Vector2D {

    // Constructors
    public VelocityVector() {
        super();
    }
    
    public VelocityVector(double x, double y) {
        super(x, y);
        this.normalize();
    }
    
    // Methods
    /**
     * Normalizes this vector's magnitude.
     */
    private final void normalize() {
        double ratio = this.magnitude() / Settings.PLAYER_SPEED_MAX;
        if (ratio > 1.0) {
            this.setX(this.getX() / ratio);
            this.setY(this.getY() / ratio);
        }
    }
    
    /**
     * Updates this vector using polar coordinates.
     * 
     * @param dir direction in radians
     * @param mag magnitude
     */
    public final void setPolar(double dir, double mag) {
        this.setX(mag * Math.cos(dir));
        this.setY(mag * Math.sin(dir));
    }
}
