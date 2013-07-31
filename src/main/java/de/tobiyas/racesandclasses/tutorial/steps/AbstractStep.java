package de.tobiyas.racesandclasses.tutorial.steps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.tutorial.TutorialPath;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public abstract class AbstractStep implements StepInterface{
	
	protected String playerName;
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
		Player player = Bukkit.getPlayer(playerName);
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
