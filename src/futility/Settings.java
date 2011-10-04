package futility;

import futility.Settings;

public class Settings {
    public static final boolean DEBUG = false;
    public static final String HOSTNAME = "localhost";
    public static final int INIT_PORT = 6000;
    public static final String OTHER_TEAM_NAME = "adversary";
    public static final int MSG_SIZE = 4096;
    public static final String TEAM_NAME = "futility";
    public static final String SOCCER_SERVER_VERSION = "15.0";
    public static final int VERBOSITY = 0;
    
    // Default initial position for things with unknown locations
    // Note: the origin is arbitrarily chosen to be the farthest bottom-left
    // point (given standard display conventions) that an object can occupy.
    public static Point INITIAL_POSITION() {
        return new Point(-1.0, -1.0);
    }
    
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
        static int INFO = 2;
        static int DEBUG = 1;
        static int ERROR = 0;
        static int NONE = -1;
    }
    
    public static Rectangle PENALTY_AREA_LEFT() {
        return new Rectangle(FIELD_BUFFER + FIELD_HEIGHT / 2.0 + GOAL_HEIGHT / 2.0 + PENALTY_AREA_DISTANCE, FIELD_BUFFER + PENALTY_AREA_DISTANCE, FIELD_BUFFER - GOAL_HEIGHT / 2 - PENALTY_AREA_DISTANCE, FIELD_BUFFER);
    }
    
    public static Rectangle PENALTY_AREA_RIGHT() {
        return new Rectangle(FIELD().getCenter().y + (GOAL_HEIGHT / 2) + PENALTY_AREA_DISTANCE, FIELD().right, FIELD().getCenter().y - GOAL_HEIGHT - PENALTY_AREA_DISTANCE, FIELD().right - PENALTY_AREA_DISTANCE);
    }
}
