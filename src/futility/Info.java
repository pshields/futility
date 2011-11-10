package futility;

/**
 *Encapsulates the data received in a see message 
 *{@link SeeInfo} & {@link SenseInfo} extend this class
 */
public abstract class Info {

	int time;
	
	public Info(){
		reset();
	}
	
	public void reset(){
		this.time = -1;
	}
	
}
