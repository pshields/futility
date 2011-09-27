package futility;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import futility.Settings;

public class Server {
    InetAddress host = null;
    int port;
    Settings settings = new Settings();
    DatagramSocket socket;
    
    public Server() {
        port = settings.INIT_PORT;
        try {
            // Set up server connection
            host = InetAddress.getByName(settings.HOSTNAME);
            socket = new DatagramSocket();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    
    public void quit() {
        send("(bye)");
        socket.close();
    }
    
    public String receive() {
        byte[] buffer = new byte[settings.MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, settings.MSG_SIZE);
        try {
            socket.receive(packet);
            if (port == settings.INIT_PORT) {
                port = packet.getPort();
            }
        }
        catch (IOException e) {
            System.err.println("socket receiving error " + e);
        }
        return new String(buffer);
    }
    
    /**
     * Send a message to the soccer server
     * 
     * @param message
     */
    public void send(String message)
    {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, port);
        try {
            socket.send(packet);
        }
        catch (IOException e) {
            System.err.println("socket sending error " + e);
        }
    }
    
    /** Send a message to the soccer server
     * 
     * It will be properly formatted for free!
     */
    public void send(String command, String[] args) {
        send(String.format("(%s %s)", command, StringUtils.join(args, " ")));
    }

}
