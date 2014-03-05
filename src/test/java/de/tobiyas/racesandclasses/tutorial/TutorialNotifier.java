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

import java.util.Observable;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class TutorialNotifier extends Observable{


	public TutorialNotifier(){
		RacesAndClasses.getPlugin().getTutorialManager().registerObserver(this);
		this.setChanged();
	}
	
	
	public void generateContainer(String playerName, TutorialState state){
		TutorialStepContainer container = new TutorialStepContainer(playerName, state);
		fireContainer(container);
	}
	
	public void generateContainer(String playerName, TutorialState state, int subState){
		TutorialStepContainer container = new TutorialStepContainer(playerName, state, subState);
		fireContainer(container);
	}


	public void fireContainer(TutorialStepContainer container) {
		this.notifyObservers(container);
		this.setChanged();
	}
}
