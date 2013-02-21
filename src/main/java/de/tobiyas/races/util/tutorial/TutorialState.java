package de.tobiyas.races.util.tutorial;

public enum TutorialState {
	
	none("not running", new String[]{"start"}, null),
	end("End", new String[]{"stop", "reset", "repost"}, none),
	
	channels("channels", new String[]{"skip", "stop", "reset", "repost"}, end),
	
	selectClass("select Class",new String[]{"skip", "stop", "reset", "repost"}, channels),
	infoClass("info Class", new String[]{"skip", "stop", "reset", "repost"}, selectClass),
	
	selectRace("select Race", new String[]{"skip", "stop", "reset", "repost"}, infoClass),
	infoRace("info Race", new String[]{"skip", "stop", "reset", "repost"}, selectRace),
	
	start("start", new String[]{"skip", "stop", "repost"}, infoRace);
	
	
	private String stateName;
	private String[] acceptedCommands;
	private TutorialState nextState;
	
	TutorialState(String stateName, String[] acceptedCommands, TutorialState nextState){
		this.stateName = stateName;
		this.acceptedCommands = acceptedCommands;
		this.nextState = nextState;
	}
	
	public String getStateName(){
		return stateName;
	}
	
	public boolean isAccepted(String command){
		for(String commandIntern : acceptedCommands){
			if(commandIntern.equalsIgnoreCase(command))
				return true;
		}
		
		return false;
	}
	
	public TutorialState getNextStep(){
		return nextState;
	}
	
	public static TutorialState getState(String name){
		for(TutorialState state : TutorialState.values())
			if(state.name().equalsIgnoreCase(name))
				return state;
		
		return none;
	}
}
