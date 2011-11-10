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
 * Threaded class that contains the player agent's sensory data parsing and
 * strategy computation algorithms.
 */
public class Brain implements Runnable {
    
    /**
     * Enumerator representing the possible strategies that may be used by this
     * player agent.
     * 
     * DASH_AROUND_THE_FIELD_CLOCKWISE tells the player to dash around
     *   the field boundaries clockwise.
     * DASH_TOWARDS_THE_BALL_AND KICK implements a simple soccer strategy:
     *
     * 1. Run towards the ball.
     * 2. Rotate around it until you can see the opponent's goal.
     * 3. Kick the ball towards said goal.
     * 
     * LOOK_AROUND tells the player to look around in a circle.
     */
    public enum Strategy {
        DASH_AROUND_THE_FIELD_CLOCKWISE,
        DASH_TOWARDS_BALL_AND_KICK,
        LOOK_AROUND
    }
	
    ///////////////////////////////////////////////////////////////////////////
    // MEMBER VARIABLES
    ///////////////////////////////////////////////////////////////////////////    
    Client client;
    Player player;
    private int time;
    
    // Self info & Play mode
    // TODO Encapsulate this information as needed.
    private String playMode;
    private String viewQuality;
    private String viewWidth;
    private double stamina;
    private double staminaEffort;
    private double staminaCapacity;
    private double speedAmount;
    private double speedDirection;
    private double headAngle;
    private String collision = "none";
    
    // Object models
    public Rectangle field = Settings.FIELD;
    public MobileObject ball = new MobileObject();
    public StationaryObject goal = new StationaryObject();
    
    HashMap<String, FieldObject> fieldObjects = new HashMap<String, FieldObject>(100);
    ArrayDeque<String> hearMessages = new ArrayDeque<String>();
    LinkedList<Settings.RESPONSE>responseHistory = new LinkedList<Settings.RESPONSE>();
    private long timeLastSee = 0;
    private long timeLastSenseBody = 0;
    private int lastRan = -1;

    private Strategy currentStrategy = Strategy.LOOK_AROUND;

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
     * Assesses the utility of a strategy for the current time step.
     * 
     * @param strategy the strategy to assess the utility of
     * @return an assessment of the strategy's utility in the range [0.0, 1.0]
     */
    private final double assessUtility(Strategy strategy) {
        double utility = 0;
        switch (strategy) {
        case DASH_AROUND_THE_FIELD_CLOCKWISE:
            utility = 0.95;
            break;
        case DASH_TOWARDS_BALL_AND_KICK:        
            utility = 0.94;
            break;
        case LOOK_AROUND:
            utility = 1 - this.player.position.getConfidence(this.time);
            break;
        default:
            utility = 0;
            break;
        }
        return utility;
    }
    
    /** 
     * A rough estimate of whether the player can kick the ball, dependent
     * on its distance to the ball and whether it is inside the playing field.
     * 
     * @return true if the player is on the field and within kicking distance
     */
    public final boolean canKickBall() {
        if (!player.inRectangle(field)) {
            return false;
        }
        FieldObject ball = fieldObjects.get("(b)");
        if (ball.info.time != time) {
            return false;
        }
        return ball.info.distance < 0.7;
    }

    /**
     * True if and only if the ball was seen in the most recently-parsed 'see' message.
     */
    public final boolean canSee(String id) {
        if (!this.fieldObjects.containsKey(id)) {
            return false;
        }
        FieldObject obj = this.fieldObjects.get(id);
        return obj.info.time == this.time;
    }

    /**
     * Accelerates the player in the direction of its body.
     * 
     * @param power the power of the acceleration (0 to 100)
     */
    private final void dash(double power) {
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
        for (Strategy strategy : Strategy.values()) {
            double utility = this.assessUtility(strategy);
            if (utility > bestUtility) {
                bestUtility = utility;
                optimalStrategy = strategy;
            }
        }
        return optimalStrategy;
    }
    
    /** 
     * Estimates the position of a field object.
     * 
     * @param object a field object to estimate the position of
     * @return a position estimate for the field object
     */
    private PositionEstimate estimatePositionOf(FieldObject object) {
        PositionEstimate estimate = new PositionEstimate();
        // TODO 
        return estimate;
    }
    
    /**
     * Executes a strategy for the player in the current time step.
     * 
     * @param strategy the strategy to execute
     */
    private final void executeStrategy(Strategy strategy) {
        switch (strategy) {
        case DASH_AROUND_THE_FIELD_CLOCKWISE:
            double x = player.position.getPosition().getX();
            double y = player.position.getPosition().getY();
            double targetDirection = 0;
            if (player.inRectangle(field)) {
                targetDirection = -90;
            }
            // Then run around clockwise between the physical boundary and the field
            else if (y <= field.getTop() && x <= field.getRight()) {
                targetDirection = 0;        
            }
            else if (x >= field.getRight() && y <= field.getBottom()) {
                targetDirection = 90;
            }
            else if (y >= field.getBottom() && x >= field.getLeft()) {
                targetDirection = 180;
            }
            else if (x <= field.getLeft() && y >= field.getTop()) {
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
            double approachAngle;
            String goalString = "(g "+this.player.otherTeam.side +")";
            if (fieldObjects.containsKey(goalString) && fieldObjects.containsKey("(b)")) {
                FieldObject ball = this.fieldObjects.get("(b)");
                FieldObject goal = this.fieldObjects.get(goalString);
                double power = Math.min(100, 10 + player.distanceTo(ball) * 20);
                if (ball != null && goal != null && canKickBall()) {
                    if (this.canSee("(g)")) {
                        kick(100.0, this.player.relativeAngleTo(goal));
                    }
                    else {
                        dash(30.0, 90.0);
                    }
                }
                else if (ball != null && goal != null && this.canSee("(b)")) {
                    if (this.canSee("(g)")) {
                        double approachAngleDelta = player.distanceTo(ball)/10;
                        approachAngle = this.player.relativeAngleTo(ball) + Math.copySign(1.0, -this.player.relativeAngleTo(goal)) * approachAngleDelta;
                        if (this.player.relativeAngleTo(ball) > this.player.relativeAngleTo(goal)) {
                            approachAngle =  + approachAngleDelta;
                        }
                        else {
                            approachAngle = this.player.relativeAngleTo(ball) - approachAngleDelta;
                        }
                        dash(power, approachAngle);
                    }
                    else {
                        dash(power);
                    }
                }
                else {
                    turn(7.0);
                }
            }
            else {
                turn(7.0);
            }
            break;
        case LOOK_AROUND:
            turn(7);
            break;
        default:
            break;
        }
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
        double x1 = Math.cos(Math.toRadians(o1.info.direction)) * o1.info.distance;
        double y1 = Math.sin(Math.toRadians(o1.info.direction)) * o1.info.distance;
        double x2 = Math.cos(Math.toRadians(o2.info.direction)) * o2.info.distance;
        double y2 = Math.sin(Math.toRadians(o2.info.direction)) * o2.info.distance;
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
        double x = o1.position.getX() - o1.info.distance * Math.cos(Math.toRadians(direction + o1.info.direction));
        double y = o1.position.getY() - o1.info.distance * Math.sin(Math.toRadians(direction + o1.info.direction));
        double distance = this.player.position.getPosition().distanceTo(new Point(x, y)); 
        this.player.position.update(x, y, 0.95, this.time);
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
            this.timeLastSenseBody = timeReceived;
        	// TODO better nested parentheses parsing logic; perhaps
        	//    reconcile with Patrick's parentheses logic?            
        	collision = "none";
            int time = Integer.parseInt(message.substring(12, 17).split("\\s")[0]);
            if (time != this.time) {
                this.time = time;
            }
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
            		viewQuality = nArgs[1];
            		viewWidth = nArgs[2];
            	}
            	else if ( nArgs[0].contains("stamina") )
            	{ // Player's stamina data
            		stamina = Double.parseDouble(nArgs[1]);
            		staminaEffort = Double.parseDouble(nArgs[2]);
            		staminaCapacity = Double.parseDouble(nArgs[3]);
            	}
            	else if ( nArgs[0].contains("speed") )
            	{ // Player's speed data
            		speedAmount = Double.parseDouble(nArgs[1]);
            		speedDirection = Double.parseDouble(nArgs[2]);
            	}
            	else if ( nArgs[0].contains("head_angle") )
            	{ // Player's head angle
            		headAngle = Double.parseDouble(nArgs[1]);
            	}
            	else if ( nArgs[0].contains("ball") || nArgs[0].contains("player")
            			       || nArgs[0].contains("post") )
            	{ // COLLISION flags; limitation of this loop approach is we
            	  //   can't handle nested parentheses arguments well.
            	  // Luckily these flags only occur in the collision structure.
            		collision = nArgs[0];
            	}
            }
            // If the brain has responded to two see messages in a row, it's time to respond to a sense_body.
            if (this.responseHistory.get(0) == Settings.RESPONSE.SEE && this.responseHistory.get(1) == Settings.RESPONSE.SEE) {
                this.client.responseExecutor.execute(this);
                this.responseHistory.push(Settings.RESPONSE.SENSE_BODY);
                this.responseHistory.removeLast();
            }
        }
        // Handle `hear` messages
        else if (message.startsWith("(hear"))
        {
        	String parts[] = message.split("\\s");
        	time = Integer.parseInt(parts[1]);
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
        		if ( parts[2].startsWith("r")                   // Referee;
        			   && Settings.PLAY_MODES.contains(nMsg) )  // Play Mode?
            		playMode = nMsg;
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
            for (String info : infos) {
                String id = Futil.extractId(info);
                if (Futil.isUniqueFieldObject(id)) {
                    FieldObject obj = this.getOrCreate(id);
                    obj.update(info, this.time);
                }
            }
            // Immediately run for the current step. Since our computations takes only a few
            // milliseconds, it's okay to start running over half-way into the 100ms cycle.
            // That means two out of every three time steps will be executed here.
            this.client.responseExecutor.execute(this);
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
            playMode = parts[3].split("\\)")[0];
        }
        else if (message.startsWith("(server_param")) {
        	parseServerParameters(message);
        }
    }
    
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
        this.updatePositionAndDirection();
        this.currentStrategy = this.determineOptimalStrategy();
        Log.i("Current strategy: " + this.currentStrategy);
        this.executeStrategy(this.currentStrategy);
        Log.d("Estimated position: " + this.player.position.render(this.time) + ".");
        Log.d("Estimated direction: " + this.player.direction.render(this.time) + ".");
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
                if (flag.info.time == this.time) {
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
        Log.i("Did not update position or direction at time " + this.time);
    }
}
