/** @file AccelerationVector.java
 * Acceleration vectors for modeling game physics.
 * 
 * @author Team F(utility)
 */

package futility;

/**
 * Model for the acceleration of various `FieldObject`s in a soccer server time step. 
 */
public class AccelerationVector extends Vector2D {
    
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Empty constructor.
     */    
    public AccelerationVector() {
        super();
    }
    
    /**
     * Constructor for a vector, given an x and y.
     * 
     * @param x the x-coordinate of the vector
     * @param y the y-coordinate of the vector
     */
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
