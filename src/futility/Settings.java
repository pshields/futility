/** @file Settings.java
 * Global variable and settings storage; known server and player parameters
 * are stored here.
 * 
 * @author Team F(utility)
 */

package futility;

import java.util.Arrays;
import java.util.HashSet;

import futility.Settings;

/**
 * Static class that stores all client parameters based on information
 * known about the simulation.
 */
public class Settings {
	// Client initialization settings:
    public static int VERBOSITY = Log.ERROR;
    public static final boolean DEBUG = false;
    public static final String HOSTNAME = "localhost";
    public static final int INIT_PORT = 6000;
    public static final String SOCCER_SERVER_VERSION = "15.0";
    public static final int MSG_SIZE = 4096;
    
    // Spin-up script team names
    public static final String TEAM_NAME = "futility";
    public static final String OTHER_TEAM_NAME = "adversary";
    
    // Field geometry for a standard field
    private static double GOAL_HEIGHT = 14.02;
    public static final double FIELD_WIDTH = 105.0;
    public static final double FIELD_HEIGHT = 68.0;
    public static final double FIELD_BUFFER = 5.0;
    public static final double PENALTY_AREA_WIDTH = 16.5; // 97.4% confirmed in robocup; based on size of actual field
    public static final double PENALTY_AREA_HEIGHT = 40.3; // 97.4% confirmed in robocup; based on size of actual field
    
    // Other constants
    public static final char LEFT_SIDE = 'l';
    public static final char RIGHT_SIDE = 'r';
    
    // Server parameters
    public static ServerParams_Ball   BALL_PARAMS   = new ServerParams_Ball();
    public static ServerParams_Player PLAYER_PARAMS = new ServerParams_Player();
    public static final double        TEAM_FAR_LENGTH = 40.0;
    public static final double        TEAM_TOO_FAR_LENGTH = 60.0;
    
    // Inferences
    public static final double DISTANCE_ESTIMATE = 0.333333 * TEAM_FAR_LENGTH + 0.666666 * TEAM_TOO_FAR_LENGTH;

    // Coordinates
    public static final Point CENTER_FIELD = new Point(0, 0);
    
    /**
     * Constant string literals representing the commands a client may send
     * to the server.
     */
    public class Commands {
        public static final String BYE = "bye";
        public static final String DASH = "dash";
        public static final String INIT = "init";
        public static final String KICK = "kick";
        public static final String TURN = "turn";
        public static final String MOVE = "move";
    }
    
    public static enum RESPONSE {
        SEE,
        SENSE_BODY,
        NONE
    }

	/**
	 * Gets the height of the goal.
	 * 
	 * @return the goal's height.
	 */
	public static double getGoalHeight() {
		return GOAL_HEIGHT;
	}
	
	/**
	 * Sets the height of the goal.
	 * 
	 * @param height the height of the goal.
	 */
	public static void setGoalHeight(double height) {
		GOAL_HEIGHT = height;
	}

	/**
	 * Rebuilds all server parameter data stamps according to each object's Builder settings.
	 */
    public static void rebuildParams()
    {
    	BALL_PARAMS = new ServerParams_Ball();
    	PLAYER_PARAMS = new ServerParams_Player();
    }
	
	// playing field
    public static Rectangle FIELD = new Rectangle(-FIELD_HEIGHT / 2.0, FIELD_WIDTH / 2.0, FIELD_HEIGHT / 2.0, -FIELD_WIDTH / 2.0);
    
    // absolute physical boundary of the game space
    public static Rectangle PHYSICAL_BOUNDARY = new Rectangle(FIELD.getTop() - FIELD_BUFFER, FIELD.getRight() + FIELD_BUFFER, FIELD.getBottom() + FIELD_BUFFER, FIELD.getLeft() - FIELD_BUFFER);
    
    // penalty areas
    public static Rectangle PENALTY_AREA_LEFT = new Rectangle(-PENALTY_AREA_HEIGHT / 2.0, FIELD.getLeft() + PENALTY_AREA_WIDTH, PENALTY_AREA_HEIGHT / 2.0, FIELD.getLeft());
    public static Rectangle PENALTY_AREA_RIGHT = new Rectangle(-PENALTY_AREA_HEIGHT / 2.0, FIELD.getRight(), PENALTY_AREA_HEIGHT / 2.0, FIELD.getRight() - PENALTY_AREA_WIDTH);
    
    ///////////////////////////////////////////////////////////////////////////
    // LOOK-UP TABLES
    ///////////////////////////////////////////////////////////////////////////
    
    // List of known game-state play modes
    // Look-up table for checking referee messages against any known or
    //   specified play modes. Use contains("") to check against the table.
    // Hash Set, for O(1) look-up time.
    public static final HashSet<String> PLAY_MODES = new HashSet<String>(Arrays.asList(
    		"before_kick_off",
    		"play_on",
    		"time_over",
    		"kick_off_l",
    		"kick_off_r",
    		"kick_in_l",
    		"kick_in_r",
    		"free_kick_l",
    		"free_kick_r",
    		"corner_kick_l",
    		"corner_kick_r",
    		"goal_l_",
    		"goal_r_",
    		"goal_kick_l",
    		"goal_kick_r",
    		"drop_ball",
    		"offside_l",
    		"offside_r"
    ));
    
    // Starting formation for pre kick off game states
    // Index 0 is unused; index 1 onward corresponds to a uniform number.
    public static final Point[] FORMATION = {
        new Point(  0.0,   0.0),  // Center of field
    	new Point(-52.5,   0.0),  // Player 1
    	new Point(-30.0,  20.0),
    	new Point(-30.0, -20.0),
    	new Point(-30.0,   0.0),
    	new Point(-15.0, -20.0),
    	new Point(-15.0,  20.0),
    	new Point(-15.0,   0.0),
    	new Point(-20.0,  30.0),
    	new Point(-20.0, -30.0),
    	new Point(-20.0,  15.0),
    	new Point(-20.0, -15.0)   // Player 11
    };
    
    public static final Point[] FREE_KICK_L_FORMATION = {
        new Point(  0.0,   0.0),  // Center of field
    	new Point(-52.5,   0.0),  // Player 1
    	new Point(-30.0,  20.0),
    	new Point(-30.0, -20.0),
    	new Point(-30.0,   0.0),
    	new Point(-15.0, -20.0),
    	new Point(-15.0,  20.0),
    	new Point(-15.0,   0.0),
    	new Point(-20.0,  30.0),
    	new Point(-20.0, -30.0),
    	new Point(-20.0,  15.0),
    	new Point(-20.0, -15.0)   // Player 11
    };
    
    public static final Point[] FREE_KICK_R_FORMATION = {
        new Point(  0.0,   0.0),  // Center of field
    	new Point(-52.5,   0.0),  // Player 1
    	new Point(-30.0,  20.0),
    	new Point(-30.0, -20.0),
    	new Point(-30.0,   0.0),
    	new Point(-15.0, -20.0),
    	new Point(-15.0,  20.0),
    	new Point(-15.0,   0.0),
    	new Point(-20.0,  30.0),
    	new Point(-20.0, -30.0),
    	new Point(-20.0,  15.0),
    	new Point(-20.0, -15.0)   // Player 11
    };
    
    public static final Point[] CORNER_KICK_L_FORMATION = {
        new Point(  0.0,   0.0),  // Center of field
    	new Point(-52.5,   0.0),  // Player 1
    	new Point(-30.0,  20.0),
    	new Point(-30.0, -20.0),
    	new Point(-30.0,   0.0),
    	new Point(-15.0, -20.0),
    	new Point(-15.0,  20.0),
    	new Point(-15.0,   0.0),
    	new Point(-20.0,  30.0),
    	new Point(-20.0, -30.0),
    	new Point(-20.0,  15.0),
    	new Point(-20.0, -15.0)   // Player 11
    };
    
    public static final Point[] CORNER_KICK_R_FORMATION = {
        new Point(  0.0,   0.0),  // Center of field
    	new Point(-52.5,   0.0),  // Player 1
    	new Point(-30.0,  20.0),
    	new Point(-30.0, -20.0),
    	new Point(-30.0,   0.0),
    	new Point(-15.0, -20.0),
    	new Point(-15.0,  20.0),
    	new Point(-15.0,   0.0),
    	new Point(-20.0,  30.0),
    	new Point(-20.0, -30.0),
    	new Point(-20.0,  15.0),
    	new Point(-20.0, -15.0)   // Player 11
    };
    
    // Groupings of stationary flags
    public static final String[][] BOUNDARY_FLAG_GROUPS = {
            // top boundary flags
            {
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
                "(f t r 50)"
            },
            // right boundary flags
            {
                "(f r t 30)",
                "(f r t 20)",
                "(f r t 10)",
                "(f r 0)",
                "(f r b 10)",
                "(f r b 20)",
                "(f r b 30)"
            },
            // bottom boundary flags
            {
                "(f b l 50)",
                "(f b l 40)",
                "(f b l 30)",
                "(f b l 20)",
                "(f b l 10)",
                "(f b 0)",
                "(f b r 10)",
                "(f b r 20)",
                "(f b r 30)",
                "(f b r 40)",
                "(f b r 50)"
            },
            // left boundary flags
            {
                "(f l t 30)",
                "(f l t 20)",
                "(f l t 10)",
                "(f l 0)",
                "(f l b 10)",
                "(f l b 20)",
                "(f l b 30)"
            },
            
    };
    
    /**
     * List of known stationary objects.
     * Although they could theoretically be parsed on the fly, we think it's
     * probably more efficient to parse and store them in advance. They are
     * stationary, after all.
     */
    public static final StationaryObject[] STATIONARY_OBJECTS = {
        // Physical boundary flags
        new StationaryObject("(f t l 50)", -50.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t l 40)", -40.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t l 30)", -30.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t l 20)", -20.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t l 10)", -10.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t 0)", 0.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t r 10)", 10.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t r 20)", 20.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t r 30)", 30.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t r 40)", 40.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f t r 50)", 50.0, PHYSICAL_BOUNDARY.getTop()),
        new StationaryObject("(f r t 30)", PHYSICAL_BOUNDARY.getRight(), -30.0),
        new StationaryObject("(f r t 20)", PHYSICAL_BOUNDARY.getRight(), -20.0),
        new StationaryObject("(f r t 10)", PHYSICAL_BOUNDARY.getRight(), -10.0),
        new StationaryObject("(f r 0)", PHYSICAL_BOUNDARY.getRight(), 0.0),
        new StationaryObject("(f r b 10)", PHYSICAL_BOUNDARY.getRight(), 10.0),
        new StationaryObject("(f r b 20)", PHYSICAL_BOUNDARY.getRight(), 20.0),
        new StationaryObject("(f r b 30)", PHYSICAL_BOUNDARY.getRight(), 30.0),
        new StationaryObject("(f b r 50)", 50.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b r 40)", 40.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b r 30)", 30.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b r 20)", 20.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b r 10)", 10.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b 0)", 0.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b l 10)", -10.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b l 20)", -20.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b l 30)", -30.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b l 40)", -40.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f b l 50)", -50.0, PHYSICAL_BOUNDARY.getBottom()),
        new StationaryObject("(f l b 30)", PHYSICAL_BOUNDARY.getLeft(), 30.0),
        new StationaryObject("(f l b 20)", PHYSICAL_BOUNDARY.getLeft(), 20.0),
        new StationaryObject("(f l b 10)", PHYSICAL_BOUNDARY.getLeft(), 10.0),
        new StationaryObject("(f l 0)", PHYSICAL_BOUNDARY.getLeft(), 0.0),
        new StationaryObject("(f l t 10)", PHYSICAL_BOUNDARY.getLeft(), 10.0),
        new StationaryObject("(f l t 20)", PHYSICAL_BOUNDARY.getLeft(), 20.0),
        new StationaryObject("(f l t 30)", PHYSICAL_BOUNDARY.getLeft(), 30.0),
        
        // Field corner flags
        new StationaryObject("(f l t)", FIELD.getLeft(), FIELD.getTop()),
        new StationaryObject("(f r t)", FIELD.getRight(), FIELD.getTop()),
        new StationaryObject("(f r b)", FIELD.getRight(), FIELD.getBottom()),
        new StationaryObject("(f l b)", FIELD.getLeft(), FIELD.getBottom()),
        
        // Field center flags
        new StationaryObject("(f c t)", 0.0, FIELD.getTop()),
        new StationaryObject("(f c)", 0.0, 0.0),
        new StationaryObject("(f c b)", 0.0, FIELD.getBottom()),
        
        // Penalty area flags
        new StationaryObject("(f p l t)", PENALTY_AREA_LEFT.getRight(), PENALTY_AREA_LEFT.getTop()),
        new StationaryObject("(f p l c)", PENALTY_AREA_LEFT.getRight(), 0.0),
        new StationaryObject("(f p l b)", PENALTY_AREA_LEFT.getRight(), PENALTY_AREA_LEFT.getBottom()),
        new StationaryObject("(f p r t)", PENALTY_AREA_RIGHT.getLeft(), PENALTY_AREA_RIGHT.getTop()),
        new StationaryObject("(f p r c)", PENALTY_AREA_RIGHT.getLeft(), 0.0),
        new StationaryObject("(f p r b)", PENALTY_AREA_RIGHT.getLeft(), PENALTY_AREA_RIGHT.getBottom()),
        
        // Goalpost flags
        new StationaryObject("(f g l t)", FIELD.getLeft(), GOAL_HEIGHT / 2),
        new StationaryObject("(f g l b)", FIELD.getLeft(), -GOAL_HEIGHT / 2),
        new StationaryObject("(f g r t)", FIELD.getRight(), GOAL_HEIGHT / 2),
        new StationaryObject("(f g r b)", FIELD.getRight(), -GOAL_HEIGHT / 2),
        
        // Goals
        new StationaryObject("(g l)", FIELD.getLeft(), 0.0),
        new StationaryObject("(g r)", FIELD.getRight(), 0.0)
    };
}
