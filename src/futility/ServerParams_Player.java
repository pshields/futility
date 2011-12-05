/** @file ServerParams_Player.java
 * Encapsulates all server parameter constants related to player objects
 * in a way that allows redefinition in the future through object
 * initialization.
 * 
 * @author Team F(utility)
 */

package futility;

/** @class ServerParams_Player
 * Encapsulates constant parameters related to the player. Allows for
 * redefinition through server parameter messages. 
 */
public class ServerParams_Player {
	// Player speed
	public final double PLAYER_ACCEL_MAX;
    public final double PLAYER_SPEED_MAX;
    public final double PLAYER_SPEED_MAX_DELTA_MIN;
    public final double PLAYER_SPEED_MAX_DELTA_MAX;
    public final double PLAYER_DECAY;
    
    // Other Player Parameters
    public final double PLAYER_SIZE;
    public final double PLAYER_WEIGHT;
    public final double KICKABLE_MARGIN;
    
    // Dash Angle / Power
    public final double DASH_POWER_MIN;
    public final double DASH_POWER_MAX;
    public final double DASH_ANGLE_MIN;
    public final double DASH_ANGLE_MAX;
    
    // Kick Angle / Power
    public final double MOMENT_MAX;
    public final double MOMENT_MIN;
    public final double POWER_MAX;
    public final double POWER_MIN;
        
    // Stamina
    public final double STAMINA_CAPACITY;
    public final double STAMINA_INC_MAX;
    public final double STAMINA_MAX;
    
    // Neck Angle / Moment
    public final double NECK_ANGLE_MIN;
    public final double NECK_ANGLE_MAX;
    public final double NECK_MOMENT_MIN;
    public final double NECK_MOMENT_MAX;
    
    /**
     * Default Constructor; builds a Params_Ball data stamp based on the values
     *  in the Builder subclass.
     */
	public ServerParams_Player() {
		PLAYER_ACCEL_MAX = Builder.PLAYER_ACCEL_MAX;
		PLAYER_SPEED_MAX = Builder.PLAYER_SPEED_MAX;
		PLAYER_SPEED_MAX_DELTA_MIN = Builder.PLAYER_SPEED_MAX_DELTA_MIN;
		PLAYER_SPEED_MAX_DELTA_MAX = Builder.PLAYER_SPEED_MAX_DELTA_MAX;
		PLAYER_DECAY     = Builder.PLAYER_DECAY;
		PLAYER_SIZE      = Builder.PLAYER_SIZE;
		PLAYER_WEIGHT    = Builder.PLAYER_WEIGHT;
		KICKABLE_MARGIN  = Builder.KICKABLE_MARGIN;
	    DASH_POWER_MIN   = Builder.DASH_POWER_MIN;
	    DASH_POWER_MAX   = Builder.DASH_POWER_MAX;
	    DASH_ANGLE_MIN   = Builder.DASH_ANGLE_MIN;
	    DASH_ANGLE_MAX   = Builder.DASH_ANGLE_MAX;
		MOMENT_MAX       = Builder.MOMENT_MAX;
		MOMENT_MIN       = Builder.MOMENT_MIN;
		NECK_ANGLE_MIN   = Builder.NECK_ANGLE_MIN;
		NECK_ANGLE_MAX   = Builder.NECK_ANGLE_MAX;
		NECK_MOMENT_MIN  = Builder.NECK_MOMENT_MIN;
		NECK_MOMENT_MAX  = Builder.NECK_MOMENT_MAX;
		POWER_MAX        = Builder.POWER_MAX;
		POWER_MIN        = Builder.POWER_MIN;
		STAMINA_CAPACITY = Builder.STAMINA_CAPACITY;
		STAMINA_INC_MAX  = Builder.STAMINA_INC_MAX;
		STAMINA_MAX      = Builder.STAMINA_MAX;
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
		private static double PLAYER_ACCEL_MAX  =  1.0;
	    private static double PLAYER_SPEED_MAX  =  1.05;
	    private static double PLAYER_SPEED_MAX_DELTA_MIN = 0.0;
	    private static double PLAYER_SPEED_MAX_DELTA_MAX = 0.0;
	    private static double PLAYER_DECAY      =  0.4;
	    private static double PLAYER_SIZE       =  0.3;
	    private static double PLAYER_WEIGHT     =  60.0;
	    private static double KICKABLE_MARGIN   =  0.7;
	    private static double DASH_POWER_MIN    = -100.0;
	    private static double DASH_POWER_MAX    =  100.0;
	    private static double DASH_ANGLE_MIN    = -180.0;
	    private static double DASH_ANGLE_MAX    =  180.0;
	    private static double MOMENT_MIN        = -180.0;
	    private static double MOMENT_MAX        =  180.0;
	    private static double NECK_ANGLE_MIN    = -90.0;
	    private static double NECK_ANGLE_MAX    =  90.0;
	    private static double NECK_MOMENT_MIN   = -180.0;
	    private static double NECK_MOMENT_MAX   =  180.0;
	    private static double POWER_MAX         =  100.0;
	    private static double POWER_MIN         = -100.0;
	    private static double STAMINA_CAPACITY  =  130600.0;
	    private static double STAMINA_INC_MAX   =  45.0;
	    private static double STAMINA_MAX       =  8000.0;	
		
		///////////////////////////////////////////////////////////////////////
		// PARSING
		///////////////////////////////////////////////////////////////////////
		/**
		 * Takes a string array of the form: Name Value <br>
		 * Where Name is the name of a parameter as specified by the server,
		 * and Value is the given value for that parameter. Used in parsing. <br> <br>
		 * Any notable exceptions are automatically logged.
		 * @param args String array containing a parameter and its value.
		 */
		public static void dataParser(String[] args)
		{
			try
			{
				Double val = Double.parseDouble(args[1]);
				
				// Check against every supported parameter.
				if ( args[0].contains("player_speed_max") )
		        	set_speed_max(val);
				
		        else if ( args[0].contains("player_accel_max") )
		        	set_accel_max(val);
				
		        else if ( args[0].contains("player_decay") )
		        	set_decay(val);
				
		        else if ( args[0].contains("player_size") )
		        	set_size(val);
				
		        else if ( args[0].contains("player_weight") )
		        	set_weight(val);
				
		        else if ( args[0].contains("max_dash_angle") )
		        	set_dash_angle_max(val);
				
		        else if ( args[0].contains("min_dash_angle") )
		        	set_dash_angle_min(val);
				
		        else if ( args[0].contains("max_dash_power") )
		        	set_dash_power_max(val);
				
		        else if ( args[0].contains("min_dash_power") )
		        	set_dash_power_min(val);
				
		        else if ( args[0].contains("maxmoment") )
		        	set_moment_max(val);
				
		        else if ( args[0].contains("minmoment") )
		        	set_moment_min(val);
				
		        else if ( args[0].contains("maxneckang") )
		        	set_neck_angle_max(val);
				
		        else if ( args[0].contains("minneckang") )
		        	set_neck_angle_min(val);
				
		        else if ( args[0].contains("maxneckmoment") )
		        	set_neck_moment_max(val);
				
		        else if ( args[0].contains("minneckmoment") )
		        	set_neck_moment_min(val);
				
		        else if ( args[0].contains("maxpower") )
		        	set_power_max(val);
				
		        else if ( args[0].contains("minpower") )
		        	set_power_min(val);
				
		        else if ( args[0].contains("stamina_capacity") )
		        	set_stamina_capacity(val);
				
		        else if ( args[0].contains("stamina_max") )
		        	set_stamina_max(val);
				
		        else if ( args[0].contains("stamina_inc_max") )
		        	set_stamina_inc_max(val);
		        else if ( args[0].contains("kickable_margin"))
		        	set_kickable_margin(val);
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
	    
		///////////////////////////////////////////////////////////////////////
		// SETTERS
		///////////////////////////////////////////////////////////////////////
		/**
		 * @param val Kickable margin
		 */
	    public static void set_kickable_margin(double val) {
			KICKABLE_MARGIN = val;
		}

		/**
		 * @param player_accel_max Maximum acceleration
		 */
	    public static void set_accel_max(double player_accel_max) {
			PLAYER_ACCEL_MAX = player_accel_max;
		}
	    
	    /**
	     * @param player_speed_max Maximum velocity
	     */
		public static void set_speed_max(double player_speed_max) {
			PLAYER_SPEED_MAX = player_speed_max;
		}
		
		/**
		 * @param val Player speed decay
		 */
		public static void set_decay(double val)
		{
			PLAYER_DECAY = val;
		}

		/**
		 * @param val Player size
		 */
		public static void set_size(double val)
		{
			PLAYER_SIZE = val;
		}
		
		/**
		 * @param val Player's weight
		 */
		public static void set_weight(double val)
		{
			PLAYER_WEIGHT = val;
		}
		
		/**
		 * @param val Maximum dash power
		 */
		public static void set_dash_power_max(double val) {
			DASH_POWER_MAX = val;
		}
		
		/**
		 * @param val Minimum dash power
		 */
		public static void set_dash_power_min(double val) {
			DASH_POWER_MIN = val;
		}
		
		/**
		 * @param val Maximum dash angle, degrees
		 */
		public static void set_dash_angle_max(double val) {
			DASH_ANGLE_MAX = val;
		}
		
		/**
		 * @param val Minimum dash angle, degrees
		 */
		public static void set_dash_angle_min(double val) {
			DASH_ANGLE_MIN = val;
		}
		
		/**
		 * @param moment_max Maximum player moment, degrees
		 */
		public static void set_moment_max(double moment_max) {
			MOMENT_MAX = moment_max;
		}
		
		/**
		 * @param moment_min Minimum player moment, degrees
		 */
		public static void set_moment_min(double moment_min) {
			MOMENT_MIN = moment_min;
		}
		
		/**
		 * @param neck_angle_min Minimum neck angle, degrees
		 */
		public static void set_neck_angle_min(double neck_angle_min) {
			NECK_ANGLE_MIN = neck_angle_min;
		}
		
		/**
		 * @param neck_angle_max Maximum neck angle, degrees
		 */
		public static void set_neck_angle_max(double neck_angle_max) {
			NECK_ANGLE_MAX = neck_angle_max;
		}
		
		/**
		 * @param neck_moment_min Minimum neck moment, degrees
		 */
		public static void set_neck_moment_min(double neck_moment_min) {
			NECK_MOMENT_MIN = neck_moment_min;
		}
		
		/**
		 * @param neck_moment_max Maximum neck moment, degrees
		 */
		public static void set_neck_moment_max(double neck_moment_max) {
			NECK_MOMENT_MAX = neck_moment_max;
		}
		
		/**
		 * @param power_max Maximum kicking power
		 */
		public static void set_power_max(double power_max) {
			POWER_MAX = power_max;
		}
		
		/**
		 * @param power_min Minimum kicking power
		 */
		public static void set_power_min(double power_min) {
			POWER_MIN = power_min;
		}
		
		/**
		 * @param stamina_capacity Player's stamina capacity
		 */
		public static void set_stamina_capacity(double stamina_capacity) {
			STAMINA_CAPACITY = stamina_capacity;
		}
		
		/**
		 * @param player_accel_max Player's maximum stamina increment
		 */
		public static void set_stamina_inc_max(double stamina_inc_max) {
			STAMINA_INC_MAX = stamina_inc_max;
		}
		
		/**
		 * @param stamina_max Player's maximum stamina
		 */
		public static void set_stamina_max(double stamina_max) {
			STAMINA_MAX = stamina_max;
		}
	}
}
