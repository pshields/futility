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

    public Client(String[] args) {
        // Parse command-line arguments
        for (int i = 1; i < args.length; i++ )
        {
            try 
            {
                if (args[i].equals("-t") || args[i].equals("--team"))
                {
                    team.name = args[i+1];
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
                    parse(receiveMessage());
                }
            }
        });
        t.start();
        // Start sending commands back to the server
        actionExecutor.scheduleAtFixedRate(this, 0, 100, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Respond during the current timestep
     */
    @Override
    public void run() {
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
        resetKnowledge();
    }
 }
