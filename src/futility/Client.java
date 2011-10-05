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

public class Client implements Runnable {
    boolean debugMode = Settings.DEBUG;
    Player player;
    ScheduledThreadPoolExecutor responseExecutor;
    InetAddress soccerServerHost;
    int soccerServerPort = Settings.INIT_PORT;
    DatagramSocket soccerServerSocket;
    DatagramSocket socket;
    int verbosity = Settings.VERBOSITY;

    /** Client constructor
     * 
     * Set up a client to play some virtual soccer!
     * 
     * @param args the same arguments passed to the process
     */
    public Client(String[] args) {
        for (int i = 0; i < args.length; i++ ) {
            try {
                if (args[i].equals("-t") || args[i].equals("--team")) {
                    player.team.name = args[i+1];
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
        
        sendCommand(Commands.INIT, player.team.name, String.format("(version %s)", Settings.SOCCER_SERVER_VERSION));
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

    /** Respond during the current timestep
     */
    @Override
    public void run() {
        player.brain.run();
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
 }
