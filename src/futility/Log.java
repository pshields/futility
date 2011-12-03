/** @file Log.java
 * Logging class.
 * 
 * @author Team F(utility)
 */ 

package futility;

/**
 * Logging class.
 */
public final class Log {

	//verbosity levels for logging messages
	public static int DEBUG = 2;
    public static int INFO = 1;
    public static int ERROR = 0;
    public static int NONE = -1;
    
    /**
     * Basic logging shortcut.
     * 
     * @param message what to send to the standard output
     */
    private static void log(String message){
    	 System.out.println(message);
    }
    
    /**
     * Logs with verbosity. Allows the inclusion of a verbosity value with a
     * message.
     * 
     * @param verbosity the minimum verbosity level the message should display at
     * @param message the message to display
     */
    public static void log(int verbosity, String message) {
        if (Settings.VERBOSITY >= verbosity) {
            if (verbosity == Log.DEBUG) {
                log("DEBUG: " + message);
            }
            else if (verbosity == Log.INFO) {
                log("INFO: " + message);
            }
            else if (verbosity == Log.ERROR) {
                log("ERROR: " + message);
            }
            else {
                log(String.format("UNKNOWN VERBOSITY LEVEL %d", verbosity) + message);
            }
        }
    }
    
    /**
     * Convenience method for calling {@link #log(int, String)} with 
     * the {@link #DEBUG} flag
     * @param message the message to display
     */
    public static void d(String message){
    	log(DEBUG, message);
    }
    
    /**
     * Convenience method for calling {@link #log(int, String)} with the {@link #INFO} flag
     * @param message the message to display
     */
    public static void i(String message){
    	log(INFO, message);
    }
    
    /**
     * Convenience method for calling {@link #log(int, String)} with the {@link #ERROR} flag
     * @param message the message to display
     */
    public static void e(String message){
    	log(ERROR, message);
    }
}
