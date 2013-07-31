package de.tobiyas.racesandclasses.tutorial.steps;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;

public abstract class AbstractMoreStepsStep extends AbstractStep{

	protected int currentStep;
	protected int maxSteps;
	
	protected String[] stepStartMessage;
	protected String[] stepEndMessage;
	
	
	@Override
	public boolean handleContainer(TutorialStepContainer container) {
		if(container.getState() != currentState)
			return false;
		
		int step = container.getStep();
		if(step == this.currentStep){
			stepDone();
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void stepDone(){
		postEndMessage(currentStep);
		currentStep ++;
		
		if(currentStep > maxSteps){
			finished = true;
			writeBack.skip();
			return;
		}
		
		postStartMessage(currentStep);
	}

	@Override
	public int getStateValue(){
		return currentStep;
	}
	

	protected void postStartMessage(int stage) {
		if(writeBack.isActivationSequence()) return;
		if(stepStartMessage == null || stepStartMessage.length == 0 || stepStartMessage.length < currentStep){
			sendMessageToPlayer(ChatColor.RED + "Error in Tutorial! Please skip this step. Something gone wrong.");
			return;
		}
		
		try{
			sendMessageToPlayer(stepStartMessage[currentStep - 1]);
		}catch(Exception e){
			sendMessageToPlayer(ChatColor.RED + "Error in Tutorial! Please skip this step. Something gone wrong.");
		}
	}


	protected void postEndMessage(int stage) {
		if(writeBack.isActivationSequence()) return;
		if(stepEndMessage == null || stepEndMessage.length == 0 || stepEndMessage.length < currentStep){
			sendMessageToPlayer(ChatColor.RED + "Error in Tutorial! Please skip this step. Something gone wrong.");
			return;
		}
		
		try{
			sendMessageToPlayer(stepEndMessage[currentStep - 1]);
		}catch(Exception e){
			sendMessageToPlayer(ChatColor.RED + "Error in Tutorial! Please skip this step. Something gone wrong.");
		}
	}
	
	@Override
	public void postStartMessage(){
		if(writeBack.isActivationSequence()) return;
		postStartMessage(currentStep);
	}
	
	@Override
	public void setStateStep(int stateValue){
		currentStep = stateValue;
	}
}
