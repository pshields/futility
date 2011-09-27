package futility;

public class ActionItem{

	String mValue;
	
	public ActionItem() {
		mValue = null;
	}
	
	public ActionItem(String value){
		mValue = value;
	}
	
	/**
	 * returns the string in action format 
	 * for the server ie (dash 100)
	 */
	@Override
	public String toString() {
		return super.toString();
	}
	
	public static class Builder{
		
		private String action, arg0, arg1;
		
		public Builder() {		}
		
		public void setAction(String action){
			this.action = action;
		}
		
		public void addArgument(String arg){
			if(arg0 == null)arg0 = arg;
			else if(arg1 == null) arg1 = arg;
		}
		
		public ActionItem build(){
			if(action == null) throw new NullPointerException("must set the action");
			if(arg1 == null && arg0 != null)return new ActionItem(String.format("(%s %s)", action, arg0));
			else if(arg0 == null) return new ActionItem(String.format("(%s)", action));
			else return new ActionItem(String.format("(%s %s %s)",action, arg0, arg1));
		}
	}
}
