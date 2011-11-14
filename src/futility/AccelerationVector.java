package futility;

public class AccelerationVector extends Vector2D {
    
    // Constructors
    public AccelerationVector() {
        super();
    }
    
    public AccelerationVector(double x, double y) {
        super(x, y);
        this.normalize();
    }
    
    // Methods
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
    
    public final void reset() {
        this.setX(0.0);
        this.setY(0.0);
    }
}
