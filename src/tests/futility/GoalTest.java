package tests.futility;


import static org.junit.Assert.*;

import org.junit.Test;

import org.junit.After;
import org.junit.Before;

import futility.PositionEstimate;
import futility.Settings;

public class GoalTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
    public void testGoal() {
/*		Goal(String id)
        this.id = id;
        double x = -1.0;
        double y = Settings.FIELD().getCenter().getY();
        
        // Determine the side this goal belongs on:
        if (id.charAt(3) == 'l') {
            x = Settings.FIELD().getLeft();
        }
        else if (id.charAt(3) == 'r') {
            x = Settings.FIELD().getRight();
        }
        else {
            System.out.println("Couldn't parse goal id "+id);
        }
        
        // Set our understanding of the goal's position:
        position = new PositionEstimate(x, y, 1.0, true);
*/
		fail("Not implemented yet");
    }
}
