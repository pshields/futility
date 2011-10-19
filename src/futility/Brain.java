package futility;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Brain implements Runnable {
    private boolean canSeeBall;  // TODO Remove eventually
    private boolean canSeeGoal;  // TODO Remove eventually
    
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
    public Rectangle field = new Rectangle(Settings.FIELD_BUFFER + Settings.FIELD_HEIGHT, Settings.FIELD_BUFFER + Settings.FIELD_WIDTH, Settings.FIELD_BUFFER, Settings.FIELD_BUFFER);
    public MobileObject ball = new MobileObject();
    public StationaryObject goal = new StationaryObject();
    
    LinkedHashMap<String, FieldObject> fieldObjects = new LinkedHashMap<String, FieldObject>(Settings.INITIAL_HASH_MAP_SIZE);
    ArrayDeque<String> hearMessages = new ArrayDeque<String>();

    private Strategy currentStrategy = Strategy.LOOK_AROUND;
    
    public enum Strategy {
        DASH_AROUND_THE_FIELD_COUNTERCLOCKWISE,
        LOOK_AROUND
    }

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
            //client.log(Settings.LOG_LEVELS.DEBUG, String.format("Adding %s to my HashMap...", object.id));
            fieldObjects.put(object.id, object);
        }
    }
    
    /** A rough estimate of whether the player can kick the ball
     * 
     * Return true if the player is on the field and within the kickable
     * distance of the ball.
     * 
     * @return
     */
    public final boolean canKickBall() {
        if (!player.inRectangle(field)) {
            return false;
        }
        if (player.distanceTo(ball) > 0.7) {
            return false;
        }
        else {
            return true;
        }
    }
    
    /**
     * This is a simple soccer strategy.
     * 
     * 1. Run towards the ball.
     * 2. Rotate around it until you can see the opponent's goal.
     * 3. Kick the ball towards said goal.
     * 
     */
    public final void dashTowardsBallAndKickTowardsGoal() {
        double approachAngle;
        double power = Math.min(100, 10 + player.distanceTo(ball) * 20);
        if (canKickBall()) {
            if (canSeeGoal) {
                kick(100.0, player.relativeAngleTo(goal));
            }
            else {
                dash(30.0, 90.0);
            }
        }
        else if (canSeeBall) {
            if (canSeeGoal) {
                double approachAngleDelta = player.distanceTo(ball)/10;
                approachAngle = this.player.relativeAngleTo(ball) + Math.copySign(1.0, -this.player.relativeAngleTo(goal)) * approachAngleDelta;
                if (this.player.relativeAngleTo(ball) > this.player.relativeAngleTo(goal)) {
                    approachAngle =  + approachAngleDelta;
                }
                else {
                    approachAngle = this.player.relativeAngleTo(goal) - approachAngleDelta;
                }
                dash(power, approachAngle);
            }
            else {
                dash(power);
            }
        }
        else {
            if (this.player.relativeAngleTo(ball) > 0) {
                turn(7.0);
            }
            else {
                turn(-7.0);
            }
        }
    }

    /** True if and only if the ball was seen in the most recently-parsed 'see' message.
     * 
     *  // TODO integrate this method into the standard game state modeling methods
     */
    public final boolean canSeeBall() {
        return this.canSeeBall;
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
     * Accelerates the player in the direction of its body, offset by the given angle.
     * 
     * @param power the power of the acceleration (0 to 100)
     * @param offset an offset to be applied to the player's direction, yielding the direction of acceleration
     */
    public final void dash(double power, double offset) {
        client.sendCommand(Settings.Commands.DASH, Double.toString(power), Double.toString(offset));
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
        case DASH_AROUND_THE_FIELD_COUNTERCLOCKWISE:
            double x = player.position.getPosition().getX();
            double y = player.position.getPosition().getY();
            double targetDirection = 0;
            if (player.inRectangle(field)) {
                targetDirection = 90;
            }
            // Then run around clockwise between the physical boundary and the field
            else if (y >= field.top && x <= field.right) {
                targetDirection = 0;        
            }
            else if (x >= field.right && y >= field.bottom) {
                targetDirection = 270;
            }
            else if (y <= field.bottom && x >= field.left) {
                targetDirection = 180;
            }
            else if (x <= field.left && y <= field.top) {
                targetDirection = 90;
            }
            else {
                client.log(Settings.LOG_LEVELS.ERROR, "Strategy " + strategy + " doesn't know how to handle position "+this.player.position.getPosition().render() + ".");
            }
            if (Math.abs(this.player.relativeAngleTo(targetDirection)) > 10) {
                this.turnTo(targetDirection);
            }
            else {
                this.dash(50, this.player.relativeAngleTo(targetDirection));
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
     * Gets the strategy with the current highest utility.
     * 
     * @return the strategy we think is optimal for the current time step
     */
    private final Strategy getOptimalStrategy() {
        Strategy optimalStrategy = this.currentStrategy;
        double bestUtility = 0;
        for (Strategy strategy : Strategy.values()) {
            double utility = this.getUtility(strategy);
            if (utility > bestUtility) {
                bestUtility = utility;
                optimalStrategy = strategy;
            }
        }
        return optimalStrategy;
    }
    
    /**
     * Gets a measure of the utility of the given strategy for the current time step.
     * 
     * @param strategy the strategy to assess the utility of
     * @return a measure of the strategy's utility in the range [0.0, 1.0]
     */
    private final double getUtility(Strategy strategy) {
        double utility = 0;
        switch (strategy) {
            case DASH_AROUND_THE_FIELD_COUNTERCLOCKWISE:
                utility = 0.5;
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
     * Gets a measure of the error of point estimate from the position of one
     * or more field objects. The error is equal to the sum of the distances
     * from the point to the nearest plausible point the player could be, given
     * the angle the player saw each object at.
     * 
     * @param point the point to measure error against
     * @param objects one or more qualified field objects
     * @return
     */
    public double measureError(Point point, FieldObject... objects) {
        double error = 0;
        for (FieldObject object : objects) {
            error += object.asCircle().closestDistanceTo(point);
        }
        return error;
    }

    /**
     * Parses a message from the soccer server. This method is called whenever
     * a message from the server is received.
     * 
     * @param message the message (string), exactly as it was received
     */
    public void parseMessage(String message) {
        // Handle sense_body messages
        if (message.startsWith("(sense_body")) {
        	// TODO better nested parentheses parsing logic; perhaps
        	//    reconcile with Patrick's parentheses logic?
        	collision = "none";
            time = Integer.parseInt(message.substring(12, 16).split("\\s")[0]);
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
        }
        // Handle hear messages
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
        // Handle see messages
        else if (message.startsWith("(see")) {
            // Update our concept of the current timestep.
            // Standard times go up to 6000 so we'll only check four digits.
            // Split the string on any non-digit and take the first part
            String timePart = message.substring(5, 9).split("\\D")[0];
            time = Integer.parseInt(timePart);
            // Following the time are parentheses-delimited ObjectInfos.
            // We're parsing them manually so we don't waste cycles on
            // pattern matching (not sure if/how much it helps.)
            int openParentheses = 0; // Not counting the exterior '(see ... )' parenthesis
            int beginIndex = -1;
            int endIndex = -1;
            LinkedList<String> justSeenObjectIds = new LinkedList<String>();
            for (int i = 5; i < message.length(); i++) {
                if (message.charAt(i) == '(') {
                    if (openParentheses == 0) {
                        beginIndex = i; // This character marks the first character in an ObjectInfo string
                    }
                    openParentheses++;
                }
                else if (message.charAt(i) == ')') {
                    if (openParentheses == 1) {
                        endIndex = i + 1; // This character marks the last character in an ObjectInfo string
                        // Now parse the ObjectInfo
                        String objectId = parseObjectInfo(message.substring(beginIndex, endIndex));
                        justSeenObjectIds.add(objectId);
                    }
                    openParentheses--;
                }
            }
            this.updatePositionAndDirection(justSeenObjectIds);
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
                client.log(Settings.LOG_LEVELS.ERROR, "Could not parse teamSide.");
            }
            player.number = Integer.parseInt(parts[2]);
            playMode = parts[3].split("\\)")[0];
        }
    }
    
    /**
     * Parses and handles an ObjectInfo string.
     * @param objectInfo the ObjectInfo string
     * @return the objectId of the parsed ObjectInfo
     */

    public final String parseSeenObject(String objectInfo) {
        // Identify the object name
        int i = 2;
        while (objectInfo.charAt(i) != ')') {
            i++;
        }

        String id = objectInfo.substring(1, i+1); // id is the object name
        String values = objectInfo.substring(i + 2).replaceAll("\\)", ""); // the remaining arguments, no leading whitespace
        String[] args = values.split(" ");
 
        
        FieldObject obj = createFieldObject(id);
        if(obj == null) return; //yet unsupported object or an error
        obj.timeLastSeen = time;
        switch(args.length){
        case 1:
        	obj.angleToLastSeen.update(Double.valueOf(args[0]), time);
        	break;
        case 6:
        	obj.headFacing.update(Double.valueOf(args[5]), time);
        	obj.bodyFacing.update(Double.valueOf(args[4]), time);
        case 4:
        	obj.directionChange = Double.valueOf(args[3]);
        	obj.distanceChange = Double.valueOf(args[2]);
        case 2:
        	obj.distanceTo = Double.valueOf(args[0]);
        	obj.angleToLastSeen.update(Double.valueOf(args[1]), time);  
        	break;
        default:
        	player.client.log(Settings.LOG_LEVELS.ERROR, "Invalid number of arguments for a FieldObject");
        	return;
        }
        
        if (fieldObjects.containsKey(id)) {
            // Update the player's beliefs about the field object
            
            FieldObject object = fieldObjects.get(id);
            object.copyFieldObject(obj);
            
            player.client.log(Settings.LOG_LEVELS.DEBUG, "Just updated field object with name " + object.id);
            // TODO Update our conception of player /ball positions
        }
        else {
        	fieldObjects.put(id, obj);
            player.client.log(Settings.LOG_LEVELS.DEBUG, "Just added " + id + " to the HashMap.");
        }
    }
    
    /**
     * Given a valid soccer server object name this method returns a proper FieldObject
     * @param name
     * @return a FieldObject based off the name
     */
    private FieldObject createFieldObject(String name) {
		if(name.startsWith("(b")){
			return new Ball();
		}
		else if(name.startsWith("(p")){
			return new Player(name);
		}
		else if(name.startsWith("(g")){
			return new Goal(name);
		}
		else if(name.startsWith("(f")){
			return new Flag(name);
		}
		else if(name.startsWith("(l")){
			//TODO return whatever an l is
			return null;
		}
		else if(name.startsWith("(B")){
			//TODO return whatever a B is
			return null;
		}
		else if(name.startsWith("(F")){
			//TODO return whatver an F is
			return null;
		}
		else if(name.startsWith("(G")){
			//TODO return whatever a G is
			return null;
		}
		else if(name.startsWith("(P")){
			//TODO return whatever a P is
			return null;
		}
		else{
			player.client.log(Settings.LOG_LEVELS.ERROR, "invalid name detected for see parse");
			return null;
		}
	}
    
    /**
     * Responds for the current time step. This method is called every 100ms.
     */
    public void run() {
        // Possibly update the current strategy
        this.currentStrategy = this.getOptimalStrategy();
        this.client.log("Current strategy: "+this.currentStrategy);
        // Execute the current strategy
        this.executeStrategy(this.currentStrategy);
        // Clear variables for the current time step
        this.canSeeBall = false;
        this.canSeeGoal = false;
        // Log debug info
        this.client.log(Settings.LOG_LEVELS.DEBUG, "Estimateed position: " + this.player.position.render(this.time) + ".");
        this.client.log(Settings.LOG_LEVELS.DEBUG, "Estimated direction: " + this.player.direction.render(this.time) + ".");
    }
    
    /** 
     * Adds the given angle to the player's current direction.
     * 
     * @param offset an angle in degrees to add to the player's current direction
     */
    public final void turn(double offset) {
        // Simplify the angle to the smallest offset necessary to effect the
        // desired change in direction. The resulting angle must be within
        // the min and max moment to be accepted by the server. By default,
        // the moments are -180 and 180 degrees.
        while (offset > 180) {
            offset -= 360;
        }
        while (offset <= -180) {
            offset += 360;
        }
        // In the soccer server implementation, a positive angle represents
        // a right turn. Since our implementation assumes a standard unit
        // circle, we reverse the provided offset when sending the command.
        client.sendCommand(Settings.Commands.TURN, -offset);
        // Update the player's direction
        // TODO Potentially take magnitude of offset into account in the
        // determination of the new confidence in the player's position.
        player.direction.update(player.direction.getDirection() + offset, 0.95 * player.direction.getConfidence(time), time);
    }
    
    /** 
     * Updates the player's current direction to be the given direction.
     * 
     * @param direction a standard angle on the unit circle in degrees
     */
    public final void turnTo(double direction) {
        this.turn(this.player.relativeAngleTo(direction));
    }
    
    /**
     * Updates the player's current direction in light of new information. This
     * method takes a linked list of ObjectId strings, figures out which ones
     * actually have enough information to use for determining the player's
     * position and direction, and uses them to do so.
     * 
     * @param justSeenObjectIds a linked list of ObjectId strings
     */
    private final void updatePositionAndDirection(LinkedList<String> justSeenObjectIds) {
        // Create a list of qualified objects
        // An object is qualified if the player received distance and direction
        // information about the object in the current turn.
        LinkedList<FieldObject> qualifiedObjects = new LinkedList<FieldObject>();
        for (String objectId : justSeenObjectIds) {
            if (fieldObjects.containsKey(objectId)) {    
                FieldObject object = fieldObjects.get(objectId);
                if (object.isStationaryObject() && object.angleToLastSeen.getTimeEstimated() == this.time) {
                    qualifiedObjects.add(object);
                }
            }
        }
        this.client.log(Settings.LOG_LEVELS.DEBUG, "Found " + qualifiedObjects.size() + " qualified objects out of " + justSeenObjectIds.size() + " just seen objects at time " + this.time);
        // TODO Handle the near-ideal case of 3+ qualified objects
        // Update the player's position
        if (qualifiedObjects.size() >= 2) {
            // Introduce some randomness into the calculation in order to help
            // players who are stuck.
            int i = (int)(Math.random() * (qualifiedObjects.size() - 1));
            FieldObject o1 = qualifiedObjects.get(i);
            FieldObject o2 = qualifiedObjects.get(i+1);
            Point[] points = o1.asCircle().intersectionPointsWith(o2.asCircle());
            if (points.length > 0) {
                Point bestPoint = new Point();
                double bestError = Double.MAX_VALUE;
                // Use player's beliefs about directions to the objects to
                // determine which point to use
                for (Point point : points) {
                    double error = this.measureError(point, o1, o2);
                    if (error < bestError) {
                        bestError = error;
                        bestPoint.update(point);
                    }
                }
                double updateConfidence = 25 / (25 + bestError);
                this.client.log(Settings.LOG_LEVELS.DEBUG, "Used two-circle triangulation to derive a new position estimate of " + bestPoint.render() + " with a confidence of " + Double.toString(updateConfidence));
                player.position.update(bestPoint, updateConfidence, this.time);
            }
            else {
                this.client.log(Settings.LOG_LEVELS.ERROR, "Two-circle triangulation returned no points.");
            }
        }
        if (qualifiedObjects.size() >= 1){
            FieldObject object = qualifiedObjects.get(0);
            // Alternative position update function
            double updateConfidence = 0.95 * player.direction.getConfidence(time);
            if (this.player.position.getConfidence(time) < updateConfidence) {
                double x = object.position.getPosition().getX();
                double y = object.position.getPosition().getY();
                double objectiveAngleTo = this.player.direction.getDirection() - object.angleToLastSeen.getDirection();
                x += Math.cos(objectiveAngleTo) * object.distanceTo;
                y += Math.sin(objectiveAngleTo) * object.distanceTo;
                this.player.position.update(x, y, updateConfidence, time);
                this.client.log(Settings.LOG_LEVELS.DEBUG, "Thought it would be smart to update position to " + this.player.position.getPosition().render() + " with a confidence of " + Double.toString(updateConfidence) + ".");
            }
            // Alternative direction update function
            updateConfidence = this.player.position.getConfidence(time) * object.position.getConfidence(time) * 0.95;
            if (updateConfidence > this.player.direction.getConfidence(time)) {
                double x0 = this.player.position.getPosition().getX();
                double y0 = this.player.position.getPosition().getY();
                double x1 = object.position.getPosition().getX();
                double y1 = object.position.getPosition().getY();
                double newDirection = Math.toDegrees(Math.atan((y1 - y0)/(x1 - x0)));
                this.player.direction.update(newDirection, updateConfidence, time);
                this.client.log(Settings.LOG_LEVELS.DEBUG, "Thought it would be smart to update direction to " + Double.toString(this.player.direction.getDirection()) + " with a confidence of " + Double.toString(updateConfidence) + ".");
            }
        }
        else {
            // Do nothing. The confidence in the player's position will
            // naturally decrease with time. If it gets really low, it
            // will switch to a 'look around' strategy.
        }
    }
    
    private final Strategy getOptimalStrategy() {
        Strategy optimalStrategy = this.currentStrategy;
        double bestUtility = 0;
        for (Strategy strategy : Strategy.values()) {
            double utility = this.getUtility(strategy);
            if (utility > bestUtility) {
                bestUtility = utility;
                optimalStrategy = strategy;
            }
        }
        return optimalStrategy;
    }
    
    private final double getUtility(Strategy strategy) {
        double utility = 0;
        switch (strategy) {
            case DASH_AROUND_THE_FIELD_COUNTERCLOCKWISE:
                utility = 0.3;
                break;
            case LOOK_AROUND:
                utility = 1 - ( 1 / (1 + this.player.position.getConfidence(this.time)));
                break;
            default:
                utility = 0;
                break;
        }
        return utility;
    }
}
