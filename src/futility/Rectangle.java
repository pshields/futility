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
    
    public boolean contains(ObjectConcept object) {
        if (object.x > left && object.x < right && object.y > bottom && object.y < top)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
