/** @file PointTest.java
 * `Point` tests.
 *
 * @author Team F(utility)
 */
package futility;

import static org.junit.Assert.*;

import org.junit.Test;

import futility.Point;

/**
 * Container class for `Point` tests.
 */
public class PointTest {

    /**
     * Tests the point constructor.
     */
	@Test
	public void testPoint() {
		Point newPoint = new Point(0, 1);
		assertEquals(0, newPoint.getX(), 0);
		assertEquals(1, newPoint.getY(), 0);
	}

	/**
	 *  Tests the absoluteAngleTo method.
	 */
	@Test
    public void testAngleTo() {
		Point[] otherPoints = {
				new Point(1, 1)				
		};
		Point thisPoint = new Point(0, 0);
		double[] expectations = {
				45
		};

		for (int i = 0; i < otherPoints.length; i++) {
			assertEquals(expectations[i], thisPoint.absoluteAngleTo(otherPoints[i]), 0);
		}
    }
    
	/**
	 * Tests updating a point with another point's coordinates.
	 */
	@Test
	public void testUpdateFromAnotherPoint() {
		Point newPoint = new Point(0, 1);
		
		newPoint.update(2, 3);
		assertEquals(2, newPoint.getX(), 0);
		assertEquals(3, newPoint.getY(), 0);
	}

	/**
	 * Tests updating a point with given coordinates.
	 */
	@Test
    public void testUpdateFromXYCoords() {
		double[] x = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 5.5, 4.5, 3.5, 2.5, 1.5, 0.5};
		double[] y = {0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0};
		Point thisPoint = new Point(0.0, 0.0);
		
		for (int i = 0; i < x.length; i++) {
			thisPoint.update(x[i], y[i]);
			
			assertEquals(x[i], thisPoint.getX(), 0);
			assertEquals(y[i], thisPoint.getY(), 0);
		}
	}
    
	/**
	 * Tests the isEqual method.
	 */
    @Test
    public void testIsEqual() {
		Point[] otherPoints = {
				new Point(1.0, 1.0),				
				new Point(1.1, 1.1),
				new Point(1.2, 1.2),			
				new Point(1.3, 1.3),
				new Point(1.4, 1.4),			
				new Point(1.5, 1.5),
				new Point(1.6, 1.6),			
				new Point(1.7, 1.7),
				new Point(1.8, 1.8),
				new Point(1.9, 1.9),
				new Point(2.0, 2.0),
		};
		Point thisPoint = new Point(1.5, 1.5);
		boolean[] expectations = {
				false,
				false,
				false,
				false,
				false,
				true,
				false,
				false,
				false,
				false,
				false,
		};

		for (int i = 0; i < otherPoints.length; i++) {
			assertEquals(expectations[i], thisPoint.isEqual(otherPoints[i]));
		}
    }
}