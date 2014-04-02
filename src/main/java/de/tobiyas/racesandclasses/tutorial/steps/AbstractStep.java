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
package de.tobiyas.racesandclasses.tutorial.steps;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.tutorial.TutorialPath;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public abstract class AbstractStep implements StepInterface{
	
	protected UUID playerUUID;
	protected TutorialPath writeBack;
	protected boolean finished;
	
	protected TutorialState currentState;
	
	protected String startMessage;
	protected String doneMessage;
	

	@Override
	public boolean handleContainer(TutorialStepContainer container) {
		if(container.getState() != currentState)
			return false;
		
		if(container.getStep() == 1){
			stepDone();
			return true;
		}
		
		return false;
	}
	
	protected void stepDone(){
		finished = true;
		writeBack.skip();
	}
	
	@Override
	public void postStartMessage(){
		if(writeBack.isActivationSequence()) return;
		sendMessageToPlayer(startMessage);
	}
	
	@Override
	public void postDoneMessage(){
		if(writeBack.isActivationSequence()) return;
		sendMessageToPlayer(doneMessage);
	}
	
	protected void sendMessageToPlayer(String message){
		Player player = Bukkit.getPlayer(playerUUID);
		if(player != null){
			if(!message.equalsIgnoreCase("-")){
				player.sendMessage(ChatColor.YELLOW + "------------------");
				player.sendMessage(message);
				player.sendMessage(ChatColor.YELLOW + "------------------");
			}
		}
	}

	@Override
	public TutorialState getState() {
		return currentState;
	}

	@Override
	public boolean hasFinished() {
		return finished;
	}

	@Override
	public boolean repostState() {
		postStartMessage();
		return true;
	}
	
	@Override
	public int getStateValue(){
		return 1;
	}
	
	@Override
	public void setStateStep(int stateValue){
	}

}
