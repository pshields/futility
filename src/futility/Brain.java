package futility;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Brain implements Runnable {
    public boolean canSeeBall;
    public boolean canSeeGoal;
    
    Player player;
    public int time;
    
    // Object models
    public Rectangle field = new Rectangle(Settings.FIELD_BUFFER + Settings.FIELD_HEIGHT, Settings.FIELD_BUFFER + Settings.FIELD_WIDTH, Settings.FIELD_BUFFER, Settings.FIELD_BUFFER);
    public MobileObject ball = new MobileObject();
    public StationaryObject goal = new StationaryObject();
    
    LinkedHashMap<String, FieldObject> fieldObjects = new LinkedHashMap(Settings.INITIAL_HASH_MAP_SIZE);

    public enum SeeInfo {
        GOAL_ANGLE,
        GOAL_DISTANCE
    }

    public Brain(Player player) {
        this.player = player;
    }
    
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

    /** True if and only if the ball was seen in the most recently-parsed 'see' message.
     * 
     */
    public final boolean canSeeBall() {
        return this.canSeeBall;
    }

    public final void dash(double power) {
        player.client.sendCommand(Commands.DASH, Double.toString(power));
    }

    public final void dash(double power, double direction) {
        player.client.sendCommand(Commands.DASH, power, direction);
    }

    public void kick(double power) {
        player.client.sendCommand(Commands.KICK, power);
    }

    public void kick(double power, double direction) {
        player.client.sendCommand(Commands.KICK, Double.toString(power), Double.toString(direction));
    }

    public void parseMessage(String message) {
        // Handle sense_body messages
        if (message.startsWith("(sense_body")) {
            time = Integer.parseInt(message.substring(12, 16).split("\\s")[0]);
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
                        parseObjectInfo(message.substring(beginIndex, endIndex));
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
        }

    }

    /** Parse and handle an ObjectInfo string
     * 
     * Parse an ObjectInfo string and update our conception of the associated
     * object.
     *
     * @param content the ObjectInfo string
     * @return none
     */
    public final void parseObjectInfo(String objectInfo) {
        // First, identify the object name.
        int i = 2;
        while (objectInfo.charAt(i) != ')') {
            i++;
        }
        String id = objectInfo.substring(1, i); // id is the object name
        if (fieldObjects.containsKey(id)) {
            // Update our conception of the field object
            FieldObject object = fieldObjects.get(id);
            object.timeLastSeen = time;
            // TODO Update our conception of player /ball positions
        }
        else {
            // TODO Parse players
            // TODO Parse ball
            // TODO Parse goal
            // TODO Raise error if still not identified
        }
    }
    
    public void resetKnowledge() {
        canSeeBall = false;
        canSeeGoal = false;
        // TODO Indicate to the client that it's now less likely they are as close to the ball as they were previously
    }
    
    public void run() {
        player.client.log(Settings.LOG_LEVELS.DEBUG, String.format("Running at time step %d...", time));
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
        player.client.log(Settings.LOG_LEVELS.DEBUG, "Got here.");
        resetKnowledge();
        player.client.log(Settings.LOG_LEVELS.DEBUG, String.format("Done running at time step %d...", time));
    }
    
    public final void turn(double direction) {
        player.client.sendCommand(Commands.TURN, direction);
    }

    /* Figure out where I am
     * 
     * Based on the relative distances and directions to field objects most
     * recently seen, update the client's concept of its position.
     * 
     */
    public final void updatePosition() {
        if (fieldObjects.size() > 2) // Required in order to successfully triangulate
        {
            //double distanceBetweenObjects = ???;
        }
        // Otherwise, assume we are in the same place as we were, and do nothing.
    }
    
    private final void updateBallPosition(double angle) {
        // TODO
    }

    private final void updateBallPosition(double distance, double angle) {
        // TODO
    }

    private final void updateKnowledge(SeeInfo seeInfoType, Object...args) {
        switch(seeInfoType) {
        case GOAL_ANGLE:
            // TODO
            break;
        }
    }
}
