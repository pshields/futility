package futility;

import futility.Client;

public class Main {

    /**
     * Initialize client
     * 
     * Initialize one client for interaction with the soccer server. Call
     * multiple times with various arguments as needed to initialize the whole
     * team.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        String teamName = "futility";
        if (args.length > 0)
        {
            teamName = args[0];
        }
        Client client = new Client(teamName);
//        try {
//            RoboClient client = new RoboClient(teamName, host);
//            client.init();
//            client.startListening();
//            client.startWriting();          
//            //TODO be preparted to take stop commands from standard input
//
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        NetClient cl;
//        String hostname = "localhost";
//        String teamname = "Futility";
//        int port = 6000;
//        boolean coach = false, goalie = false;
//
//        // Parse any command-line arguments; enforce proper format if they
//        //   appear.
//        for ( int i = 1; i < args.length; i++ )
//        {
//            try {
//                if ( args[i].compareTo("-h") == 0 )
//                {
//                    hostname = args[i+1];
//                }
//                else if ( args[i].compareTo("-p") == 0 )
//                {
//                    port = Integer.parseInt(args[i+1]);
//                }
//                else if ( args[i].compareTo("-n") == 0 )
//                {
//                    teamname = args[i+1];
//                }
//                else if ( args[i].compareTo("-g") == 0 )
//                {
//                    goalie = true;
//                }
//                else if ( args[i].compareTo("-c") == 0 )
//                {
//                    coach = true;
//                    port = 6002;
//                }
//            }
//            catch ( Exception e )
//            {
//                System.out.println("Invalid command-line parameters.");
//                System.out.println("Accepted format is:");
//                System.out.println("futility [-c] [-n <team_name>] [-h <host_name>] [-p <port_#>]");
//                System.out.println("\t-c :");
//                System.out.println("\t\tOn-line coach mode. Sets port to 6002 unless specified.");
//                System.out.println("\t-n <team_name> :");
//                System.out.println("\t\tTeam name designation.");
//                System.out.println("\t-h <host_name> :");
//                System.out.println("\t\tHost name or address of the server to connect to.");
//                System.out.println("\t-p <port_#> :");
//                System.out.println("\t\tPort to connect to on the server.");
//            }
//        }
//
//        // Initialize the connection and the client.
//        try
//        {
//            if ( coach )
//                throw new Exception("Cannot instantiate coach client.");
//            else
//                cl = new PlayerClient(hostname, port, teamname,
//                        protocol_version, goalie);
//
//            cl.init();
//        }
//        catch ( Exception e )
//        {
//            System.err.println(e);
//            e.printStackTrace();
//        }
    }

}
