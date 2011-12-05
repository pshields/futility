/** @file RectangleTest.java
 * `Rectangle` tests.
 * 
 * @author Team F(utility)
 */

package futility;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import futility.FieldObject;
import futility.MobileObject;
import futility.Point;
import futility.Rectangle;

/**
 * Container for `Rectangle` tests.
 */
public class RectangleTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests Rectangle constructor.
	 */
	@Test
	public void testRectangle() {
		Rectangle rect = new Rectangle(1, 2, 3, 4);
		assertEquals(1, rect.getTop(), 0);
		assertEquals(2, rect.getRight(), 0);
		assertEquals(3, rect.getBottom(), 0);
		assertEquals(4, rect.getLeft(), 0);
	}

	/**
	 * Tests Rectangle::contains method.
	 */
	@Test
	public void testContains() {
		Rectangle rect = new Rectangle(4, 4, 1, 1);
		FieldObject[] objects = {
				new MobileObject(1.1, 1.1),
				new MobileObject(1.1, 2.1),
				new MobileObject(1.1, 5.1),
				new MobileObject(4.1, 1.1),
				new MobileObject(2.1, 2.1),
				new MobileObject(0.0, 0.0),
				new MobileObject(6.1, 4.1),
				new MobileObject(3.1, 3.1),
		};
		boolean[] expectations = {
				true,
				true,
				false,
				true,
				true,
				true,
				false,
				true,
		};
		
		for(int i = 0; i <= objects.length; i++) {
			assertEquals(expectations[i], rect.contains(objects[i]));
		}
	}

	@Test
	public void testGetCenter() {
		Rectangle rect = new Rectangle(2, 4, 6, 8);
		Point center = rect.getCenter();
		assertEquals(6, center.getX(), 0);
		assertEquals(4, center.getY(), 0);
	}

}
