/** @file Brain.java
 * Player agent's central logic and memory center.
 * 
 * @author Team F(utility)
 */

package futility;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;

import futility.PlayerRole.Role;

/**
 * This class contains the player's sensory data parsing and strategy computation algorithms.
 */
public class Brain implements Runnable {
    
    /**
     * Enumerator representing the possible strategies that may be used by this
     * player agent.
     * 
     * DASH_TOWARDS_THE_BALL_AND KICK implements a simple soccer strategy:
     * 1. Run towards the ball.
     * 2. Rotate around it until you can see the opponent's goal.
     * 3. Kick the ball towards said goal.
     *
     * LOOK_AROUND tells the player to look around in a circle.
     * 
     * GET_BETWEEN_BALL_AND_GOAL is pretty self explanatory
     */
    public enum Strategy {
    	PRE_KICK_OFF_POSITION,
    	PRE_KICK_OFF_ANGLE,
        DRIBBLE_KICK,
        ACTIVE_INTERCEPT,
        DASH_TOWARDS_BALL_AND_KICK,
        LOOK_AROUND,
        GET_BETWEEN_BALL_AND_GOAL,
        PRE_FREE_KICK_POSITION,
        PRE_CORNER_KICK_POSITION,
        WING_POSITION,
        CLEAR_BALL
    }
	
    ///////////////////////////////////////////////////////////////////////////
    // MEMBER VARIABLES
    ///////////////////////////////////////////////////////////////////////////    
    Client client;
    Player player;
    public int time;
    public PlayerRole.Role role;
    
    // Self info & Play mode
    private String playMode;

    private SenseInfo curSenseInfo, lastSenseInfo;
    public AccelerationVector acceleration;
    public VelocityVector velocity;
    private boolean isPositioned = false;
    
    HashMap<String, FieldObject> fieldObjects = new HashMap<String, FieldObject>(100);
    ArrayDeque<String> hearMessages = new ArrayDeque<String>();
    LinkedList<Player> lastSeenOpponents = new LinkedList<Player>();
    LinkedList<Settings.RESPONSE>responseHistory = new LinkedList<Settings.RESPONSE>();
    private long timeLastSee = 0;
    private long timeLastSenseBody = 0;
    private int lastRan = -1;

    private Strategy currentStrategy = Strategy.LOOK_AROUND;
    private boolean updateStrategy = true;

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////
    /**
     * This is the primary constructor for the Brain class.
     * 
     * @param player a back-reference to the invoking player
     * @param client the server client by which to send commands, etc.
     */
    public Brain(Player player, Client client) {
        this.player = player;
        this.client = client;
        this.curSenseInfo = new SenseInfo();
        this.lastSenseInfo = new SenseInfo();
        this.velocity = new VelocityVector();
        this.acceleration = new AccelerationVector();
        // Load the HashMap
        for (int i = 0; i < Settings.STATIONARY_OBJECTS.length; i++) {
            StationaryObject object = Settings.STATIONARY_OBJECTS[i];
            //client.log(Log.DEBUG, String.format("Adding %s to my HashMap...", object.id));
            fieldObjects.put(object.id, object);
        }
        // Load the response history
        this.responseHistory.add(Settings.RESPONSE.NONE);
        this.responseHistory.add(Settings.RESPONSE.NONE);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // GAME LOGIC
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns this player's acceleration along the x axis. Shortcut function. Remember that
     * accelerations are usually reset to 0 by the soccer server at the beginning of each time
     * step.
     * 
     * @return this player's acceleration along the x axis
     */
    private final double accelX() {
        return this.acceleration.getX();
    }
    
    /**
     * Returns this player's acceleration along the y axis. Shortcut function.
     * 
     * @return this player's acceleration along the y axis
     */
    private final double accelY() {
        return this.acceleration.getY();
    }
    
    /**
     * Returns the direction, in radians, of the player at the current time.
     */
    private final double dir() {
        return Math.toRadians(this.player.direction.getDirection());
    }
    
    /**
     * Assesses the utility of a strategy for the current time step.
     * 
     * @param strategy the strategy to assess the utility of
     * @return an assessment of the strategy's utility in the range [0.0, 1.0]
     */
    private final double assessUtility(Strategy strategy) {
        FieldObject ball = this.getOrCreate(Ball.ID);
        double utility = 0;
        switch (strategy) {
        case PRE_FREE_KICK_POSITION:
        case PRE_CORNER_KICK_POSITION:
        case PRE_KICK_OFF_POSITION:
        	// Check play mode and reposition as necessary.
        	if (this.canUseMove()) {
        		utility = 1 - (isPositioned ? 1 : 0);
        	}
        	break;
        case PRE_KICK_OFF_ANGLE:
        	if (this.isPositioned) {
        	    utility = this.player.team.side == 'r' ?
        			      ( this.canSee(Ball.ID) ? 0.0 : 1.0 ) : 0.0;
            }
        	break;
        case WING_POSITION:
        	if (PlayerRole.isWing(this.role)) {
        	    // A wing should use this strategy if another player on the wing's team
        	    // is closer to the ball, or something like that.
        	    utility = 0.70;
        	}
        	break;	
        case DRIBBLE_KICK:
        	Rectangle OPP_PENALTY_AREA = ( this.player.team.side == 'l' ) ?
        			Settings.PENALTY_AREA_RIGHT : Settings.PENALTY_AREA_LEFT;
        	// If the agent is a goalie, don't dribble!
        	// If we're in the opponent's strike zone, don't dribble! Go for score!
        	if ( this.role == PlayerRole.Role.GOALIE || this.player.inRectangle(OPP_PENALTY_AREA) ) {
        		utility = 0.0;
        	}
        	else {
        		utility = ( this.canKickBall() && this.canSee(
        				    this.player.getOpponentGoalId()) ) ?
        				  0.95 : 0.0;
        	}
        	break;
        case DASH_TOWARDS_BALL_AND_KICK:
            // The striker(s) should usually execute this strategy.
            // The wings, midfielders and defenders should generally execute this strategy when
            // they are close to the ball.
            if (ball.position.getPosition().isUnknown() || this.role == Role.GOALIE) {
                utility = 0.0;
            }
        	else if (this.role == PlayerRole.Role.STRIKER) {
                utility = 0.97;
            }
            else {
                // Utility is high if the player is within ~ 5.0 meters of the ball
                utility = Math.min(0.98, Math.pow(this.getOrCreate(Ball.ID).curInfo.distance / 10.0, -1.0));
            }
            break;
        case LOOK_AROUND:
            // This strategy is almost never necessary. It's used to re-orient players that have
            // become very confused about where they are. This might happen if they are at the edge
            // of the physical boundary, looking out.
            if (this.player.position.getPosition().isUnknown() || this.getOrCreate(Ball.ID).position.getPosition().isUnknown()) {
                utility = 0.98;
            }
            else {
                double playerPosConf = this.player.position.getConfidence(this.time);
                double ballPosConf = this.getOrCreate(Ball.ID).position.getConfidence(this.time);
                double overallConf = Math.min(playerPosConf, ballPosConf);
                utility = 1.0 - overallConf;
            }
            break;
        case GET_BETWEEN_BALL_AND_GOAL:
            // The sweeper(s) should usually execute this strategy. The goalie should also usually
            // execute this strategy, though it's implementation may be slightly different.
        	if (this.role == Role.SWEEPER || this.role == Role.GOALIE) {
        	    if (this.player.distanceTo(ball) > 5.0) {
        	        utility = 0.97;
        	    }
        	    else {
        	        utility = 0.45;
        	    }
        	}
        	else {
        	    utility = 0.4;
        	}
            
        	break;
        case CLEAR_BALL:
            // Defenders should do this if the ball is in their penalty area.
      	    if (this.ownPenaltyArea().contains(ball)) {
       	        return 0.99;
       	    }
       	    else {
       	        utility = 0.45;
       	    }
        default:
            utility = 0;
            break;
        }
        return utility;
    }

    /**
     * Checks if the play mode allows Move commands
     * 
     * @return true if move commands can be issued.
     */
    private final boolean canUseMove() {
    	return (
    			 playMode.equals( "before_kick_off" ) ||
    			 playMode.startsWith( "goal_r_") ||
    			 playMode.startsWith( "goal_l_" ) ||
	    	 	 playMode.startsWith( "free_kick_" ) ||
	    	 	 playMode.startsWith( "corner_kick_" )
    		   ); 
    }
    
    /**
     * A rough estimate of whether the player can kick the ball, dependent
     * on its distance to the ball and whether it is inside the playing field.
     *
     * @return true if the player is on the field and within kicking distance
     */
    public final boolean canKickBall() {
        FieldObject ball = this.getOrCreate(Ball.ID);
        return this.player.inRectangle(Settings.FIELD) && ball.curInfo.time >= this.time - 1 &&
                ball.curInfo.distance < Futil.kickable_radius();
    }

    /**
     * Returns an indication of whether a given ObjectId was seen in the current time step.
     * 
     * @return true if the given ObjectId was seen in the current soccer server time step
     */
    public final boolean canSee(String id) {
        return this.getOrCreate(id).curInfo.time == this.time;
    }

    /**
     * Accelerates the player in the direction of its body.
     * 
     * @param power the power of the acceleration (0 to 100)
     */
    private final void dash(double power) {
        // Update this player's acceleration
        this.acceleration.addPolar(this.dir(), this.effort());
        this.client.sendCommand(Settings.Commands.DASH, Double.toString(power));
    }

    /**
     * Accelerates the player in the direction of its body, offset by the given
     * angle.
     * 
     * @param power the power of the acceleration (0 to 100)
     * @param offset an offset to be applied to the player's direction,
     * yielding the direction of acceleration
     */
    public final void dash(double power, double offset) {
        this.acceleration.addPolar(this.dir() + offset, this.edp(power));
        client.sendCommand(Settings.Commands.DASH, Double.toString(power), Double.toString(offset));
    }
    
    /**
     * Determines the strategy with the current highest utility.
     * 
     * @return the strategy this brain thinks is optimal for the current time step
     */
    private final Strategy determineOptimalStrategy() {
        Strategy optimalStrategy = this.currentStrategy;
        double bestUtility = 0;
        if (this.updateStrategy) {
            for (Strategy strategy : Strategy.values()) {
                double utility = this.assessUtility(strategy);
                if (utility > bestUtility) {
                    bestUtility = utility;
                    optimalStrategy = strategy;
                }
            }
        }
        Log.d("Picked strategy " + optimalStrategy + " with utility " + bestUtility);
        return optimalStrategy;
    }
    /**
     * Returns this player's effective dash power. Refer to the soccer server manual for more information.
     * 
     * @return this player's effective dash power
     */
    private final double edp(double power) {
        return this.effort() * Settings.DASH_POWER_RATE * power;
    }
    
    /**
     * Returns an effort value for this player. If one wasn't received this time step, we guess.
     */
    private final double effort() {
        return this.curSenseInfo.effort;
    }
    
    /**
     * Executes a strategy for the player in the current time step.
     * 
     * @param strategy the strategy to execute
     */
    private final void executeStrategy(Strategy strategy) {
    	FieldObject ball = this.getOrCreate(Ball.ID);
        FieldObject opponentGoal = this.getOrCreate(this.player.getOpponentGoalId());
        FieldObject ownGoal = this.ownGoal();
    	
        switch (strategy) {
        case PRE_FREE_KICK_POSITION:
        	if (playMode.equals("free_kick_l")) {
            	this.move(Settings.FREE_KICK_L_FORMATION[player.number]);
        	} else {
            	this.move(Settings.FREE_KICK_R_FORMATION[player.number]);
        	}
        	this.isPositioned = true;       
        	break;
        case PRE_CORNER_KICK_POSITION:
        	if (playMode.equals("corner_kick_l")) {
            	this.move(Settings.CORNER_KICK_L_FORMATION[player.number]);
        	} else {
            	this.move(Settings.CORNER_KICK_R_FORMATION[player.number]);
        	}
        	this.isPositioned = true;   
        	break;
        case PRE_KICK_OFF_POSITION:
        	this.move(Settings.FORMATION[player.number]);
        	this.isPositioned = true;     
        	break;
        case PRE_KICK_OFF_ANGLE:
        	this.turn(30);
        	break;
        case WING_POSITION:
        	Point position = Futil.estimatePositionOf(ball, 3, this.time).getPosition();
        	if (this.role == PlayerRole.Role.LEFT_WING) {
        	    position.update(position.getX(), position.getY() + 4.0);
        	}
        	else {
        	    position.update(position.getX(), position.getY() - 4.0);
        	}
        	this.dashTo(position);
        	break;
        case DRIBBLE_KICK:
        	/*
        	 *  Find a dribble angle, weighted by presence of opponents.
        	 *  Determine dribble velocity based on current velocity.
        	 *  Dribble!
        	 */
        	
			// Predict next position:
        	Vector2D v_new = Futil.estimatePositionOf(this.player, 2, this.time).getPosition().asVector();
			Vector2D v_target = v_new.add( findDribbleAngle() );
        	Vector2D v_ball = v_target.add( new Vector2D( -1 * ball.position.getX(),
        			                        -1 * ball.position.getY() ) );
        	
			double traj_power = Math.min(Settings.PLAYER_PARAMS.POWER_MAX,
	                ( v_ball.magnitude() / (1 + Settings.BALL_PARAMS.BALL_DECAY ) ) * 10); // values of 1 or 2 do not give very useful kicks.
			this.kick(traj_power, Futil.simplifyAngle(Math.toDegrees(v_ball.direction())));
        	break;
        case DASH_TOWARDS_BALL_AND_KICK:
            if (this.canKickBall()) {
                this.kick(100.0, this.player.relativeAngleTo(opponentGoal));
            }
            else {
                double approachAngle = Futil.simplifyAngle(this.player.relativeAngleTo(ball));
                double dashPower = Math.min(100.0, 800.0 / this.player.distanceTo(ball));
                double tolerance = Math.max(10.0, 100.0 / ball.curInfo.distance);
                if (Math.abs(approachAngle) > tolerance) {
                    this.turn(approachAngle);
                }
                else {
                    dash(dashPower, approachAngle);
                }
            }
            break;
        case LOOK_AROUND:
            turn(90.0);
            break;
        case GET_BETWEEN_BALL_AND_GOAL:
            if (this.role == Role.GOALIE) {
                double targetFacingDir = 0.0;
                double x = - (Settings.FIELD_WIDTH / 2.0 - 1.0);
                if (this.player.team.side == 'r') {
                    targetFacingDir = -180.0;
                    x = x * -1.0;
                }
                if (Math.abs(this.dir() - targetFacingDir) > 10.0) {
                    this.turnTo(targetFacingDir);
                }
                else {
                    double y = ball.position.getY() / (Settings.FIELD_HEIGHT / Settings.GOAL_HEIGHT);
                    Point target = new Point(x, y);
                    if (this.player.position.getPosition().distanceTo(target) > 1.0) {
                        this.dash(60.0, this.player.relativeAngleTo(target));
                    }                   
                }
            }
            else {
            	Point midpoint = ownGoal.position.getPosition().midpointTo(ball.position.getPosition());
            	double distanceAway = this.player.position.getPosition().distanceTo(midpoint);
            	if (distanceAway > 5.0) {
            	    this.dashTo(midpoint, Math.min(100.0, distanceAway * 10.0));
            	}
            }
        	break;
        case CLEAR_BALL:
   			if (canKickBall()) {
   			    double kickDir;
   			    if (this.player.position.getY() > 0.0) {
   			        kickDir = this.player.relativeAngleTo(90.0);
   			    }
   			    else {
   			        kickDir = this.player.relativeAngleTo(-90.0);
   			    }
   				this.kick(80.0, kickDir);
   			}
   			else {
   			    Point target = Futil.estimatePositionOf(ball, 1, this.time).getPosition();
   			    if (this.player.position.getPosition().distanceTo(target) > Futil.kickable_radius()) {
   			        this.dashTo(target, 80.0);
   			    }
   			}
        	break;
        default:
            break;
        }
    }
    
    /**
     * Finds the optimum angle to kick the ball toward within a kickable
     * area.
     * 
     * @param p Point to build angle from
     * @return the vector to dribble toward.
     */
    private final Vector2D findDribbleAngle() {
    	// TODO STUB: Need algorithm for a weighted dribble angle.
    	double d_length = Math.max(1.0, Futil.kickable_radius() );
    	
    	// 5.0 is arbitrary in case nothing is visible; attempt to kick
    	//   toward the lateral center of the field.
    	double d_angle = 5.0 * -1.0 * Math.signum( this.player.position.getY() );
    	
    	// If opponents are visible, try to kick away from them.
    	if ( !lastSeenOpponents.isEmpty() )
    	{
    		double weight = 0.0d;
    		double w_angle = 0.0d;
    		for ( Player i : lastSeenOpponents )
    		{
    			double i_angle = player.relativeAngleTo(i);
    			double new_weight = Math.max(weight, Math.min(1.0,
    					    1 / player.distanceTo(i) * Math.abs(
    					    		1 / ( i_angle == 0.0 ? 1.0 : i_angle ) ) ) );
    			if ( new_weight > weight )
    				w_angle = i_angle;
    		}
    		
    		// Keep the angle within [-90,90]. Kick forward, not backward!
    		d_angle = Math.max( Math.abs( w_angle ) - 180, -90 ) * Math.signum( w_angle );
    	}
    	// Otherwise kick toward the goal.
    	else if ( this.canSee( this.player.getOpponentGoalId() ) )
    		d_angle += this.player.relativeAngleTo(
    				this.getOrCreate(this.player.getOpponentGoalId()));
    	Vector2D d_vec = new Vector2D(0.0, 0.0);
    	d_vec = d_vec.addPolar(Math.toRadians(d_angle), d_length); // ?!
    	return d_vec;	
    	
    	/*
    	 * Proposed algorithm:
    	 * 
    	 * Finding highest weight opponent:
    	 *   W_i = ( 1 / opponent_distance ) * abs( 1 / opponent_angle ) ) 
    	 * 
    	 * Finding RELATIVE angle:
    	 *   d_angle = max( abs( Opp_w_relative_angle ) - 180, -90 )
    	 *              * signum( Opp_w_relative_angle )  
    	 */
    }
    
    /**
     * Gets a field object from fieldObjects, or creates it if it doesn't yet exist.
     * 
     * @param id the object's id
     * @return the field object
     */
    private final FieldObject getOrCreate(String id) {
        if (this.fieldObjects.containsKey(id)) {
            return this.fieldObjects.get(id);
        }
        else {
            return FieldObject.create(id);
        }
    }
    
    /**
     * Infers the position and direction of this brain's associated player given two boundary flags
     * on the same side seen in the current time step.
     * 
     * @param o1 the first flag
     * @param o2 the second flag
     */
    private final void inferPositionAndDirection(FieldObject o1, FieldObject o2) {
        // x1, x2, y1 and y2 are relative Cartesian coordinates to the flags
        double x1 = Math.cos(Math.toRadians(o1.curInfo.direction)) * o1.curInfo.distance;
        double y1 = Math.sin(Math.toRadians(o1.curInfo.direction)) * o1.curInfo.distance;
        double x2 = Math.cos(Math.toRadians(o2.curInfo.direction)) * o2.curInfo.distance;
        double y2 = Math.sin(Math.toRadians(o2.curInfo.direction)) * o2.curInfo.distance;
        double direction = -Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
        // Need to reverse the direction if looking closer to west and using horizontal boundary flags
        if (o1.position.getY() == o2.position.getY()) {
            if (Math.signum(o2.position.getX() - o1.position.getX()) != Math.signum(x2 - x1)) {
                direction += 180.0;
            }
        }
        // Need to offset the direction by +/- 90 degrees if using vertical boundary flags
        else if (o1.position.getX() == o1.position.getX()) {
            if (Math.signum(o2.position.getY() - o1.position.getY()) != Math.signum(x2 - x1)) {
                direction += 270.0;
            }
            else {
                direction += 90.0;
            }
        }
        this.player.direction.update(Futil.simplifyAngle(direction), 0.95, this.time);
        double x = o1.position.getX() - o1.curInfo.distance * Math.cos(Math.toRadians(direction + o1.curInfo.direction));
        double y = o1.position.getY() - o1.curInfo.distance * Math.sin(Math.toRadians(direction + o1.curInfo.direction));
        double distance = this.player.position.getPosition().distanceTo(new Point(x, y)); 
        this.player.position.update(x, y, 0.95, this.time);
    }
    
    /**
     * Indication of if this player is a defender.
     * 
     * @return true if this player is a defender
     */
    public boolean isDefender() {
        return this.role == Role.LEFT_DEFENDER || this.role == Role.RIGHT_DEFENDER;
    }

    /**
     * Moves the player to the specified coordinates (server coords).
     * 
     * @param p the Point object to pass coordinates with (must be in server coordinates).
     */
    public void move(Point p)
    {
    	move(p.getX(), p.getY());
    }
    
    /**
     * Moves the player to the specified coordinates (server coords).
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void move(double x, double y) {
        client.sendCommand(Settings.Commands.MOVE, Double.toString(x), Double.toString(y));
        this.player.position.update(x, y, 1.0, this.time);
    }
    
    /**
     * Overrides the strategy selection system. Used in tests.
     */
    public void overrideStrategy(Strategy strategy) {
        this.currentStrategy = strategy;
        this.updateStrategy = false;
    }
    
    /**
     * Kicks the ball in the direction of the player.
     * 
     * @param power the level of power with which to kick (0 to 100)
     */
    public void kick(double power) {
        client.sendCommand(Settings.Commands.KICK, Double.toString(power));
    }
    
    /**
     * Kicks the ball in the player's direction, offset by the given angle.
     * 
     * @param power the level of power with which to kick (0 to 100)
     * @param offset an angle in degrees to be added to the player's direction, yielding the direction of the kick
     */
    public void kick(double power, double offset) {
        client.sendCommand(Settings.Commands.KICK, Double.toString(power), Double.toString(offset));
    }
    
    /**
     * Parses a message from the soccer server. This method is called whenever
     * a message from the server is received.
     * 
     * @param message the message (string), exactly as it was received
     */
    public void parseMessage(String message) {
        long timeReceived = System.currentTimeMillis();
        message = Futil.sanitize(message);
        // Handle `sense_body` messages
        if (message.startsWith("(sense_body")) {
        	curSenseInfo.copy(lastSenseInfo);
        	curSenseInfo.reset();
        	
            this.timeLastSenseBody = timeReceived;
            curSenseInfo.time = Futil.extractTime(message);
            this.time = curSenseInfo.time;       

            String parts[] = message.split("\\(");
            for ( String i : parts ) // for each structured argument:
            {
            	// Clean the string, and break it down into the base arguments.
            	String nMsg = i.split("\\)")[0].trim();
            	if ( nMsg.isEmpty() ) continue;
            	String nArgs[] = nMsg.split("\\s");
            	
            	// Check for specific argument types; ignore unknown arguments.
            	if ( nArgs[0].contains("view_mode") )
            	{ // Player's current view mode
            		curSenseInfo.viewQuality = nArgs[1];
            		curSenseInfo.viewWidth = nArgs[2];
            	}
            	else if ( nArgs[0].contains("stamina") )
            	{ // Player's stamina data
            		curSenseInfo.stamina = Double.parseDouble(nArgs[1]);
            		curSenseInfo.effort = Double.parseDouble(nArgs[2]);
            		curSenseInfo.staminaCapacity = Double.parseDouble(nArgs[3]);
            	}
            	else if ( nArgs[0].contains("speed") )
            	{ // Player's speed data
            		curSenseInfo.amountOfSpeed = Double.parseDouble(nArgs[1]);
            		curSenseInfo.directionOfSpeed = Double.parseDouble(nArgs[2]);
            		// Update velocity variable
            		double dir = this.dir() + Math.toRadians(curSenseInfo.directionOfSpeed);
            		this.velocity.setPolar(dir, curSenseInfo.amountOfSpeed);
            	}
            	else if ( nArgs[0].contains("head_angle") )
            	{ // Player's head angle
            		curSenseInfo.headAngle = Double.parseDouble(nArgs[1]);
            	}
            	else if ( nArgs[0].contains("ball") || nArgs[0].contains("player")
            			       || nArgs[0].contains("post") )
            	{ // COLLISION flags; limitation of this loop approach is we
            	  //   can't handle nested parentheses arguments well.
            	  // Luckily these flags only occur in the collision structure.
            		curSenseInfo.collision = nArgs[0];
            	}
            }

            // If the brain has responded to two see messages in a row, it's time to respond to a sense_body.
            if (this.responseHistory.get(0) == Settings.RESPONSE.SEE && this.responseHistory.get(1) == Settings.RESPONSE.SEE) {
                this.run();
                this.responseHistory.push(Settings.RESPONSE.SENSE_BODY);
                this.responseHistory.removeLast();
            }
        }
        // Handle `hear` messages
        else if (message.startsWith("(hear"))
        {
        	String parts[] = message.split("\\s");
        	this.time = Integer.parseInt(parts[1]);
        	if ( parts[2].startsWith("s") || parts[2].startsWith("o") || parts[2].startsWith("c") )
        	{
        		// TODO logic for self, on-line coach, and trainer coach.
        		// Self could potentially be for feedback,
        		// On-line coach will require coach language parsing,
        		// And trainer likely will as well. Outside of Sprint #2 scope.
        		return;
        	}
        	else
        	{
        		// Check for a referee message, otherwise continue.
        		String nMsg = parts[3].split("\\)")[0];         // Retrieve the message.
        		if ( nMsg.startsWith("goal_l_") )
        			nMsg = "goal_l_";
        		else if ( nMsg.startsWith("goal_r_") )
        			nMsg = "goal_r_";
        		if ( parts[2].startsWith("r")                   // Referee;
        			   && Settings.PLAY_MODES.contains(nMsg) )  // Play Mode?
        		{
            		playMode = nMsg;
            		this.isPositioned = false;
        		}
            	else
            		hearMessages.add( nMsg );
        	}
        }
        // Handle `see` messages
        else if (message.startsWith("(see")) {
            this.timeLastSee = timeReceived;
            this.time = Futil.extractTime(message);
            LinkedList<String> infos = Futil.extractInfos(message);
            lastSeenOpponents.clear();
            for (String info : infos) {
                String id = Futil.extractId(info);
                if (Futil.isUniqueFieldObject(id)) {
                    FieldObject obj = this.getOrCreate(id);
                    obj.update(this.player, info, this.time);
                    this.fieldObjects.put(id, obj);
                    if ( id.startsWith("(p \"") && !( id.startsWith(this.player.team.name, 4) ) )
                    	lastSeenOpponents.add( (Player)obj );
                }
            }
            // Immediately run for the current step. Since our computations takes only a few
            // milliseconds, it's okay to start running over half-way into the 100ms cycle.
            // That means two out of every three time steps will be executed here.
            this.updatePositionAndDirection();
            this.run();
            // Make sure we stay in sync with the mid-way `see`s
            if (this.timeLastSee - this.timeLastSenseBody > 30) {
                this.responseHistory.clear();
                this.responseHistory.add(Settings.RESPONSE.SEE);
                this.responseHistory.add(Settings.RESPONSE.SEE);
            }
            else {
                this.responseHistory.add(Settings.RESPONSE.SEE);
                this.responseHistory.removeLast();
            }
        }
        // Handle init messages
        else if (message.startsWith("(init")) {
            String[] parts = message.split("\\s");
            char teamSide = message.charAt(6);
            if (teamSide == Settings.LEFT_SIDE) {
                player.team.side = Settings.LEFT_SIDE;
                player.otherTeam.side = Settings.RIGHT_SIDE;
            }
            else if (teamSide == Settings.RIGHT_SIDE) {
                player.team.side = Settings.RIGHT_SIDE;
                player.otherTeam.side = Settings.LEFT_SIDE;
            }
            else {
                // Raise error
                Log.e("Could not parse teamSide.");
            }
            player.number = Integer.parseInt(parts[2]);
            if(role != Role.GOALIE) this.role = Settings.PLAYER_ROLES[this.player.number - 1];
            playMode = parts[3].split("\\)")[0];
        }
        else if (message.startsWith("(server_param")) {
        	parseServerParameters(message);
        }
    }
    
    /**
     * Parses the initial parameters received from the server.
     *     
     * @param message the parameters message received from the server
     */
    public void parseServerParameters(String message)
    {
        String parts[] = message.split("\\(");
        for ( String i : parts ) // for each structured argument:
        {
        	// Clean the string, and break it down into the base arguments.
        	String nMsg = i.split("\\)")[0].trim();
        	if ( nMsg.isEmpty() ) continue;
        	String nArgs[] = nMsg.split("\\s");
        	
        	// Check for specific argument types; ignore unknown arguments.
        	if (nArgs[0].startsWith("dash_power_rate")) 
        	    Settings.setDashPowerRate(Double.parseDouble(nArgs[1]));
        	if ( nArgs[0].startsWith("goal_width") )
        		Settings.setGoalHeight(Double.parseDouble(nArgs[1]));
        	// Ball arguments:
        	else if ( nArgs[0].startsWith("ball") )
        		ServerParams_Ball.Builder.dataParser(nArgs);
        	// Player arguments:
        	else if ( nArgs[0].startsWith("player") || nArgs[0].startsWith("min")
        			|| nArgs[0].startsWith("max") )
        		ServerParams_Player.Builder.dataParser(nArgs);
        }
        
        // Rebuild all parameter objects with updated parameters.
        Settings.rebuildParams();
    }
    
    /**
     * Returns this player's team's goal.
     * 
     * @return this player's team's goal
     */
    public final FieldObject ownGoal() {
        return this.getOrCreate(this.player.getGoalId());
    }
    
    /**
     * Returns the penalty area of this player's team's goal.
     * 
     * @return the penalty area of this player's team's goal
     */
    public final Rectangle ownPenaltyArea() {
        if (this.player.team.side == 'l') {
            return Settings.PENALTY_AREA_LEFT;
        }
        else {
            return Settings.PENALTY_AREA_RIGHT;
        }
    }
    
    /**
     * Responds for the current time step.
     */
    public void run() {
        int expectedNextRun = this.lastRan + 1;
        if (this.time > this.lastRan + 1) {
            Log.e("Brain for player " + this.player.render() + " did not run during time step " + expectedNextRun + ".");
        }
        this.lastRan = this.time;
        this.acceleration.reset();
        this.currentStrategy = this.determineOptimalStrategy();
        this.executeStrategy(this.currentStrategy);
    }
    
    /** 
     * Adds the given angle to the player's current direction.
     * 
     * @param offset an angle in degrees to add to the player's current direction
     */
    public final void turn(double offset) {
        double moment = Futil.toValidMoment(offset);
        client.sendCommand(Settings.Commands.TURN, moment);
        // TODO Potentially take magnitude of offset into account in the
        // determination of the new confidence in the player's position.
        player.direction.update(player.direction.getDirection() + moment, 0.95 * player.direction.getConfidence(this.time), this.time);
    }
    
    /** 
     * Updates the player's current direction to be the given direction.
     * 
     * @param direction angle in degrees, assuming soccer server coordinate system
     */
    public final void turnTo(double direction) {
        this.turn(this.player.relativeAngleTo(direction));
    }
    
    /**
     * Send commands to move the player to a point at maximum power
     * @param point the point to move to
     */
    private final void dashTo(Point point){
    	dashTo(point, 50.0);
    }
    
    /**
     * Send commands to move the player to a point with the given power
     * @param point to move towards
     * @param power to move at
     */
    private final void dashTo(Point point, double power){
        double tolerance = Math.max(10.0, 100.0 / this.player.position.getPosition().distanceTo(point));
    	if (Math.abs(this.player.relativeAngleTo(point)) > tolerance) {
    		turn(this.player.relativeAngleTo(point));
    	}
    	else {
    		dash(power);
    	}
    }
    
    /**
     * Updates this this brain's belief about the associated player's position and direction
     * at the current time step. This method should be called immediately after parsing a `see`
     * message, and only then. 
     */
    private final void updatePositionAndDirection() {
        // Infer from the most-recent `see` if it happened in the current time-step
        for (int i = 0; i < 4; i++) {
            LinkedList<FieldObject> flagsOnSide = new LinkedList<FieldObject>();
            for (String id : Settings.BOUNDARY_FLAG_GROUPS[i]) {
                FieldObject flag = this.fieldObjects.get(id);
                if (flag.curInfo.time == this.time) {
                    flagsOnSide.add(flag);
                }
                else {
                    //Log.i("Flag " + id + "last updated at time " + flag.info.time + ", not " + this.time);
                }
                if (flagsOnSide.size() > 1) {
                    this.inferPositionAndDirection(flagsOnSide.poll(), flagsOnSide.poll());
                    return;
                }
            }
        }
     }
    
    /**
     * @return {@link Settings#PENALTY_AREA_LEFT} if player is on the left team, or {@link Settings#PENALTY_AREA_RIGHT} if on the right team.
     */
    final public Rectangle getMyPenaltyArea(){
    	if(player.team == null) throw new NullPointerException("Player team not initialized while getting penelty area.");
    	return player.team.side == 'l' ? Settings.PENALTY_AREA_LEFT : Settings.PENALTY_AREA_RIGHT; 
    }
    
    /**
     * Returns this player's x velocity. Shortcut function.
     * 
     * @return this player's x velocity
     */
    private final double velX() {
        return this.velocity.getX();
    }
    
    /**
     * Returns this player's y velocity. Shortcut function.
     * 
     * @return this player's y velocity
     */
    private final double velY() {
        return this.velocity.getY();
    }
    
    /**
     * Returns this player's x.
     * 
     * @return this player's x-coordinate
     */
    private final double x() {
        return this.player.position.getPosition().getX();
    }
    
    /**
     * Returns this player's y.
     * 
     * @return this player's y coordinate
     */
    private final double y() {
        return this.player.position.getPosition().getY();
    }
}
