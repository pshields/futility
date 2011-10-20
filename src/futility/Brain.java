package futility;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Brain implements Runnable {
    public boolean canSeeBall;  // TODO Remove eventually
    public boolean canSeeGoal;  // TODO Remove eventually
    
    Player player;
    public int time;
    

    
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
    LinkedList<String> justSeenObjects = new LinkedList<String>();
    ArrayDeque<String> hearMessages = new ArrayDeque<String>();
    
    public enum SeeInfo {
        GOAL_ANGLE,
        GOAL_DISTANCE
    }

    /** Brain constructor
     * 
     * @param player a back-reference to the invoking player
     */
    public Brain(Player player) {
        this.player = player;
        // Load the HashMap
        for (int i = 0; i < Settings.STATIONARY_OBJECTS.length; i++) {
            StationaryObject object = Settings.STATIONARY_OBJECTS[i];
            //player.client.log(Settings.LOG_LEVELS.DEBUG, String.format("Adding %s to my HashMap...", object.id));
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
    
    /** A basic single-player strategy
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
                kick(100.0, player.angleTo(goal));
            }
            else {
                dash(30.0, 90.0);
            }
        }
        else if (canSeeBall) {
            if (canSeeGoal) {
                double approachAngleDelta = player.distanceTo(ball)/10;
                approachAngle = player.angleTo(ball) + Math.copySign(1.0, -player.angleTo(goal)) * approachAngleDelta;
                if (player.angleTo(ball) > player.angleTo(goal)) {
                    approachAngle =  + approachAngleDelta;
                }
                else {
                    approachAngle = player.angleTo(goal) - approachAngleDelta;
                }
                dash(power, approachAngle);
            }
            else {
                dash(power);
            }
        }
        else {
            if (player.angleTo(ball) > 0) {
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

    /** Dash
     * 
     * @param power how hard to dash
     */
    public final void dash(double power) {
        player.client.sendCommand(Commands.DASH, Double.toString(power));
    }

    /** Dash in a direction
     * 
     * @param power how hard to dash
     * @param direction a relative direction in degrees in which to dash
     */
    public final void dash(double power, double direction) {
        player.client.sendCommand(Commands.DASH, power, direction);
    }
    
    /** A simple strategy to test the player's belief about its field position
     * 
     */
    public void dashClockwiseAroundTheField() {
     // First, run to the top of the field
        if (player.inRectangle(Settings.FIELD())) {
            if (Math.abs(90 - player.directionTo) > 10) {
                // Turn to face north
                turnTo(90);
            }
            else {
                dash(100);
            }
        }
        // Then run around clockwise between the physical boundary and the field
        else if (player.position.y > Settings.FIELD().top && player.position.x < Settings.FIELD().right) {
            if (Math.abs(0 - player.directionTo) > 10) {
                turnTo(0);                
            }
            else {
                dash(100);
            }
        }
        else if (player.position.x > Settings.FIELD().right && player.position.y < Settings.FIELD().bottom) {
            if (Math.abs(270 - player.directionTo) > 10) {
                turnTo(270);                
            }
            else {
                dash(100);
            }
        }
        else if (player.position.y < Settings.FIELD().bottom && player.position.x < Settings.FIELD().left) {
            if (Math.abs(180 - player.directionTo) > 10) {
                turnTo(180);                
            }
            else {
                dash(100);
            }  
        }
        else if (player.position.x < Settings.FIELD().left && player.position.y < Settings.FIELD().top) {
            if (Math.abs(90 - player.directionTo) > 10) {
                turnTo(90);                
            }
            else {
                dash(100);
            }
        }
        else {
            player.client.log(Settings.LOG_LEVELS.ERROR, "Could not determine position.");
        }
    }

    public void kick(double power) {
        player.client.sendCommand(Commands.KICK, power);
    }

    /** Kick in a relative direction
     * 
     * @param power how hard to kick
     * @param direction a relative angle in degrees by which to kick
     */
    public void kick(double power, double direction) {
        player.client.sendCommand(Commands.KICK, Double.toString(power), Double.toString(direction));
    }

    /** Parse a message from the soccer server
     * 
     * This method is called whenever a message from the server is received.
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
            time = Integer.parseInt(message.substring(5, 9).split("\\s")[0]);
            // Following the time are parentheses-delimited ObjectInfos.
            // We're parsing them manually so we don't waste cycles on
            // pattern matching (not sure if/how much it helps.)
            int openParentheses = 0; // Not counting the exterior '(see ... )' parenthesis
            int beginIndex = -1;
            int endIndex = -1;
            for (int i = 5; i < message.length(); i++) {
                if (message.charAt(i) == '(') {
                    if (openParentheses == 0) {
                        beginIndex = i; // This character marks the first character in an ObjectInfo string
                    }
                    openParentheses++;
                }
                else if (message.charAt(i) == ')') {
                    if (openParentheses == 1) {
                        endIndex = i; // This character marks the last character in an ObjectInfo string
                        // Now parse the ObjectInfo
                        parseSeenObject(message.substring(beginIndex, endIndex + 1));
                    }
                    openParentheses--;
                }
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
                player.client.log(Settings.LOG_LEVELS.ERROR, "Could not parse teamSide.");
            }
            player.number = Integer.parseInt(parts[2]);
            playMode = parts[3].split("\\)")[0];
        }
    }
    
    /** Parse and handle an ObjectInfo string
     * <br>
     * Parse an ObjectInfo string and update our beliefs about the associated
     * object. Objects must fit the following format to be properly parsed.
     *<br>
     *  ObjInfo ::= (ObjName Distance Direction DistChange DirChange BodyFacingDir HeadFacingDir ) <br>
		| (ObjName Distance Direction DistChange DirChange <br>
		| (ObjName Distance Direction) <br>
		| (ObjName Direction) <br>
		ObjName ::= (p ["Teamname" [UniformNumber [goalie]]]) <br>
		| (b) <br>
		| (g [l|r]) <br>
		| (f c) <br>
		| (f [l|c|r] [t|b]) <br>
		| (f p [l|r] [t|c|b]) <br>
		| (f g [l|r] [t|b]) <br>
		| (f [l|r|t|b] 0) <br>
		| (f [t|b] [l|r] [10|20|30|40|50]) <br>
		| (f [l|r] [t|b] [10|20|30]) <br>
		| (l [l|r|t|b]) <br>
		| (B) <br>
		| (F)<br>
		| (G) <br>
		| (P) <br>
		Distance ::= positive real number <br>
		Direction ::= -180 to 180 degrees <br>
		DistChange ::= real number <br>
		DirChange ::= real number <br>
		HeadFaceDir ::= -180 to 180 degrees <br>
		BodyFaceDir ::= -180 to 180 degrees <br>
		Teamname ::= string <br>
		UniformNumber ::= 1-11 <br>
     * @param content the ObjectInfo string
     * @return none
     */
    public final void parseSeenObject(String objectInfo) {
        // First, identify the object name.
        int i = 2;
        while (objectInfo.charAt(i) != ')') {
            i++;
        }
        String id = objectInfo.substring(1, i+1); // id is the object name
        String values = objectInfo.substring(i + 2).replaceAll("\\)", ""); // the remaining arguments, no leading whitespace
        String[] args = values.split(" ");
        
        justSeenObjects.add(id);
        
        FieldObject obj = createFieldObject(id);
        if(obj == null) return; //yet unsupported object or an error
        obj.timeLastSeen = time;
        switch(args.length){
        case 1:
        	obj.directionTo = Double.valueOf(args[0]);
        	break;
        case 6:
        	obj.headFacingDir = Double.valueOf(args[5]);
        	obj.bodyFacingDir = Double.valueOf(args[4]);
        case 4:
        	obj.directionChange = Double.valueOf(args[3]);
        	obj.distanceChange = Double.valueOf(args[2]);
        case 2:
        	obj.distanceTo = Double.valueOf(args[0]);
        	obj.directionTo = Double.valueOf(args[1]);  
        	break;
        default:
        	player.client.log(Settings.LOG_LEVELS.ERROR, "Invalid number of arguments for a FieldObject");
        	return;
        }
        
        if (fieldObjects.containsKey(id)) {
            // Update our conception of the field object
            
            FieldObject object = fieldObjects.get(id);
            object.copyFieldObject(obj);
            
            player.client.log(Settings.LOG_LEVELS.DEBUG, "Just updated field object with name " + object.id);
            // TODO Update our conception of player /ball positions
        }
        else {
        	fieldObjects.put(id, obj);
            player.client.log(Settings.LOG_LEVELS.DEBUG, "Just added " + id + " to the HashMap!");
        }
    }
    
    /**
     * Given a valid soccer server object name this method returns a proper FieldObject
     * @param name - meets the following criteria <br>
     * ObjName ::= (p ["Teamname" [UniformNumber [goalie]]]) <br>
		| (b) <br>
		| (g [l|r]) <br>
		| (f c) <br>
		| (f [l|c|r] [t|b]) <br>
		| (f p [l|r] [t|c|b]) <br>
		| (f g [l|r] [t|b]) <br>
		| (f [l|r|t|b] 0) <br>
		| (f [t|b] [l|r] [10|20|30|40|50]) <br>
		| (f [l|r] [t|b] [10|20|30]) <br>
		| (l [l|r|t|b]) <br>
		| (B) <br>
		| (F)<br>
		| (G) <br>
		| (P) <br>
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

	/** Reset the player's time-step specific knowledge
     * 
     * At the end of the turn, some variables need to be reset, such as the
     * list of field objects seen in the current timestep. We do that here.
     */
    public void resetKnowledge() {
        canSeeBall = false;
        canSeeGoal = false;
        justSeenObjects.clear();
    }
    
    /** Respond in the current timestep
     * 
     */
    public void run() {
        dashClockwiseAroundTheField();
        resetKnowledge();
    }
    
    /** Turn by a relative amount in degrees
     * 
     * @param direction the amount in degrees by which the player should turn
     */
    public final void turn(double direction) {
        player.client.sendCommand(Commands.TURN, direction);
    }
    
    /** Turn to face a global direction (east is 0)
     * 
     * @param direction a global direction in degrees
     */
    public final void turnTo(double direction) {
        turn(player.angleTo(direction));
    }

    /** Update our belief about the player's location
     * 
     * This calculation takes several factors into effect:
     *   * Can we infer the player's location from the field objects with known
     *     locations seen in the most recently-received 'see'? If we have at
     *     least two with estimated directions and distances, we can.
     *   * Do we have an idea about where the player was in the last time-step?
     *     We can take that plus the player's direction, momentum, and actions
     *     taken in the previous time-step to guess where the player is now.
     * 
     * This method is still in development.
     * 
     */
    public final void updatePosition() {
        if (justSeenObjects.size() > 2) // Required in order to successfully triangulate
        {
            double x = -1.0;
            double y = -1.0;
            FieldObject[] objects = {
                    fieldObjects.get(justSeenObjects.get(0)),
                    fieldObjects.get(justSeenObjects.get(1))
            };
            // player.position.update(x, y);
        }
        // Otherwise, assume we are in the same place as we were, and do nothing.
    }
}
