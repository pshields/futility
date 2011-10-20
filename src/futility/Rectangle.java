package futility;

public class Rectangle {
	private double bottom = -1.0;
	private double left = -1.0;
	private double right = -1.0;
	private double top = -1.0;
    
    public Rectangle(double top, double right, double bottom, double left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }
    
    public boolean contains(FieldObject object) {
        double x = object.position.getPosition().getX();
        double y = object.position.getPosition().getY();
        return x >= left && x <= right && y >= bottom && y <= top;
    }
    
    public Point getCenter() {
        double centerX = (left + right) / 2;
        double centerY = (bottom + top) / 2;
        return new Point(centerX, centerY);
    }

    public double getTop() {
    	return this.top;
    }
    
    public double getRight() {
    	return this.right;
    }
    
    public double getBottom() {
    	return this.bottom;
    }
    
    public double getLeft() {
    	return this.left;
    }
}