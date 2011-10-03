package futility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import futility.Commands;

public class Client {
    double ballDistance;
    double ballAngle;
    boolean canKickBall;
    boolean canSeeBall;
    boolean canSeeGoal;
    boolean debugMode = Settings.DEBUG;
    double goalAngle;
    double goalDistance;
    PlayerInfo info;
    boolean listening;
    String lastMessageTypeParsed;
    char opponentTeamSide;
    int verbosity = Settings.VERBOSITY;
    int playerNum;
    boolean printCommands;
    boolean printReceivedMessages;
    InetAddress soccerServerHost;
    int soccerServerPort = Settings.INIT_PORT;
    DatagramSocket soccerServerSocket;
    ScheduledThreadPoolExecutor actionExecutor;
    DatagramSocket socket;
    String teamName = Settings.TEAM_NAME;
    char teamSide;
    int currentTimeStep;

    public Client(String[] args) {
        // Parse command-line arguments
        for (int i = 1; i < args.length; i++ )
        {
            try 
            {
                if (args[i].equals("-t") || args[i].equals("--team"))
                {
                    teamName = args[i+1];
                }
                else if (args[i].equals("-d") || args[i].equals("--debug"))
                {
                    debugMode = true;
                }
                else if (args[i].equals("-v") || args[i].equals("--verbosity"))
                {
                    verbosity = Integer.parseInt(args[i+1]);
                }
            }
            catch (Exception e )
            {
                System.out.println("Invalid command-line parameters.");
            }
        }
        try {
            // Set up server connection
            soccerServerHost = InetAddress.getByName(Settings.HOSTNAME);
            soccerServerSocket = new DatagramSocket();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        sendCommand(Commands.INIT, teamName, String.format("(version %s)", Settings.SOCCER_SERVER_VERSION));
        // Start reading input from the server
        actionExecutor = new ScheduledThreadPoolExecutor(1);
        actionExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        listening = true;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while(listening){
                    parse(receive());
                }
            }
        });
        t.start();
        // Start sending commands back to the server
        actionExecutor.scheduleAtFixedRate(new ActionRunnable(this), 0, 100, TimeUnit.MILLISECONDS);
    }
 
    public void dash(Double power) {
        sendCommand(Commands.DASH, power);
    }

    public void dash(Double power, Double direction) {
        sendCommand(Commands.DASH, power, direction);
    }
    
    /**
     * Execute an action in the current timestep
     */
    public void act() {
        double approachAngle;
        double power = Math.min(100, 10 + ballDistance * 20);
        if (canKickBall) {
            if (canSeeGoal) {
                kick(100.0, goalAngle);
            }
            else {
                dash(30.0, 90.0);
            }
        }
        else if (canSeeBall) {
            if (canSeeGoal) {
                double approachAngleDelta = ballDistance /10;
                approachAngle = ballAngle + Math.copySign(1.0, -goalAngle) * approachAngleDelta;
                if (ballAngle > goalAngle) {
                    approachAngle =  + approachAngleDelta;
                }
                else {
                    approachAngle = goalAngle - approachAngleDelta;
                }
                dash(power, approachAngle);
            }
            else {
                dash(power);
            }
        }
        else {
            if (ballAngle > 0) {
                turn(7.0);
            }
            else {
                turn(-7.0);
            }
        }
    }

    public void kick(Double power) {
        sendCommand(Commands.KICK, power);
    }

    public void kick(Double power, Double direction) {
        sendCommand(Commands.KICK, power, direction);
    }

    public void parse(String message) {
        // Update the mLastMessageTypeParsed variable
        String[] parts = message.split(" ");
        lastMessageTypeParsed = new String(parts[0].substring(1, parts[0].length()));

        // Handle various message
        if (message.startsWith("(init ")) {
            teamSide = parts[1].charAt(0);
            if (teamSide == 'l') {
                opponentTeamSide = 'r';
            }
            else if (teamSide == 'r') {
                opponentTeamSide = 'l';
            }
            else {
                System.err.println("Could not parse teamSide.");
            }
            playerNum = Integer.parseInt(parts[2]);
        }
        else if (message.startsWith("(sense_body")) {
            currentTimeStep = Integer.parseInt(parts[1]);
        }
        else if (message.startsWith("(see ")) {
            // Extract ball info
            String beginsWith = new String("((b) ");
            int beginsAt = message.indexOf(beginsWith);
            if (beginsAt > 0) {
                canSeeBall = true;
                String relevantPart = message.substring(beginsAt + beginsWith.length(), message.indexOf(")", beginsAt + beginsWith.length()));
                String[] relevantParts = relevantPart.split(" ");
                if (relevantParts.length == 1) {
                    ballAngle = Double.valueOf(relevantParts[0]);
                }
                else if (relevantParts.length >= 2) {
                    ballDistance = Double.valueOf(relevantParts[0]);
                    ballAngle = Double.valueOf(relevantParts[1]);
                }
                if (ballDistance < 0.7) {
                    canKickBall = true;
                }
            }
            // Extract opponent goal info
            beginsWith = new String(String.format("((g %s) ", opponentTeamSide));
            beginsAt = message.indexOf(beginsWith);
            if (beginsAt > 0) {
                canSeeGoal = true;
                String relevantPart = message.substring(beginsAt + beginsWith.length(), message.indexOf(")", beginsAt + beginsWith.length()));
                String[] relevantParts = relevantPart.split(" ");
                if (relevantParts.length == 1) {
                    goalAngle = Double.valueOf(relevantParts[0]);
                }
                else if (relevantParts.length >= 2) {
                    goalDistance = Double.valueOf(relevantParts[0]);
                    goalAngle = Double.valueOf(relevantParts[1]);
                }
            }
        }
    }
    
    public void quit() {
        sendCommand(Commands.BYE);
        soccerServerSocket.close();
    }
    
    public String receive() {
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
        canKickBall = false;
        canSeeBall = false;
        canSeeGoal = false;
        ballDistance += 0.3;
    }
    
    /** Send a properly-formatted message to the soccer server
     * 
     * @param command the command to send
     * @param args any amount of object arguments
     */
    public void sendCommand(String command, Object... args) {
        String partial = String.format("(%s", command);
        for (Object arg : args) {
            partial += ' ' + arg.toString();
        }
        partial += ")\0";
        sendMessage(partial);
    }
    
    /**
     * Send a message to the soccer server
     * 
     * @param message
     */
    public void sendMessage(String message)
    {
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

    private void turn(Double direction) {
        sendCommand(Commands.TURN, direction);
    }
 }
