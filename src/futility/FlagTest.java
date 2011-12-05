/** @file FlagTest.java
 * `Flag` tests.
 * 
 * @author Team F(utility)
 */

package futility;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import futility.FieldObject;
import futility.Flag;

/**
 * `Flag` tests.
 */
public class FlagTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests that flags are assigned the correct coordinates.
	 */
	@Test
	public void testFlag() {
		String[] flags = {
				// Field perimeter flags
				"(f t l 50)",
				"(f t l 40)",
				"(f t l 30)",
				"(f t l 20)",
				"(f t l 10)",
				"(f t 0)",
				"(f t r 10)",
				"(f t r 20)",
				"(f t r 30)",
				"(f t r 40)",
				"(f t r 50)",
				"(f r t 30)",
				"(f r t 20)",
				"(f r t 10)",
				"(f r 0)",
				"(f r b 10)",
				"(f r b 20)",
				"(f r b 30)",
				"(f b r 50)",
				"(f b r 40)",
				"(f b r 30)",
				"(f b r 20)",
				"(f b r 10)",
				"(f b 0)",
				"(f b l 10)",
				"(f b l 20)",
				"(f b l 30)",
				"(f b l 40)",
				"(f b l 50)",
				"(f l b 30)",
				"(f l b 20)",
				"(f l b 10)",
				"(f l 0)",
				"(f l t 10)",
				"(f l t 20)",
				"(f l t 30)",

				// Field corner flags
				"(f l t)",
				"(f r t)",
				"(f r b)",
				"(f l b)",

				// Field center flags
				"(f c t)",
				"(f c)",
				"(f c b)",

				// Penalty area flags
				"(f p l t)",
				"(f p l c)",
				"(f p l b)",
				"(f p r t)",
				"(f p r c)",
				"(f p r b)",

				// Goalpost flags
				"(f g l t)",
				"(f g l b)",
				"(f g r t)",
				"(f g r b)",
		};

		double[] xcoords = {
				// Field perimeter flags
				-50,   // (f t l 50)
				-40,   // (f t l 40)
				-30,   // (f t l 30)
				-20,   // (f t l 20)
				-10,   // (f t l 10)
				0,     // (f t 0)
				10,    // (f t r 10)
				20,    // (f t r 20)
				30,    // (f t r 30)
				40,    // (f t r 40)
				50,    // (f t r 50)
				57.5,  // (f r t 30)
				57.5,  // (f r t 20)
				57.5,  // (f r t 10)
				57.5,  // (f r 0)
				57.5,  // (f r b 10)
				57.5,  // (f r b 20)
				57.5,  // (f r b 30)
				50,    // (f b r 50)
				40,    // (f b r 40)
				30,    // (f b r 30)
				20,    // (f b r 20)
				10,    // (f b r 10)
				0,     // (f b 0)
				-10,   // (f b l 10)
				-20,   // (f b l 20)
				-30,   // (f b l 30)
				-40,   // (f b l 40)
				-50,   // (f b l 50)
				-57.5, // (f l b 30)
				-57.5, // (f l b 20)
				-57.5, // (f l b 10)
				-57.5, // (f l 0)
				-57.5, // (f l t 10)
				-57.5, // (f l t 20)
				-57.5, // (f l t 30)

				// Field corner flags
				-52.5, // (f l t)
				52.5,  // (f r t)
				52.5,  // (f r b)
				-52.5, // (f l b)

				// Field center flags
				0,     // (f c t)
				0,     // (f c)
				0,     // (f c b)

				// Penalty area flags
				-36,   // (f p l t)
				-36,   // (f p l c)
				-36,   // (f p l b)
				36,    // (f p r t)
				36,    // (f p r c)
				36,    // (f p r b)

				// Goalpost flags
				-52.5, // (f g l t)
				-52.5, // (f g l b)
				52.5,  // (f g r t)
				52.5,  // (f g r b)
		};

		double[] ycoords = {
				// Field perimeter flags
				-39,    // (f t l 50)
				-39,    // (f t l 40)
				-39,    // (f t l 30)
				-39,    // (f t l 20)
				-39,    // (f t l 10)
				-39,    // (f t 0)
				-39,    // (f t r 10)
				-39,    // (f t r 20)
				-39,    // (f t r 30)
				-39,    // (f t r 40)
				-39,    // (f t r 50)
				-30,    // (f r t 30)
				-20,    // (f r t 20)
				-10,    // (f r t 10)
				0,      // (f r 0)
				10,     // (f r b 10)
				20,     // (f r b 20)
				30,     // (f r b 30)
				39,     // (f b r 50)
				39,     // (f b r 40)
				39,     // (f b r 30)
				39,     // (f b r 20)
				39,     // (f b r 10)
				39,     // (f b 0)
				39,     // (f b l 10)
				39,     // (f b l 20)
				39,     // (f b l 30)
				39,     // (f b l 40)
				39,     // (f b l 50)
				30,     // (f l b 30)
				20,     // (f l b 20)
				10,     // (f l b 10)
				0,      // (f l 0)
				-10,    // (f l t 10)
				-20,    // (f l t 20)
				-30,    // (f l t 30)

				// Field corner flags
				-34,    // (f l t)
				-34,    // (f r t)
				34,     // (f r b)
				34,     // (f l b)

				// Field center flags
				-34,    // (f c t)
				0,      // (f c)
				34,     // (f c b)

				// Penalty area flags
				-20.15, // (f p l t)
				0,      // (f p l c)
				20.15,  // (f p l b)
				-20.15, // (f p r t)
				0,      // (f p r c)
				20.15,  // (f p r b)

				// Goalpost flags
				-7.01,  // (f g l t)
				7.01,   // (f g l b)
				-7.01,  // (f g r t)
				7.01,   // (f g r b)
		};

		for (int i = 0; i < flags.length; i++) {
			FieldObject newFlag = FieldObject.create(flags[i]);
			System.out.println("Attempting flag id: " + flags[i]);
			assertTrue(newFlag instanceof Flag);
			assertEquals(xcoords[i], newFlag.position.getX(), 0.001);
			assertEquals(ycoords[i], newFlag.position.getY(), 0.001);
		}
	}
}