/** @file Brain.java
 * Player agent's central logic and memory center.
 * 
 * @author Team F(utility)
 */

package futility;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class contains the player's sensory data parsing and strategy computation algorithms.
 */
public class Brain implements Runnable {
    
    /**
     * Enumerator representing the possible strategies that may be used by this
     * player agent.
     * 
     * DASH_AROUND_THE_FIELD_CLOCKWISE tells the player to dash around
     *   the field boundaries clockwise.
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
        DASH_AROUND_THE_FIELD_CLOCKWISE,
        DASH_TOWARDS_BALL_AND_KICK,
        LOOK_AROUND,
        GET_BETWEEN_BALL_AND_GOAL,
        GOALIE_CATCH_BALL,
        PRE_FREE_KICK_POSITION,
        PRE_CORNER_KICK_POSITION,
        TEST_TURNS
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
        double utility = 0;
        switch (strategy) {
        case PRE_FREE_KICK_POSITION:
        case PRE_CORNER_KICK_POSITION:
        case PRE_KICK_OFF_POSITION:
        	// Check play mode, reposition as necessary.
        	if ( canUseMove() )
        		utility = 1 - (isPositioned ? 1 : 0);
        	break;
        case PRE_KICK_OFF_ANGLE:
        	if ( isPositioned )
        	{
        	    utility = this.player.team.side == 'r' ?
        			      ( this.canSee("(b)") ? 0.0 : 1.0 ) : 0.0;
            }
        	break;
        case DRIBBLE_KICK: // Unimplemented
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
        case DASH_AROUND_THE_FIELD_CLOCKWISE:
            utility = 0.93;
            break;
        case DASH_TOWARDS_BALL_AND_KICK:
            if (this.role == PlayerRole.Role.STRIKER) {
                utility = 0.95;
            }
            else {
                // Utility is high if the player is within ~ 5.0 meters of the ball
                utility = Math.min(0.95, Math.pow(this.getOrCreate("(b)").curInfo.distance / 5.0, -1.0));
            }
            break;
        case LOOK_AROUND:
            if (this.player.position.getPosition().isUnknown()) {
                utility = 1.0;
            }
            else {
                utility = 1 - this.player.position.getConfidence(this.time);
            }
            break;
        case GET_BETWEEN_BALL_AND_GOAL:
        	// estimate our confidence of where the ball and the player are on the field
        	double ballConf = this.getOrCreate("(b)").position.getConfidence(this.time);
        	double playerConf = this.player.position.getConfidence(this.time);
        	double conf = (ballConf + playerConf) / 2;

        	double initial = 1;
        	if (this.player.team.side == Settings.LEFT_SIDE) {
        		if (this.getOrCreate("(b)").position.getX() < this.player.position.getX()) {
        			initial = 0.6;
        		} else {
        			initial = 0.9;
        		}
        	} else {
        		if (this.getOrCreate("(b)").position.getX() > this.player.position.getX()) {
        			initial = 0.6;
        		} else {
        			initial = 0.9;
        		}
        	}

        	if (this.role != PlayerRole.Role.GOALIE) {
        		initial *= 0.9;
        	}
        	
        	utility = initial * conf;
        	break;
        case TEST_TURNS:
            if (this.currentStrategy == Strategy.TEST_TURNS && !this.updateStrategy) {
                utility = 1.0;
            }
            else {
                utility = 0.0;
            }
            break;
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
     * A rough estimate of whether the player can catch the ball, dependent
     * on their distance to the ball, whether they are a goalie, and whether
     * they are within their own penalty area.
     *
     * @return true if the player can catch the ball
     */
    public final boolean canCatchBall() {
    	if (this.role != PlayerRole.Role.GOALIE) {
    		return false;
    	}

    	//TODO: check if ball is within catchable distance

        if (player.team.side == Settings.LEFT_SIDE) {
        	if (player.inRectangle(Settings.PENALTY_AREA_LEFT)) {
        		return true;
        	}
        } else {
        	if (player.inRectangle(Settings.PENALTY_AREA_RIGHT)) {
        		return true;
        	}
        }

        return false;
    }

    /**
     * A rough estimate of whether the player can kick the ball, dependent
     * on its distance to the ball and whether it is inside the playing field.
     *
     * @return true if the player is on the field and within kicking distance
     */
    public final boolean canKickBall() {
        if (!player.inRectangle(Settings.FIELD)) {
            return false;
        }
        FieldObject ball = this.getOrCreate("(b)");
        if (ball.curInfo.time != time) {
            return false;
        }
        return ball.curInfo.distance < Futil.kickable_radius();
    }

    /**
     * True if and only if the ball was seen in the most recently-parsed 'see' message.
     */
    public final boolean canSee(String id) {
        if (!this.fieldObjects.containsKey(id)) {
            Log.e("Can't see " + id + "!");
            return false;
        }
        FieldObject obj = this.fieldObjects.get(id);
        return obj.curInfo.time == this.time;
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
        if (this.updateStrategy) {
            double bestUtility = 0;
            for (Strategy strategy : Strategy.values()) {
                double utility = this.assessUtility(strategy);
                if (utility > bestUtility) {
                    bestUtility = utility;
                    optimalStrategy = strategy;
                }
            }
        }
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
    	FieldObject ball = this.getOrCreate("(b)");
        FieldObject goal = this.getOrCreate(this.player.getOpponentGoalId());
    	
        switch (strategy) {
        case PRE_FREE_KICK_POSITION:
        	if (playMode.equals("free_kick_l")) {
        		if (this.player.team.side == Settings.LEFT_SIDE) {
        			//TODO
        		} else {
        			//TODO
        		}
            	this.move(Settings.FREE_KICK_L_FORMATION[player.number]);
        	} else {
        		if (this.player.team.side == Settings.LEFT_SIDE) {
        			//TODO
        		} else {
        			//TODO
        		}
            	this.move(Settings.FREE_KICK_R_FORMATION[player.number]);
        	}
        	this.isPositioned = true;
        	// Since we have now moved back into formation, derivatives
        	// strategies such as LOOK_AROUND should become dominant.        
        	break;
        case PRE_CORNER_KICK_POSITION:
        	if (playMode.equals("corner_kick_l")) {
        		if (this.player.team.side == Settings.LEFT_SIDE) {
        			//TODO
        		} else {
        			//TODO
        		}
            	this.move(Settings.CORNER_KICK_L_FORMATION[player.number]);
        	} else {
        		if (this.player.team.side == Settings.LEFT_SIDE) {
        			//TODO
        		} else {
        			//TODO
        		}
            	this.move(Settings.CORNER_KICK_R_FORMATION[player.number]);
        	}
        	this.isPositioned = true;
        	// Since we have now moved back into formation, derivatives
        	// strategies such as LOOK_AROUND should become dominant.        
        	break;
        case PRE_KICK_OFF_POSITION:
        	this.move(Settings.FORMATION[player.number]);
        	this.isPositioned = true;
        	// Since we have now moved back into formation, derivatives
        	// strategies such as LOOK_AROUND should become dominant.        
        	break;
        case PRE_KICK_OFF_ANGLE:
        	this.turn(30);
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
			
			// Kick!
			Log.i("Kick trajectory power: " + traj_power);
			kick( traj_power, Futil.simplifyAngle( Math.toDegrees(v_ball.direction()) ) );
        	break;
        case DASH_AROUND_THE_FIELD_CLOCKWISE:
            double x = player.position.getPosition().getX();
            double y = player.position.getPosition().getY();
            double targetDirection = 0;
            if (player.inRectangle(Settings.FIELD)) {
                targetDirection = -90;
            }
            // Then run around clockwise between the physical boundary and the field
            else if (y <= Settings.FIELD.getTop() && x <= Settings.FIELD.getRight()) {
                targetDirection = 0;        
            }
            else if (x >= Settings.FIELD.getRight() && y <= Settings.FIELD.getBottom()) {
                targetDirection = 90;
            }
            else if (y >= Settings.FIELD.getBottom() && x >= Settings.FIELD.getLeft()) {
                targetDirection = 180;
            }
            else if (x <= Settings.FIELD.getLeft() && y >= Settings.FIELD.getTop()) {
                targetDirection = -90;
            }
            else {
                Log.e("Strategy " + strategy + " doesn't know how to handle position " + this.player.position.getPosition().render() + ".");
            }
            double offset = Math.abs(this.player.relativeAngleTo(targetDirection)); 
            if (offset > 10) {
                this.turnTo(targetDirection);
            }
            else {
                this.dash(50, this.player.relativeAngleTo(targetDirection));
            }
            break;
        case DASH_TOWARDS_BALL_AND_KICK:
            Log.d("Estimated ball position: " + ball.position.render(this.time));
            Log.d("Estimated goal position: " + goal.position.render(this.time));
            if (this.canKickBall()) {
                if (this.canSee(this.player.getOpponentGoalId())) {
                    kick(100.0, this.player.relativeAngleTo(goal));
                }
                else {
                    dash(30.0, 90.0);
                }
            }
            else if (ball.position.getConfidence(this.time) > 0.1) {
                double approachAngle;
                approachAngle = Futil.simplifyAngle(this.player.relativeAngleTo(ball));
                if (Math.abs(approachAngle) > 10) {
                    this.turn(approachAngle);
                }
                else {
                    dash(50.0, approachAngle);
                }
            }
            else {
                turn(7.0);
            }
            break;
        case LOOK_AROUND:
            turn(7);
            break;
        case GET_BETWEEN_BALL_AND_GOAL:
        	//TODO
        	break;
        case GOALIE_CATCH_BALL:
        	//TODO
        	break;
        case TEST_TURNS:
            turn(7.0);
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
    	
    	Log.i("Dribble angle chosen: " + d_angle);
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
            
        	// TODO better nested parentheses parsing logic; perhaps
        	//    reconcile with Patrick's parentheses logic?            
        	
            Log.d("Received a `sense_body` message at time step " + time + ".");
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
            Log.d("Received `see` message at time step " + this.time);
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
            this.role = Settings.PLAYER_ROLES[this.player.number - 1];
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
     * Responds for the current time step.
     */
    public void run() {
        final long startTime = System.currentTimeMillis();
        final long endTime;
        int expectedNextRun = this.lastRan + 1;
        if (this.time > expectedNextRun) {
            Log.e("Brain did not run during time step " + expectedNextRun + ".");
        }
        this.lastRan = this.time;
        this.acceleration.reset();
        this.updatePositionAndDirection();
        this.currentStrategy = this.determineOptimalStrategy();
        Log.i("Current strategy: " + this.currentStrategy);
        this.executeStrategy(this.currentStrategy);
        Log.d("Estimated player position: " + this.player.position.render(this.time) + ".");
        Log.d("Estimated player direction: " + this.player.direction.render(this.time) + ".");
        endTime = System.currentTimeMillis();
        final long duration = endTime - startTime;
        Log.d("Took " + duration + " ms (plus small overhead) to run at time " + this.time + ".");
        if (duration > 35) {
            Log.e("Took " + duration + " ms (plus small overhead) to run at time " + this.time + ".");
        }
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
    private final void moveTowards(Point point){
    	moveTowards(point, 100d);
    }
    
    /**
     * Send commands to move the player to a point with the given power
     * @param point to move towards
     * @param power to move at
     */
    private final void moveTowards(Point point, double power){
    	final double x = player.position.getX() - point.getX();
    	final double y = player.position.getY() - point.getY();
    	final double theta = Math.toDegrees(Math.atan2(y, x));
    	turnTo(theta);
    	dash(power);
    }
    
    /**
     * Updates this this brain's belief about the associated player's position and direction
     * at the current time step. 
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
        // TODO Handle other cases
        Log.e("Did not update position or direction at time " + this.time);
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
