package futility;

import futility.Client;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Client client = new Client(args);
        client.connect();
        client.play();
        client.quit();
    }

}
