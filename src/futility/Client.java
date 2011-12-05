/** @file Client.java
 * Network agent that handles UDP communication with the game server.
 * 
 * @author Team F(utility)
 */

package futility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Network client that initializes a connection to the RoboCup 2D soccer server.
 * All UDP communication is handled by this class.
 */
public class Client {
    public boolean debugMode = Settings.DEBUG;
    public boolean hideReceivedMessages = false;
    public Player player;
    public InetAddress soccerServerHost;
    public int soccerServerPort = Settings.INIT_PORT;
    public DatagramSocket soccerServerSocket;

    /**
     * Client constructor. Set up a client to play some virtual soccer!
     * 
     * @param args the same arguments passed to the process
     */
    public Client(String[] args) {
        this.player = new Player(this);  // Initialize an associated player
        // Process command-line arguments
        for (int i = 0; i < args.length; i++ ) {
            try {
                if (args[i].equals("-d") || args[i].equals("--debug")) {
                    // Run in debug mode (no specific functionality right now)
                    this.debugMode = true;
                    if (Settings.VERBOSITY < Log.DEBUG) {
                        Settings.VERBOSITY = Log.DEBUG;
                    }
                }
                if (args[i].equals("--goalie")) {
                    // This client is for the goalie
                    this.player.brain.role = PlayerRole.Role.GOALIE;
                }
                if (args[i].equals("-s") || args[i].equals("--strategy")) {
                    try {
                        this.player.brain.overrideStrategy(Brain.Strategy.valueOf(args[i+1]));
                    }
                    catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
                if (args[i].equals("-t") || args[i].equals("--team")) {
                    // Read the team name from the command-line
                    this.player.team.name = args[i+1];
                }
                else if (args[i].equals("-h") || args[i].equals("--hide-received-messages")) {
                    this.hideReceivedMessages = true;
                }
                else if (args[i].equals("-v") || args[i].equals("--verbosity")) {
                    // Set the verbosity to a custom level:
                    //   * 0 is errors only
                    //   * 1 is errors and important info
                    //   * 2 is errors, important info and extra debug info
                    //   * 3 is 0-2 with the addition of received server messages
                    //  The default is specified in Settings.java.
                    Settings.VERBOSITY = Integer.parseInt(args[i+1]);
                }
            }
            catch (Exception e) {
                Log.e("Invalid command-line parameters.");
            }
        }
    }
    
    /** 
     * Initalizes a client. Schedules and starts related threads and connects
     * to the server.
     */
    public void init() {
        try {
            // Set up server connection
            soccerServerHost = InetAddress.getByName(Settings.HOSTNAME);
            soccerServerSocket = new DatagramSocket();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        String version = String.format("(version %s)", Settings.SOCCER_SERVER_VERSION);
        if (this.player.brain.role != PlayerRole.Role.GOALIE) {
            sendCommand(Settings.Commands.INIT, player.team.name, version);
        }
        else {
            sendCommand(Settings.Commands.INIT, player.team.name, version, "(goalie)");
        }
        // Start reading input from the server
    }
    
    /**
     * Puts the client into an infinite loop for gameplay.
     */
    public final void playForever() {
        while(true){
            player.brain.parseMessage(receiveMessage());
        }
    }

    /**
     * Disconnects from the server.
     * 
     */
    public final void quit() {
        sendCommand(Settings.Commands.BYE);
        soccerServerSocket.close();
    }
    
    /**
     * Receives a message from the server.
     * 
     * @return message sent by the server
     */
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
        String message = new String(buffer);
        if (!this.hideReceivedMessages) {
            Log.d("RECEIVED: " + message);
        }
        if (message.startsWith("(e")) {
            Log.e(this.player.render() + " RECEIVED: " + message);
        }
        return new String(buffer);
    }

    /**
     * Sends a properly-formatted message to the server.
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
        this.sendMessage(partial);
    }
    
    /**
     * Sends a message to the soccer server.
     * 
     * @param message to send to the server
     */
    private void sendMessage(String message) {
    	Log.d("Sending: " + message);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, soccerServerHost, soccerServerPort);
        try {
            soccerServerSocket.send(packet);
        }
        catch (IOException e) {
            System.err.println("socket sending error " + e);
        }
    }
 }
