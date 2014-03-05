/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.util.tutorial;

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
