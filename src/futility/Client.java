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
    double mBallDistance;
    double mBallAngle;
    boolean mCanKickBall;
    boolean mCanSeeBall;
    boolean mCanSeeGoal;
    boolean mDebugMode = Settings.DEBUG;
    double mGoalAngle;
    double mGoalDistance;
    PlayerInfo mInfo;
    boolean mListening;
    String mLastMessageTypeParsed;
    char mOpponentTeamSide;
    int mVerbosity = Settings.VERBOSITY;
    int mPlayerNum;
    boolean mPrintCommands;
    boolean mPrintReceivedMessages;
    InetAddress mSoccerServerHost;
    int mSoccerServerPort = Settings.INIT_PORT;
    DatagramSocket mSoccerServerSocket;
    ScheduledThreadPoolExecutor mActionExecutor;
    DatagramSocket mSocket;
    String mTeamName = Settings.TEAM_NAME;
    char mTeamSide;
    int mCurrentTimeStep;

    public Client(String[] args) {
        // Parse command-line arguments
        for (int i = 1; i < args.length; i++ )
        {
            try 
            {
                if (args[i].equals("-t") || args[i].equals("--team"))
                {
                    mTeamName = args[i+1];
                }
                else if (args[i].equals("-d") || args[i].equals("--debug"))
                {
                    mDebugMode = true;
                }
                else if (args[i].equals("-v") || args[i].equals("--verbosity"))
                {
                    mVerbosity = Integer.parseInt(args[i+1]);
                }
            }
            catch (Exception e )
            {
                System.out.println("Invalid command-line parameters.");
            }
        }
        try {
            // Set up server connection
            mSoccerServerHost = InetAddress.getByName(Settings.HOSTNAME);
            mSoccerServerSocket = new DatagramSocket();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        sendCommand(Commands.INIT, mTeamName, String.format("(version %s)", Settings.SOCCER_SERVER_VERSION));
        // Start reading input from the server
        mActionExecutor = new ScheduledThreadPoolExecutor(1);
        mActionExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        mListening = true;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while(mListening){
                    parse(receive());
                }
            }
        });
        t.start();
        // Start sending commands back to the server
        mActionExecutor.scheduleAtFixedRate(new ActionRunnable(this), 0, 100, TimeUnit.MILLISECONDS);
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
        double power = Math.min(100, 10 + mBallDistance * 20);
        if (mCanKickBall) {
            if (mCanSeeGoal) {
                kick(100.0, mGoalAngle);
            }
            else {
                dash(30.0, 90.0);
            }
        }
        else if (mCanSeeBall) {
            if (mCanSeeGoal) {
                double approachAngleDelta = mBallDistance /10;
                approachAngle = mBallAngle + Math.copySign(1.0,  - mGoalAngle) * approachAngleDelta;
                if (mBallAngle > mGoalAngle) {
                    approachAngle =  + approachAngleDelta;
                }
                else {
                    approachAngle = mGoalAngle - approachAngleDelta;
                }
                dash(power, approachAngle);
            }
            else {
                dash(power);
            }
        }
        else {
            if (mBallAngle > 0) {
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
        mLastMessageTypeParsed = new String(parts[0].substring(1, parts[0].length()));

        // Handle various message
        if (message.startsWith("(init ")) {
            mTeamSide = parts[1].charAt(0);
            if (mTeamSide == 'l') {
                mOpponentTeamSide = 'r';
            }
            else if (mTeamSide == 'r') {
                mOpponentTeamSide = 'l';
            }
            else {
                System.err.println("Could not parse teamSide.");
            }
            mPlayerNum = Integer.parseInt(parts[2]);
        }
        else if (message.startsWith("(sense_body")) {
            mCurrentTimeStep = Integer.parseInt(parts[1]);
        }
        else if (message.startsWith("(see ")) {
            // Extract ball info
            String beginsWith = new String("((b) ");
            int beginsAt = message.indexOf(beginsWith);
            if (beginsAt > 0) {
                mCanSeeBall = true;
                String relevantPart = message.substring(beginsAt + beginsWith.length(), message.indexOf(")", beginsAt + beginsWith.length()));
                String[] relevantParts = relevantPart.split(" ");
                if (relevantParts.length == 1) {
                    mBallAngle = Double.valueOf(relevantParts[0]);
                }
                else if (relevantParts.length >= 2) {
                    mBallDistance = Double.valueOf(relevantParts[0]);
                    mBallAngle = Double.valueOf(relevantParts[1]);
                }
                if (mBallDistance < 0.7) {
                    mCanKickBall = true;
                }
            }
            // Extract opponent goal info
            beginsWith = new String(String.format("((g %s) ", mOpponentTeamSide));
            beginsAt = message.indexOf(beginsWith);
            if (beginsAt > 0) {
                mCanSeeGoal = true;
                String relevantPart = message.substring(beginsAt + beginsWith.length(), message.indexOf(")", beginsAt + beginsWith.length()));
                String[] relevantParts = relevantPart.split(" ");
                if (relevantParts.length == 1) {
                    mGoalAngle = Double.valueOf(relevantParts[0]);
                }
                else if (relevantParts.length >= 2) {
                    mGoalDistance = Double.valueOf(relevantParts[0]);
                    mGoalAngle = Double.valueOf(relevantParts[1]);
                }
            }
        }
    }
    
    public void quit() {
        sendCommand(Commands.BYE);
        mSoccerServerSocket.close();
    }
    
    public String receive() {
        byte[] buffer = new byte[Settings.MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, Settings.MSG_SIZE);
        try {
            mSoccerServerSocket.receive(packet);
            if (mSoccerServerPort == Settings.INIT_PORT) {
                mSoccerServerPort = packet.getPort();
            }
        }
        catch (IOException e) {
            System.err.println("socket receiving error " + e);
        }
        if (mVerbosity >= 2) {
            System.out.println(new String(buffer));
        }
        return new String(buffer);
    }

    public void resetKnowledge() {
        mCanKickBall = false;
        mCanSeeBall = false;
        mCanSeeGoal = false;
        mBallDistance += 0.3;
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
        if (mDebugMode || (mVerbosity >= 1)) {
            System.out.println(message);
        }
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, mSoccerServerHost, mSoccerServerPort);
        try {
            mSoccerServerSocket.send(packet);
        }
        catch (IOException e) {
            System.err.println("socket sending error " + e);
        }
    }

    private void turn(Double direction) {
        sendCommand(Commands.TURN, direction);
    }
 }
