package futility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    private final int INIT_PORT = 6000;
    private final int MSG_SIZE = 4096;
    private double ballDistance;
    private double ballAngle;
    private boolean canKickBall;
    private boolean canSeeBall;
    private boolean canSeeGoal;
    private double goalAngle;
    private double goalDistance;
    private InetAddress host;
    private String lastMessageTypeParsed;
    private char opponentTeamSide;
    private int playerNum;
    private int port;
    private DatagramSocket socket;
    private String teamName;
    private char teamSide;
    private int time;

    public Client(String[] args) {
        if (args.length > 0)
        {
            teamName = args[0];
        }
    }

    public void connect() {
        try {
            // Set up server connection
            host = InetAddress.getByName("localhost");
            port = INIT_PORT;
            socket = new DatagramSocket();

            // Initialize client
            send(String.format("(init %s (version 15.0))", teamName));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void dash(double power) {
        send(String.format("(dash %f)", power));
    }

    public void dash(double power, double direction) {
        send(String.format("(dash %f %f)", power, direction));
    }

    public void kick(double power) {
        send(String.format("(kick %f)", power));
    }

    public void kick(double power, double direction) {
        send(String.format("(kick %f %f)", power, direction));
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

    public void play() {
        while (true) {
            parse(receive());
            if (timeToRespond()) {
                respond();
                resetKnowledge();
            }
        }
    }

    public void quit() {
        send("(bye)");
        socket.close();
    }

    public String receive() {
        byte[] buffer = new byte[MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);
        try {
            socket.receive(packet);
            if (port == INIT_PORT) {
                port = packet.getPort();
            }
        }
        catch (IOException e) {
            System.err.println("socket receiving error " + e);
        }
        return new String(buffer);
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
                kick(100, goalAngle);
            }
            else {
                dash(30, 90);
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
                turn(7);
            }
            else {
                turn(-7);
            }
        }
    }

    private void send(String message)
    {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, port);
        try {
            socket.send(packet);
        }
        catch (IOException e) {
            System.err.println("socket sending error " + e);
        }
    }

    private boolean timeToRespond() {
        if (lastMessageTypeParsed.equals("sense_body")) {
            return true;
        }
        else {
            return false;
        }
    }

    private void turn(double direction) {
        send(String.format("(turn %f)\0", direction));
    }
 }
