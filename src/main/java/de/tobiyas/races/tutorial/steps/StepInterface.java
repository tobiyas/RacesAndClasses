package de.tobiyas.races.tutorial.steps;

import de.tobiyas.races.tutorial.TutorialStepContainer;
import de.tobiyas.races.util.tutorial.TutorialState;

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
