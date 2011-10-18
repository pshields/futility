package futility;

import java.util.Arrays;
import java.util.HashSet;

import futility.Settings;

public class Settings {
    public static final boolean DEBUG = false;
    public static final String HOSTNAME = "localhost";
    public static final int INIT_PORT = 6000;
    public static final int INITIAL_HASH_MAP_SIZE = 50;
    public static final String OTHER_TEAM_NAME = "adversary";
    public static final int MSG_SIZE = 4096;
    public static final String TEAM_NAME = "futility";
    public static final String SOCCER_SERVER_VERSION = "15.0";
    public static final int VERBOSITY = 0;
    
    // Default initial position for things with unknown locations
    // Note: the origin is arbitrarily chosen to be the farthest bottom-left
    // point (given standard display conventions) that an object can occupy.
    public static final Point INITIAL_POSITION = new Point(-1.0, -1.0);
    
    // Field geometry
    public static final double FIELD_WIDTH = 105.0;
    public static final double FIELD_HEIGHT = 68.0;
    public static final double FIELD_BUFFER = 5.0;
    public static final double GOAL_HEIGHT = 14.02; // TODO Double-check this
    public static final double PENALTY_AREA_DISTANCE = 16.5; // TODO Double-check this
    
    // Other constants
    public static final char LEFT_SIDE = 'l';
    public static final char RIGHT_SIDE = 'r';    
    
    public static Rectangle FIELD() {
        return new Rectangle(FIELD_BUFFER + FIELD_HEIGHT, FIELD_BUFFER + FIELD_WIDTH, FIELD_BUFFER, FIELD_BUFFER);
    }
    
    public static Rectangle PHYSICAL_BOUNDARY() {
        return new Rectangle(FIELD_BUFFER * 2.0 + FIELD_HEIGHT, FIELD_BUFFER * 2.0 + FIELD_WIDTH, 0.0, 0.0);
    }
    
    public static class LOG_LEVELS {
        static int ALL = 3;
        static int DEBUG = 2;
        static int INFO = 1;
        static int ERROR = 0;
        static int NONE = -1;
    }
    
    public static Rectangle PENALTY_AREA_LEFT() {
        return new Rectangle(FIELD_BUFFER + FIELD_HEIGHT / 2.0 + GOAL_HEIGHT / 2.0 + PENALTY_AREA_DISTANCE, FIELD_BUFFER + PENALTY_AREA_DISTANCE, FIELD_BUFFER - GOAL_HEIGHT / 2 - PENALTY_AREA_DISTANCE, FIELD_BUFFER);
    }
    
    public static Rectangle PENALTY_AREA_RIGHT() {
        return new Rectangle(FIELD().getCenter().y + (GOAL_HEIGHT / 2) + PENALTY_AREA_DISTANCE, FIELD().right, FIELD().getCenter().y - GOAL_HEIGHT - PENALTY_AREA_DISTANCE, FIELD().right - PENALTY_AREA_DISTANCE);
    }
    
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
    		"goal_kick_l",
    		"goal_kick_r",
    		"drop_ball",
    		"offside_l",
    		"offside_r"
    ));
    
    // List of known stationary objects
    // Although they could theoretically be parsed on the fly, we think it's
    // probably more efficient to parse and store them in advance. They are
    // stationary, after all.
    public static final StationaryObject[] STATIONARY_OBJECTS = {
        // Physical boundary flags
        new Flag("(f t l 50)"),
        new Flag("(f t l 40)"),
        new Flag("(f t l 30)"),
        new Flag("(f t l 20)"),
        new Flag("(f t l 10)"),
        new Flag("(f t 0)"),
        new Flag("(f t r 10)"),
        new Flag("(f t r 20)"),
        new Flag("(f t r 30)"),
        new Flag("(f t r 40)"),
        new Flag("(f t r 50)"),
        new Flag("(f r t 30)"),
        new Flag("(f r t 20)"),
        new Flag("(f r t 10)"),
        new Flag("(f r 0)"),
        new Flag("(f r b 10)"),
        new Flag("(f r b 20)"),
        new Flag("(f r b 30)"),
        new Flag("(f b r 50)"),
        new Flag("(f b r 40)"),
        new Flag("(f b r 30)"),
        new Flag("(f b r 20)"),
        new Flag("(f b r 10)"),
        new Flag("(f b 0)"),
        new Flag("(f b l 10)"),
        new Flag("(f b l 20)"),
        new Flag("(f b l 30)"),
        new Flag("(f b l 40)"),
        new Flag("(f b l 50)"),
        new Flag("(f l b 30)"),
        new Flag("(f l b 20)"),
        new Flag("(f l b 10)"),
        new Flag("(f l 0)"),
        new Flag("(f l t 10)"),
        new Flag("(f l t 20)"),
        new Flag("(f l t 30)"),
        
        // Field corner flags
        new Flag("(f l t)"),
        new Flag("(f r t)"),
        new Flag("(f r b)"),
        new Flag("(f l b)"),
        
        // Field center flags
        new Flag("(f c t)"),
        new Flag("(f c)"),
        new Flag("(f c b)"),
        
        // Penalty area flags
        new Flag("(f p l t)"),
        new Flag("(f p l c)"),
        new Flag("(f p l b)"),
        new Flag("(f p r t)"),
        new Flag("(f p r c)"),
        new Flag("(f p r b)"),
        
        // Goalpost flags
        new Flag("(f g l t)"),
        new Flag("(f g l b)"),
        new Flag("(f g r t)"),
        new Flag("(f g r b)"),
        
        // Goals
        new Goal("(g l)"),
        new Goal("(g r)")
    };
}
