/** @file Rectangle.java
 * Represents a rectangular area on the playing field.
 * 
 * @author Team F(utility)
 * @date 20 October 2011
 */

package futility;

/** @class Rectangle
 * Class representation of a rectangular area on the playing field.
 */
public class Rectangle {
    public double bottom = -1.0;
    public double left = -1.0;
    public double right = -1.0;
    public double top = -1.0;
    
    /**
     * Constructor, builds a rectangle based on position of each border.
     * @param top vertical position of the top border
     * @param right horizontal position of the right border
     * @param bottom vertical position of the bottom border
     * @param left horizontal position of the left border
     */
    public Rectangle(double top, double right, double bottom, double left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }
    
    /**
     * Checks if an object lies within the area of this rectangle.
     * @param object the field object to test for
     * @return true if the object is inside this rectangle, otherwise false.
     */
    public boolean contains(FieldObject object) {
        double x = object.position.getPosition().getX();
        double y = object.position.getPosition().getY();
        return x >= left && x <= right && y >= bottom && y <= top;
    }
    
    /**
     * Gets the center point of this rectangle.
     * @return the center of the rectangle as a Point object.
     */
    public Point getCenter() {
        double centerX = (left + right) / 2;
        double centerY = (bottom + top) / 2;
        return new Point(centerX, centerY);
    }
}
