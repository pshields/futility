package futility;

public class Vector2D {
    private double x;
    private double y;

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////
    public Vector2D() {
        this.reset();
    }
    
    /**
     * Constructor taking only a magnitude. Current implementation assumes the direction is the
     * positive x axis.
     *  
     * @param magnitude
     */
    public Vector2D(double magnitude) {
        this.x = magnitude;
    }
    
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////
    public static Vector2D ZeroVector() {
        return new Vector2D(0.0, 0.0);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // GETTERS AND SETTERS
    ///////////////////////////////////////////////////////////////////////////
    public final double getX() {
        return this.x;
    }
    
    protected final void setX(double x) {
        this.x = x;
    }
    
    public final double getY() {
        return this.y;
    }
    
    protected final void setY(double y) {
        this.y = y;
    }
    
    public final double magnitude() {
        return Math.hypot(x, y);
    }
    
    /**
     * Returns the result of adding a given polar vector to this one.
     * 
     * @param dir direction in radians of the other vector
     * @param mag magnitude of the other vector
     */
    public final Vector2D addPolar(double dir, double mag) {
        double x = mag * Math.cos(dir);
        double y = mag * Math.sin(dir);
        return new Vector2D(x, y).add(this);
    }
    
    /**
     * Returns the direction, in radians, represented by the vector.
     * 
     * @return the direction, in radians, represented by the vector
     */
    public final double direction() {
        return Math.atan2(this.y, this.x);
    }
    
    /**
     * Adds two vectors. Converts to Cartesian coordinates in order to do so.
     * 
     * @param that the other direction vector
     * @return the result as a direction vector
     */
    public final Vector2D add(Vector2D that) {
        return new Vector2D(this.x + that.getX(), this.y + that.getY());
    }
    
    /**
     * Resets this vector.
     */
    public void reset() {
        this.x = Double.NaN;
        this.y = Double.NaN;
    }
}
