package futility;

public class ActionRunnable implements Runnable {

	private Client mClient;
//	private Timer mTimer;
	
	public ActionRunnable(Client client){
		mClient = client;
	}
	
	@Override
	public void run() {
        mClient.respond();
        mClient.resetKnowledge();
	}
}