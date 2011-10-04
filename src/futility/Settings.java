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
    
    // Field geometry
    public static final double FIELD_WIDTH = 105.0;
    public static final double FIELD_HEIGHT = 68.0;
    public static final double FIELD_BUFFER = 5.0;
    
    // Other constants
    public static final char LEFT_SIDE = 'l';
    public static final char RIGHT_SIDE = 'r';
    
    public static class LOG_LEVELS {
        static int INFO = 2;
        static int DEBUG = 1;
        static int ERROR = 0;
        static int NONE = -1;
    }
}
