package futility;

// A point on a 2D plane
public class Point {
    double x;
    double y;
    
    public Point() {
        x = Settings.INITIAL_POSITION().x;
        y = Settings.INITIAL_POSITION().y;
    }
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
