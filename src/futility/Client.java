/** @file Client.java
 * Network agent that handles UDP communication with the game server.
 * @author Team F(utility)
 * @date 20 October 2011
 */

package futility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** @class Client
 * Network client that initializes a connection to the robocup soccer server.
 * All UDP communication is handled by this class.
 */
public class Client {
    public boolean debugMode = Settings.DEBUG;
    public boolean hideReceivedMessages = false;
    public Player player;
    public ScheduledThreadPoolExecutor responseExecutor;
    public InetAddress soccerServerHost;
    public int soccerServerPort = Settings.INIT_PORT;
    public DatagramSocket soccerServerSocket;
    public int verbosity = Settings.VERBOSITY;

    /** Client constructor
     * 
     * Set up a client to play some virtual soccer!
     * 
     * @param args the same arguments passed to the process
     */
    public Client(String[] args) {
        player = new Player(this);  // Initialize an associated player
        // Process command-line arguments
        for (int i = 0; i < args.length; i++ ) {
            try {
                if (args[i].equals("-d") || args[i].equals("--debug")) {
                    // Run in debug mode (no specific functionality right now)
                    this.debugMode = true;
                    if (verbosity < Settings.LOG_LEVELS.DEBUG) {
                        this.verbosity = Settings.LOG_LEVELS.DEBUG;
                    }
                }
                if (args[i].equals("-t") || args[i].equals("--team")) {
                    // Read the team name from the command-line
                    player.team.name = args[i+1];
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
                    this.verbosity = Integer.parseInt(args[i+1]);
                }
            }
            catch (Exception e) {
                System.out.println("Invalid command-line parameters.");
            }
        }
    }
    
    /** Initalize a client
     * 
     * Schedule and start related threads and connect to the server
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
        
        sendCommand(Settings.Commands.INIT, player.team.name, String.format("(version %s)", Settings.SOCCER_SERVER_VERSION));
        // Start reading input from the server
        responseExecutor = new ScheduledThreadPoolExecutor(1);
        responseExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    player.brain.parseMessage(receiveMessage());
                }
            }
        });
        t.start();
        // Start sending commands back to the server
        log(Settings.LOG_LEVELS.DEBUG, "Scheduling client to run every 100 milliseconds...");
        responseExecutor.scheduleAtFixedRate(player.brain, 0, 100, TimeUnit.MILLISECONDS);
    }
    
    /** Basic logging shortcut
     * 
     * @param message what to send to the standard output
     */
    public void log(String message) {
        System.out.println(message);
    }
    
    /** Log with verbosity
     * 
     * Allows the inclusion of a verbosity value with a message
     * 
     * @param verbosity the minimum verbosity level the message should display at
     * @param message the message to display
     */
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
    
    /** Disconnect from the server
     * 
     */
    public final void quit() {
        sendCommand(Settings.Commands.BYE);
        soccerServerSocket.close();
    }
    
    /** Receive a message from the soccer server
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
        if (!this.hideReceivedMessages) {
            log(Settings.LOG_LEVELS.DEBUG, "RECEIVED: "+new String(buffer));
        }
        return new String(buffer);
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
    
    /**
     * Sends a message to the soccer server.
     * 
     * @param message to send to the server
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
 }
