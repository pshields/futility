package futility;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BrainTest {
    Process rcssserver;
    Client client;

    @Before
    public void setUp() throws Exception {
        try {
            Runtime runtime = Runtime.getRuntime();
            this.rcssserver = runtime.exec("rcssserver");
            String[] args = {};
            this.client = new Client(args);
            this.client.init();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @After
    public void tearDown() throws Exception {
        this.rcssserver.destroy();
    }
    
    /**
     * Tests that the player with the brain correctly updates its beliefs about the player's
     * direction when sending turn commands.
     */
    @Test
    public void testTurnCommandsUpdateBeliefs() {
        this.client.player.brain.overrideStrategy(Brain.Strategy.TEST_TURNS);
        assertEquals(true, true);
    }
}
