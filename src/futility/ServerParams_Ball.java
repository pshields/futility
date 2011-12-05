/** @file ServerParams_Ball.java
 * Encapsulates all server parameter constants related to the ball object
 * in a way that allows redefinition in the future through object
 * initialization.
 * 
 * @author Team F(utility)
 */

package futility;

/** @class ServerParams_Ball
 * Encapsulates constant parameters related to the ball. Allows for
 * redefinition through server parameter messages. 
 */
public class ServerParams_Ball {
    public final double BALL_SIZE;
    public final double BALL_DECAY;
    public final double BALL_RAND;
    public final double BALL_WEIGHT;
    public final double BALL_SPEED_MAX;
    public final double BALL_ACCEL_MAX;
    public final double BALL_STUCK_AREA;
    
    /**
     * Default Constructor; builds a Params_Ball data stamp based on the values
     *  in the Builder subclass.
     */
    public ServerParams_Ball()
    {
    	BALL_SIZE       = Builder.BALL_SIZE;
    	BALL_DECAY      = Builder.BALL_DECAY;
    	BALL_RAND       = Builder.BALL_RAND;
    	BALL_WEIGHT     = Builder.BALL_WEIGHT;
    	BALL_SPEED_MAX  = Builder.BALL_SPEED_MAX;
    	BALL_ACCEL_MAX  = Builder.BALL_ACCEL_MAX;
    	BALL_STUCK_AREA = Builder.BALL_STUCK_AREA;
    }
	
	/** @class Builder
	 * Default values for this parameter class are stored here. The provided
	 * setters and parsing routines allow for safe modification without
	 * affecting parameters that may already be in use. <br> <br>
	 * To make use of the new parameter data, initialize the parent Parameter
	 * object.
	 */
	public static class Builder
	{
		// KNOWN DEFAULT VALUES
		// These can be changed by calling individual setters or dataParser()
		private static double BALL_SIZE       = 0.085;
		private static double BALL_DECAY      = 0.94;
		private static double BALL_RAND       = 0.05;
		private static double BALL_WEIGHT     = 0.2;
		private static double BALL_SPEED_MAX  = 3.0;
		private static double BALL_ACCEL_MAX  = 2.7;
		private static double BALL_STUCK_AREA = 3.0;		
		
		///////////////////////////////////////////////////////////////////////
		// PARSING
		///////////////////////////////////////////////////////////////////////
		/**
		 * Takes a string array of the form: Name Value <br>
		 * Where Name is the name of a parameter as specified by the server,
		 * and Value is the given value for that parameter. Used in parsing. <br> <br>
		 * Any notable exceptions are automatically logged.
		 * 
		 * @param args String array containing a parameter and its value.
		 */
		public static void dataParser(String[] args)
		{
			try
			{
				Double val = Double.parseDouble(args[1]);
				
				// Check against every supported parameter.
				if ( args[0].contains("ball_size") )
		        	set_size(val);
				
		        else if ( args[0].contains("ball_decay") )
		        	set_decay(val);
				
		        else if ( args[0].contains("ball_rand") )
		        	set_rand(val);
				
		        else if ( args[0].contains("ball_weight") )
		        	set_weight(val);
				
		        else if ( args[0].contains("ball_accel_max") )
		        	set_accel_max(val);
				
		        else if ( args[0].contains("ball_speed_max") )
		        	set_speed_max(val);
				
		        else if ( args[0].contains("ball_stuck_area") )
		        	set_stuck_area(val);
				
			}
			catch ( NullPointerException ne )
			{
				Log.e("No server parameter specified");
			}
			catch ( ArrayIndexOutOfBoundsException ae )
			{
				Log.e("Malformed server parameter; no value specified");
			}
			catch ( NumberFormatException nfe )
			{
				Log.d("Server parameter NaN.");
			}
		}
		
		/**
		 * Sets the size of the ball.
		 * 
		 * @param ball_size The size of the ball
		 */
		public static void set_size(double ball_size) {
			BALL_SIZE = ball_size;
		}
		
		/**
		 * Sets the velocity decay of the ball.
		 * 
		 * @param ball_decay velocity decay of the ball
		 */
		public static void set_decay(double ball_decay) {
			BALL_DECAY = ball_decay;
		}
		
		/**
		 * @param ball_rand Ball rand flag (unknown purpose)
		 */
		public static void set_rand(double ball_rand) {
			BALL_RAND = ball_rand;
		}
		
		/**
		 * @param ball_weight Weight of the ball
		 */
		public static void set_weight(double ball_weight) {
			BALL_WEIGHT = ball_weight;
		}
		
		/**
		 * @param ball_speed_max Maximum velocity of the ball
		 */
		public static void set_speed_max(double ball_speed_max) {
			BALL_SPEED_MAX = ball_speed_max;
		}
		
		/**
		 * @param ball_accel_max Maximum acceleration of the ball
		 */
		public static void set_accel_max(double ball_accel_max) {
			BALL_ACCEL_MAX = ball_accel_max;
		}
		
		/**
		 * @param ball_stuck_area "stuck" region size of the ball
		 */
		public static void set_stuck_area(double ball_stuck_area) {
			BALL_STUCK_AREA = ball_stuck_area;
		}
	}
}
