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
    }

}
