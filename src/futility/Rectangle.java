package futility;

public class Rectangle {
    public double bottom = -1.0;
    public double left = -1.0;
    public double right = -1.0;
    public double top = -1.0;
    
    public Rectangle(double top, double right, double bottom, double left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }
    
    public boolean contains(FieldObject object) {
        if (object.position.x > left && object.position.x < right && object.position.y > bottom && object.position.y < top) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public Point getCenter() {
        double centerX = (left + right) / 2;
        double centerY = (bottom + top) / 2;
        return new Point(centerX, centerY);
    }
}
