// Base class for our client, to isolate player logic from interaction
// functions like kick or turn. Put interaction functions here.

package futility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player extends MobileObject {
    public boolean canSeeBall;
    public boolean canSeeGoal;
    boolean debugMode = Settings.DEBUG;
    public int number;
    public Team otherTeam = new Team();
    public Team team = new Team();
    public int time;
    InetAddress soccerServerHost;
    int soccerServerPort = Settings.INIT_PORT;
    DatagramSocket soccerServerSocket;
    int verbosity = Settings.VERBOSITY;
    LinkedList<GameObject> gameObjects = new LinkedList();
    
    // Object models
    public Rectangle field = new Rectangle(Settings.FIELD_BUFFER + Settings.FIELD_HEIGHT, Settings.FIELD_BUFFER + Settings.FIELD_WIDTH, Settings.FIELD_BUFFER, Settings.FIELD_BUFFER);
    public MobileObject ball = new MobileObject();
    public StationaryObject goal = new StationaryObject();

    public enum SeeInfo {
        GOAL_ANGLE,
        GOAL_DISTANCE
    }
    
    public final boolean canKickBall() {
        if (!inRectangle(field)) {
            return false;
        }
        if (distanceTo(ball) > 0.7) {
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

    public final void dash(Double power) {
        sendCommand(Commands.DASH, power);
    }

    public final void dash(Double power, Double direction) {
        sendCommand(Commands.DASH, power, direction);
    }

    public void kick(Double power) {
        sendCommand(Commands.KICK, power);
    }

    public void kick(Double power, Double direction) {
        sendCommand(Commands.KICK, power, direction);
    }
    
    public void log(String message) {
        System.out.println(message);
    }
    
    public void log(int verbosity, String message) {
        if (this.verbosity >= verbosity) {
            if (verbosity == Settings.LOG_LEVELS.DEBUG) {
                log("DEBUG: " + message);
            }
            else if (verbosity == Settings.LOG_LEVELS.INFO) {
                log("INFO: " + message);
            }
            else if (verbosity == Settings.LOG_LEVELS.ERROR) {
                System.err.println("ERROR: " + message);
            }
            else {
                log(String.format("UNKNOWN VERBOSITY LEVEL %d", verbosity) + message);
            }
        }
    }

    public void parseMessage(String message) {
        // Remove parenthesis for better readability
        String[] parts = message.substring(1, message.length() - 1).split(" ");
        // Handle init messages
        if (parts[0].equals("init")) {
            char teamSide = parts[1].charAt(0);
            if (teamSide == Settings.LEFT_SIDE) {
                team.side = Settings.LEFT_SIDE;
                otherTeam.side = Settings.RIGHT_SIDE;
            }
            else if (teamSide == Settings.RIGHT_SIDE) {
                team.side = Settings.RIGHT_SIDE;
                otherTeam.side = Settings.LEFT_SIDE;
            }
            else {
                System.err.println("Could not parse teamSide.");
            }
            number = Integer.parseInt(parts[2]);
        }
        // Handle sense_body messages
        else if (parts[0].equals("sense_body")) {
            time = Integer.parseInt(parts[1]);
        }
        // Handle see messages
        else if (parts[0].equals("see")) {
            // Isolate the substring inside of the see (an object info followed by parameters.)
            String substring = message.substring(5, message.length() - 1);
            // Get object info. The regular expression below should capture any object info.
            Pattern pattern = Pattern.compile("\\((\\(\\w\\))?[\\w\\s\\.]*\\)?)");
            Matcher matcher = pattern.matcher(substring);
            matcher.find(); // Assume it found one
            String objectInfo = substring.substring(0, matcher.end());
            gameObjects.add(parseObjectInfo(objectInfo));
        }
    }
    
    public final void parseCommandLineArguments(String[] args) {
        for (int i = 0; i < args.length; i++ ) {
            try {
                if (args[i].equals("-t") || args[i].equals("--team")) {
                    team.name = args[i+1];
                }
                else if (args[i].equals("-d") || args[i].equals("--debug")) {
                    debugMode = true;
                    if (verbosity < Settings.LOG_LEVELS.DEBUG) {
                        verbosity = Settings.LOG_LEVELS.DEBUG;
                    }
                }
                else if (args[i].equals("-v") || args[i].equals("--verbosity")) {
                    verbosity = Integer.parseInt(args[i+1]);
                }
            }
            catch (Exception e) {
                System.out.println("Invalid command-line parameters.");
            }
        }
    }
    
    public final GameObject parseObjectInfo(String content) {
        if (content.startsWith("(f")) {
            return new Flag(content);
        }
        else {
            return new Flag("(f l t)"); // TODO Fix this! Only there to appease the compiler.
        }
    }
    
    
    /** Initialize a new client
     * 
     * @param args arguments to treat as if they were command-line arguments
     */
    public static final void initClient(String[] args) {
        Client client = new Client(args);
        client.init();
    }
    
    /** Initialize a client for a specific team
     * 
     * @param args arguments to treat as if they were command-line arguments
     * @param teamName a team name to override any other defaults
     */
    public static final void initClient(String[] args, String teamName) {
        Client client = new Client(args);
        client.team.name = teamName;
        client.init();
    }
    

    public final void quit() {
        sendCommand(Commands.BYE);
        soccerServerSocket.close();
    }

    public String receiveMessage() {
        byte[] buffer = new byte[Settings.MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, Settings.MSG_SIZE);
        try {
            soccerServerSocket.receive(packet);
            if (soccerServerPort == Settings.INIT_PORT) {
                soccerServerPort = packet.getPort();
            }
        }
        catch (IOException e) {
            System.err.println("socket receiving error " + e);
        }
        if (verbosity >= 2) {
            System.out.println(new String(buffer));
        }
        return new String(buffer);
    }

    public void resetKnowledge() {
        canSeeBall = false;
        canSeeGoal = false;
        // TODO Indicate to the client that it's now less likely they are as close to the ball as they were previously
    }

    /**
     * Send a message to the soccer server
     * 
     * @param message
     */
    private void sendMessage(String message) {
        if (debugMode || (verbosity >= 1)) {
            System.out.println(message);
        }
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, soccerServerHost, soccerServerPort);
        try {
            soccerServerSocket.send(packet);
        }
        catch (IOException e) {
            System.err.println("socket sending error " + e);
        }
    }

    /** Send a properly-formatted message to the soccer server
     * 
     * @param command the command to send
     * @param args any amount of object arguments
     */
    public final void sendCommand(String command, Object... args) {
        String partial = String.format("(%s", command);
        for (Object arg : args) {
            partial += ' ' + arg.toString();
        }
        partial += ")\0";
        sendMessage(partial);
    }
    
    public static final void startTeam(String[] args) {
        for (int i=0; i<=10; i++) {
            initClient(args);
        }
    }
    
    public static final void startTeam(String[] args, String teamName) {
        for (int i=0; i<=10; i++) {
            initClient(args, teamName);
        }
    }

    public final void turn(Double direction) {
        sendCommand(Commands.TURN, direction);
    }

    /* Figure out where I am
     * 
     * Based on the relative distances and directions to field objects most
     * recently seen, update the client's concept of its position.
     * 
     */
    public final void updatePosition(FieldObject[] objects) {
        if (objects.length > 2) // Required in order to successfully triangulate
        {
            // TODO
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
