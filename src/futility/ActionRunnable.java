package futility;

public class ActionRunnable implements Runnable {

	private RoboClient mClient;
//	private Timer mTimer;
	
	public ActionRunnable(RoboClient client){
		mClient = client;
	}
	
	@Override
	public void run() {
		mClient.sendActions();
	}

}
