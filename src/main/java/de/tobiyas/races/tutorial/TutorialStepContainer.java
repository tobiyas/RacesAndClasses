package de.tobiyas.races.tutorial;

import de.tobiyas.races.util.tutorial.TutorialState;

public class TutorialStepContainer {

	private String playerName;
	private TutorialState state;
	private int step; //0 : finished, 1 : step 1 finished, 2 : step 2 finished, ...
	
	
	public TutorialStepContainer(String playerName, TutorialState state, int step){
		this.playerName = playerName;
		this.state = state;
		this.step = step;
	}
	
	public TutorialStepContainer(String playerName, TutorialState state){
		this.playerName = playerName;
		this.state = state;
		this.step = 1;
	}
	
	public String getName(){
		return playerName;
	}
	
	public TutorialState getState(){
		return state;
	}
	
	public int getStep(){
		return step;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof TutorialStepContainer))
			return false;
		
		TutorialStepContainer otherState = (TutorialStepContainer) obj;
		if(playerName != otherState.getName()) return false;
		if(state != otherState.getState()) return false;
		if(step != otherState.getStep()) return false;
		
		return true;
		
	}
}
