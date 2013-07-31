package de.tobiyas.racesandclasses.tutorial.steps;

import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public interface StepInterface {

	public boolean handleContainer(TutorialStepContainer container);

	public void postStartMessage();

	public void postDoneMessage();
	
	public TutorialState getState();
	
	public boolean hasFinished();

	public boolean repostState();

	public int getStateValue();

	public void setStateStep(int stateValue);
}
