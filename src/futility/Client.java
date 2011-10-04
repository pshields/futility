package futility;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import futility.Commands;

public class Client extends Player implements Runnable {
    boolean listening;
    ScheduledThreadPoolExecutor actionExecutor;
    DatagramSocket socket;

    /** Client constructor
     * 
     * Set up a client to play some virtual soccer!
     * 
     * @param args the same arguments passed to the process
     */
    public Client(String[] args) {
        parseCommandLineArguments(args);
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
        
        sendCommand(Commands.INIT, team.name, String.format("(version %s)", Settings.SOCCER_SERVER_VERSION));
        // Start reading input from the server
        actionExecutor = new ScheduledThreadPoolExecutor(1);
        actionExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        listening = true;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while(listening){
                    parseMessage(receiveMessage());
                }
            }
        });
        t.start();
        // Start sending commands back to the server
        log(Settings.LOG_LEVELS.DEBUG, "Scheduling client to run every 100 milliseconds...");
        actionExecutor.scheduleAtFixedRate(this, 0, 100, TimeUnit.MILLISECONDS);
    }
    
    /** Main function
     *
     * First function to execute. Reads command-line arguments and activates
     * one or more clients as specified in the args.
     * 
     * The ability to activate multiple clients here is meant as a CPU-saving
     * technique for development. When competing, use the separate spin-up
     * script to ensure complete process isolation. 
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        boolean startedClients = false;
        for (int i = 0; i < args.length; i++ )
        {
            try 
            {
                if (args[i].equals("-c") || args[i].equals("--compete"))
                {
                    startTeam(args);
                    startTeam(args, Settings.OTHER_TEAM_NAME);
                    startedClients = true;
                }
                else if (args[i].equals("-s") || args[i].equals("--start-team"))
                {
                    startTeam(args);
                    startedClients = true;
                }
            }
            catch (Exception e)
            {
                System.out.println("Invalid command-line parameters.");
            }
        }
        
        if (!startedClients) {
            initClient(args);
        }
    }
    
    /** Respond during the current timestep
     */
    @Override
    public void run() {
        log(Settings.LOG_LEVELS.DEBUG, String.format("Running at time step %d...", time));
        double approachAngle;
        double power = Math.min(100, 10 + distanceTo(ball) * 20);
        if (canKickBall()) {
            if (canSeeGoal) {
                kick(100.0, angleTo(goal));
            }
            else {
                dash(30.0, 90.0);
            }
        }
        else if (canSeeBall) {
            if (canSeeGoal) {
                double approachAngleDelta = distanceTo(ball)/10;
                approachAngle = angleTo(ball) + Math.copySign(1.0, -angleTo(goal)) * approachAngleDelta;
                if (angleTo(ball) > angleTo(goal)) {
                    approachAngle =  + approachAngleDelta;
                }
                else {
                    approachAngle = angleTo(goal) - approachAngleDelta;
                }
                dash(power, approachAngle);
            }
            else {
                dash(power);
            }
        }
        else {
            if (angleTo(ball) > 0) {
                turn(7.0);
            }
            else {
                turn(-7.0);
            }
        }
        log(Settings.LOG_LEVELS.DEBUG, "Got here.");
        resetKnowledge();
        log(Settings.LOG_LEVELS.DEBUG, String.format("Done running at time step %d...", time));
    }
 }
