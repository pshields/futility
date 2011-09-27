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
        Client client = new Client();
        client.start();
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
    }

}
