package futility;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import futility.Settings;

public class Settings {
    public final String HOSTNAME = "localhost";
    public final int INIT_PORT = 6000;
    public final int MSG_SIZE = 4096;
    public final String TEAM_NAME = "futility";
    public final String SOCCER_SERVER_VERSION = "15.0";
    
    /**
     * Initialize the settings object
     */
    public Settings() {

    }
}
