package splat.executor;

public class ReturnFromCall extends Exception {

	private Value returnVal;
	
	public ReturnFromCall(Value returnVal) {
		this.returnVal = returnVal;
	}

	public ReturnFromCall() { this.returnVal = null; };
	
	public Value getReturnVal() {
		return returnVal;
	}
}
