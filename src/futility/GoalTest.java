/** @file GoalTest.java
 * `Goal` tests.
 *
 * @author Team F(utility)
 */

package futility;

import static org.junit.Assert.*;

import org.junit.Test;

import org.junit.After;
import org.junit.Before;

import futility.FieldObject;
import futility.Goal;

/**
 * `Goal` testing class.
 */
public class GoalTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests that the goals have the correct positions.
	 */
	@Test
    public void testGoal() {
		String[] goals = {
				"(goal l)",
				"(goal r)",
		};
		double[] xcoords = {
				-52.5,
				52.5,
		};
		double[] ycoords = {
				0,
				0,
		};

		for (int i = 0; i < goals.length; i++) {
			FieldObject newGoal = FieldObject.create(goals[i]);
			System.out.println("Attempting goal id: " + goals[i]);
			assertTrue(newGoal instanceof Goal);
			assertEquals(xcoords[i], newGoal.position.getX(), 0.001);
			assertEquals(ycoords[i], newGoal.position.getY(), 0.001);
		}
    }
}
