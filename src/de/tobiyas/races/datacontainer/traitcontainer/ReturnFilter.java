package de.tobiyas.races.datacontainer.traitcontainer;

public class ReturnFilter {

	private Object newValueObject;
	private boolean cancle;
	private int operation;
	
	public ReturnFilter(Object newValueObject, int operation, boolean cancle){
		this.newValueObject = newValueObject;
		this.cancle = cancle;
	}
	
	public Object getNewValue(){
		return newValueObject;
	}
	
	public int getOperation(){
		return operation;
	}
	
	public boolean isCancled(){
		return cancle;
	}
}
