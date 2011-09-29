// Not currently in use. Player info is currently handled directly in Client class.

package futility;

import java.util.LinkedList;

import futility.ActionItem.Builder;

public class PlayerInfo {

	private boolean mLeftSide;
	private int mPlayerNumber;
	//etc
	
	private LinkedList<ActionItem> mActionItems;
	
	public PlayerInfo(){
		mActionItems = new LinkedList<ActionItem>();
	}
	
	/**
	 * Parse and store info about the client
	 * @param state info coming from server
	 */
	public void setState(String state){
	
		
		//TODO add actions based on state
		//example
		ActionItem.Builder b = new Builder();
		b.setAction("dash");
		b.addArgument("100");
		synchronized (this) {
			//any push needs to be synchronized a differnt
			//thread will be reading these actions.
			mActionItems.push(b.build());
		}
	}

	public void setLeftSide(boolean mLeftSide) {
		this.mLeftSide = mLeftSide;
	}

	public boolean isLeftSide() {
		return mLeftSide;
	}

	public void setPlayerNumber(int mPlayerNumber) {
		this.mPlayerNumber = mPlayerNumber;
	}

	public int getPlayerNumber() {
		return mPlayerNumber;
	}
	
	public LinkedList<ActionItem> getActionItems(){
		return mActionItems;
	}
}
