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

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.tutorial.steps.ChannelsState;
import de.tobiyas.racesandclasses.tutorial.steps.EndState;
import de.tobiyas.racesandclasses.tutorial.steps.InfoClassState;
import de.tobiyas.racesandclasses.tutorial.steps.InfoRaceState;
import de.tobiyas.racesandclasses.tutorial.steps.SelectClassState;
import de.tobiyas.racesandclasses.tutorial.steps.SelectRaceState;
import de.tobiyas.racesandclasses.tutorial.steps.StartState;
import de.tobiyas.racesandclasses.tutorial.steps.StepInterface;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;
import de.tobiyas.util.config.YAMLConfigExtended;

public class TutorialPath {

	private String playerName;
	private StepInterface currentState;
	private boolean activationSequence;
	
	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	public TutorialPath(String playerName){
		this.playerName = playerName;
		this.activationSequence = false;
		currentState = new StartState(playerName, this);
	}
	
	public TutorialPath(String playerName, boolean activationSequence){
		this.playerName = playerName;
		this.activationSequence = activationSequence;
		currentState = new StartState(playerName, this);
	}
	
	public void save(YAMLConfigExtended config){
		config.load();
		config.createSection("states." + playerName);
		config.set("states." + playerName + ".stateName", currentState.getState().name());
		config.set("states." + playerName + ".stateValue", currentState.getStateValue());
		config.save();
	}
	
	//Handle inputs/stage change
	public boolean skip(){
		currentState.postDoneMessage();
		currentState = constructNewStep(currentState.getState().getNextStep());
		
		if(currentState == null){
			plugin.getTutorialManager().unregister(playerName);
		}
		return true;
	}
	
	public boolean reset(){
		currentState = new StartState(playerName, this);
		return true;
	}
	
	public boolean stop(){
		plugin.getTutorialManager().unregister(playerName);
		return true;
	}
	
	public boolean setState(TutorialState state){
		if(state == null)
			return false;
			
		currentState = constructNewStep(state);
		return true;
	}
	
	public TutorialStepContainer getCurrentState(){
		TutorialStepContainer container = new TutorialStepContainer(playerName, currentState.getState(), currentState.getStateValue());
		return container;
	}
	
	
	public void handle(TutorialStepContainer container) {
		currentState.handleContainer(container);
	}
	
	private StepInterface constructNewStep(TutorialState state){
		switch(state){
			case start : return new StartState(playerName, this);
			
			case infoRace : return new InfoRaceState(playerName, this);
			case selectRace : return new SelectRaceState(playerName, this);
			
			case infoClass : return new InfoClassState(playerName, this);
			case selectClass : return new SelectClassState(playerName, this);
			
			case channels : return new ChannelsState(playerName, this);
			
			case end : return new EndState(playerName, this);
			
			default : return null;
		}
	}

	public boolean repostState() {
		return currentState.repostState();
	}

	public void activate() {
		activationSequence = false;
	}

	public boolean isActivationSequence() {
		return activationSequence;
	}

	public void setStateStep(int stateValue) {
		currentState.setStateStep(stateValue);
	}
}
