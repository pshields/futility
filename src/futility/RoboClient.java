package futility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RoboClient {

	final static String VERSION = "15.0";
	
	private String mTeamName;
	private InetAddress mAddress;
	private int mPort;
	private DatagramSocket mSocket;
	private PlayerInfo mInfo;
	private boolean mConnected;
	private boolean mListening;
	private ScheduledThreadPoolExecutor mActionExecutor;
	
	public RoboClient(String name, String host) throws UnknownHostException, SocketException{
		mTeamName = name;
		mAddress = InetAddress.getByName(host);
		mSocket = new DatagramSocket();
		mInfo = new PlayerInfo();
		mPort = -1;
		mConnected = false;
		mActionExecutor = new ScheduledThreadPoolExecutor(2);
		mActionExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
	}
	
	public void init() throws IOException{
		//connect
		sendMessage(mSocket, String.format("(init %s (version %s))", mTeamName, VERSION), mAddress, 6000);
		//handshake
		byte[] buffer = new byte[1024 * 8];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		mSocket.receive(packet);
		mPort = packet.getPort();
		mConnected = true;
		//set state from response
		final String s = new String(packet.getData(), 0, packet.getData().length);
		mInfo.setState(s);		        
	}
	
	public void startListening(){
		if(!mConnected){
			//TODO reconnect to server
		}
		mListening = true;
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(mListening){
					try {
						final String incoming = receiveMessage(mSocket);
						mInfo.setState(incoming);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
			}
		});
		t.start();
	}
	
	public void closeConnection() throws IOException{
		stopWriting();
		stopListening();
	}
	
	//public void startWriting(){
		//warning calling this function more than once is a bad thing
		//should add a safty net to ensure that doesn't happen
		//mActionExecutor.scheduleAtFixedRate(new ActionRunnable(this), 0, 100, TimeUnit.MILLISECONDS);
	//}
	
	public void sendActions() {
		//can send 3 actions at a time
		synchronized (mInfo) {
			int count = 0;
			while(mInfo.getActionItems().size() > 0 && count < 3){
				try {
					sendMessage(mSocket, mInfo.getActionItems().pop().toString(), mAddress, mPort);
					count++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	private void stopWriting(){
		synchronized (this) {
			mActionExecutor.shutdown();
		}
	}
	
	private void stopListening() throws IOException{
		synchronized (this) {
			mListening = false;
			mConnected = false;
		}
		sendMessage(mSocket, "(bye)", mAddress, mPort);
	}
	
	private void sendMessage(DatagramSocket socket, String message, InetAddress address, int port) throws IOException{
		byte[] bytes = message.getBytes();
		socket.send(new DatagramPacket(bytes, bytes.length, address, port));
	}
	
	private String receiveMessage(DatagramSocket socket) throws IOException{
		byte[] buffer = new byte[1024 * 8];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		return new String(packet.getData(), 0, packet.getData().length);
	}
	
}
