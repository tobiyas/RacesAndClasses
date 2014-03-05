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
package de.tobiyas.racesandclasses.tutorial;

import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

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
