package tests.futility;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import futility.Flag;
import futility.Goal;

public class FlagTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

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
				7.5,   //(f t l 50)
				17.5,  //(f t l 40)
				27.5,  //(f t l 30)
				37.5,  //(f t l 20)
				47.5,  //(f t l 10)
				57.5,  //(f t 0)
				67.5,  //(f t r 10)
				77.5,  //(f t r 20)
				87.5,  //(f t r 30)
				97.5,  //(f t r 40)
				107.5, //(f t r 50)
				115,   //(f r t 30)
				115,   //(f r t 20)
				115,   //(f r t 10)
				115,   //(f r 0)
				115,   //(f r b 10)
				115,   //(f r b 20)
				115,   //(f r b 30)
				107.5, //(f b r 50)
				97.5,  //(f b r 40)
				87.5,  //(f b r 30)
				77.5,  //(f b r 20)
				67.5,  //(f b r 10)
				57.5,  //(f b 0)
				47.5,  //(f b l 10)
				37.5,  //(f b l 20)
				27.5,  //(f b l 30)
				17.5,  //(f b l 40)
				7.5,   //(f b l 50)
				0,     //(f l b 30)
				0,     //(f l b 20)
				0,     //(f l b 10)
				0,     //(f l 0)
				0,     //(f l t 10)
				0,     //(f l t 20)
				0,     //(f l t 30)

				// Field corner flags
				5,     //(f l t)
				110,   //(f r t)
				110,   //(f r b)
				5,     //(f l b)

				// Field center flags
				57.5,  //(f c t)
				57.5,  //(f c)
				57.5,  //(f c b)

				// Penalty area flags
				21.5,  //(f p l t)
				21.5,  //(f p l c)
				21.5,  //(f p l b)
				93.5,  //(f p r t)
				93.5,  //(f p r c)
				93.5,  //(f p r b)

				// Goalpost flags
				5,     //(f g l t)
				5,     //(f g l b)
				110,   //(f g r t)
				110,   //(f g r b)
		};

		double[] ycoords = {
				// Field perimeter flags
				78,    //(f t l 50)
				78,    //(f t l 40)
				78,    //(f t l 30)
				78,    //(f t l 20)
				78,    //(f t l 10)
				78,    //(f t 0)
				78,    //(f t r 10)
				78,    //(f t r 20)
				78,    //(f t r 30)
				78,    //(f t r 40)
				78,    //(f t r 50)
				69,    //(f r t 30)
				59,    //(f r t 20)
				49,    //(f r t 10)
				39,    //(f r 0)
				29,    //(f r b 10)
				19,    //(f r b 20)
				9,     //(f r b 30)
				0,     //(f b r 50)
				0,     //(f b r 40)
				0,     //(f b r 30)
				0,     //(f b r 20)
				0,     //(f b r 10)
				0,     //(f b 0)
				0,     //(f b l 10)
				0,     //(f b l 20)
				0,     //(f b l 30)
				0,     //(f b l 40)
				0,     //(f b l 50)
				9,     //(f l b 30)
				19,    //(f l b 20)
				29,    //(f l b 10)
				39,    //(f l 0)
				49,    //(f l t 10)
				59,    //(f l t 20)
				69,    //(f l t 30)

				// Field corner flags
				73,    //(f l t)
				73,    //(f r t)
				5,     //(f r b)
				5,     //(f l b)

				// Field center flags
				73,    //(f c t)
				39,    //(f c)
				5,     //(f c b)

				// Penalty area flags
				59.15, //(f p l t)
				39,    //(f p l c)
				18.85, //(f p l b)
				59.15, //(f p r t)
				39,    //(f p r c)
				18.85, //(f p r b)

				// Goalpost flags
				46.01, //(f g l t)
				31.99, //(f g l b)
				46.01, //(f g r t)
				31.99, //(f g r b)
		};

		for (int i = 0; i < flags.length; i++) {
			Flag newFlag = new Flag(flags[i]);
			System.out.println("Attempting flag id: " + flags[i]);
			assertEquals(xcoords[i], newFlag.position.getX(), 0.001);
			assertEquals(ycoords[i], newFlag.position.getY(), 0.001);
		}
	}
}