package de.tobiyas.races.tests.tutorial;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.races.tests.generate.plugin.GenerateRaces;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;
import de.tobiyas.races.tutorial.TutorialManager;
import de.tobiyas.races.tutorial.TutorialStepContainer;
import de.tobiyas.races.util.tutorial.TutorialState;

public class TutorialTests {

	private static TutorialNotifier notifier;
	private static String player = "PlayerTutorial";
	
	@Before
	public void startUp(){
		GenerateBukkitServer.generateServer();
		GenerateBukkitServer.generatePlayerOnServer(player);
		
		GenerateRaces.generateRaces();
		
		TutorialManager.init();
		TutorialManager.enable();
		notifier = new TutorialNotifier();
	}
	
	@Test
	//Main Success Test
	public void mainSuccessStory(){
		testStart();
		
		testRace();
		
		testClass();
		
		testChannels();
		
		testEnd();
	}
	
	//tests Start
	private void testStart(){
		TutorialManager.start(player);
		
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.start);
		TutorialStepContainer currentContainer = TutorialManager.getCurrentState(player);
		validate(container, currentContainer);
		
		TutorialManager.skip(player);
	}
	
	//tests Race section
	private void testRace(){
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.infoRace);
		TutorialStepContainer currentContainer = TutorialManager.getCurrentState(player);
		validate(container, currentContainer);
		
		notifier.fireContainer(container);
		
		container = new TutorialStepContainer(player, TutorialState.selectRace);
		currentContainer = TutorialManager.getCurrentState(player);
		validate(container, currentContainer);
		
		notifier.fireContainer(container);
	}
	
	//tests Class section
	private void testClass(){
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.infoClass);
		TutorialStepContainer currentContainer = TutorialManager.getCurrentState(player);
		validate(container, currentContainer);
			
		notifier.fireContainer(container);
		
		container = new TutorialStepContainer(player, TutorialState.selectClass);
		currentContainer = TutorialManager.getCurrentState(player);
		validate(container, currentContainer);
			
		notifier.fireContainer(container);
	}
	
	//tests Channel section
	private void testChannels(){
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.channels);
		TutorialStepContainer currentContainer = TutorialManager.getCurrentState(player);
		validate(container, currentContainer);
				
		notifier.fireContainer(container);
				
		for(int i = 2; i <= 6; i++){
			container = new TutorialStepContainer(player, TutorialState.channels, i);
			currentContainer = TutorialManager.getCurrentState(player);
			validate(container, currentContainer);
					
			notifier.fireContainer(container);
		}
	}
	
	//tests End section
	private void testEnd(){
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.end);
		TutorialStepContainer currentContainer = TutorialManager.getCurrentState(player);
		validate(container, currentContainer);
		
		TutorialManager.stop(player);
		
		Assert.assertNull(TutorialManager.getCurrentState(player));
	}
	
	private void validate(TutorialStepContainer container1, TutorialStepContainer container2){
		Assert.assertEquals(container1.getName(), container2.getName());
		Assert.assertEquals(container1.getState(), container2.getState());
		Assert.assertEquals(container1.getStep(), container2.getStep());
	}
	
	@Test
	public void testSettingState(){
		testStart();
		TutorialManager.setState(player, "channels");
		testChannels();
	}
	
	@Test
	public void testTutorialMisc(){
		Assert.assertEquals(TutorialState.end.getStateName(), "End");
		Assert.assertTrue(TutorialState.end.isAccepted("stop"));
		Assert.assertFalse(TutorialState.end.isAccepted("start"));
		
		Assert.assertEquals(TutorialState.none, TutorialState.getState("NoneExisting"));
		Assert.assertTrue(new TutorialStepContainer(player, TutorialState.infoClass).equals(new TutorialStepContainer(player, TutorialState.infoClass)));
		Assert.assertFalse(new TutorialStepContainer(player, TutorialState.end).equals(new TutorialStepContainer(player, TutorialState.infoClass)));
		
	}
	
	
	@After
	public void tearDown(){
		TutorialManager.shutDown();
		notifier = null;
		
		GenerateBukkitServer.dropServer();
	}
}
