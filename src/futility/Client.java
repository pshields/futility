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
    private double ballDistance;
    private double ballAngle;
    private boolean canKickBall;
    private boolean canSeeBall;
    private boolean canSeeGoal;
    private Commands commands = new Commands();
    private double goalAngle;
    private double goalDistance;
    private PlayerInfo mInfo;
    private boolean mListening;
    private String lastMessageTypeParsed;
    private char opponentTeamSide;
    private int playerNum;
    private int port;
    private ScheduledThreadPoolExecutor mActionExecutor;
    private Server server = new Server();
    private Settings settings = new Settings();
    private DatagramSocket socket;
    private String mTeamName;
    private char teamSide;
    private int time;

    public Client(String teamName) {
        mTeamName = teamName;
        String[] initArgs = {mTeamName, String.format("(version %s)", settings.SOCCER_SERVER_VERSION)};
        server.send(commands.INIT, initArgs);
        // Start reading
        mActionExecutor = new ScheduledThreadPoolExecutor(2);
        mActionExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        mListening = true;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while(mListening){
                    final String incoming = server.receive();
                    parse(incoming);
                }
            }
        });
        t.start();
        // Start reading
        mActionExecutor.scheduleAtFixedRate(new ActionRunnable(this), 0, 100, TimeUnit.MILLISECONDS);
    }
    public void dash(Double power) {
        String[] args = {power.toString()};
        server.send(commands.DASH, args);
    }

    public void dash(Double power, Double direction) {
        String[] args = {power.toString(), direction.toString()};
        server.send(commands.DASH, args);
    }

    public void kick(Double power) {
        String[] args = {power.toString()};
        server.send(commands.KICK, args);
    }

    public void kick(Double power, Double direction) {
        String[] args = {power.toString(), direction.toString()};
        server.send(commands.KICK, args);
    }

    public void parse(String message) {
        // Update the lastMessageTypeParsed variable
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
            time = Integer.parseInt(parts[1]);
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

    public void resetKnowledge() {
        canKickBall = false;
        canSeeBall = false;
        canSeeGoal = false;
        ballDistance += 0.3;
    }

    public void respond() {
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
                approachAngle = ballAngle + Math.copySign(1.0, ballAngle - goalAngle) * approachAngleDelta;
                if (ballAngle > goalAngle) {
                    approachAngle = ballAngle + approachAngleDelta;
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
    
    public void start(){
        mActionExecutor.scheduleAtFixedRate(new ActionRunnable(this), 0, 100, TimeUnit.MILLISECONDS);
    }

    private boolean timeToRespond() {
        if (lastMessageTypeParsed.equals("sense_body")) {
            return true;
        }
        else {
            return false;
        }
    }

    private void turn(Double direction) {
        String[] args = {direction.toString()};
        server.send(commands.TURN, args);
    }
 }
