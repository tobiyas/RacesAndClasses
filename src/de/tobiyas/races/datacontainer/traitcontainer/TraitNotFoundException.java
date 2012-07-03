package de.tobiyas.races.datacontainer.traitcontainer;

public class TraitNotFoundException extends Exception {

	private static final long serialVersionUID = 7879171651303580001L;
	
	public TraitNotFoundException(String msg){
		super(msg);
	}
	
	public TraitNotFoundException(){
		super();
	}
	
	public TraitNotFoundException(String traitName, String containerName){
		super("Could not find Trait: " + traitName + " for: " + containerName);
	}
	
}
