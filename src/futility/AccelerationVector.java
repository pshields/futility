package futility;

public class AccelerationVector extends Vector2D {
    
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////
    public AccelerationVector() {
        super();
    }
    
    public AccelerationVector(double x, double y) {
        super(x, y);
        this.normalize();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Normalizes this vector's magnitude.
     */
    private final void normalize() {
        double ratio = this.magnitude() / Settings.PLAYER_ACCEL_MAX;
        if (ratio > 1.0) {
            this.setX(this.getX() / ratio);
            this.setY(this.getY() / ratio);
        }
    }
    
    /**
     * Resets this vector to zero.
     */
    public final void reset() {
        this.setX(0.0);
        this.setY(0.0);
    }
    
    /**
     * Returns the zero vector.
     * 
     * @return the zero vector
     */
    public static final AccelerationVector ZeroVector() {
        return new AccelerationVector(0.0, 0.0);
    }
}
