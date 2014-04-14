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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.generate.plugin.MockRaCPlugin;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class TutorialTests {

	private static TutorialNotifier notifier;
	private static String player = "PlayerTutorial";
	
	@Before
	public void startUp(){
		GenerateBukkitServer.generateServer();
		GenerateBukkitServer.generatePlayerOnServer(player);
		
		GenerateRaces.generateRaces();
		
		TutorialManager tutorialManager = new TutorialManager();
		MockRaCPlugin plugin = (MockRaCPlugin) RacesAndClasses.getPlugin();
		plugin.setTutorialManager(tutorialManager);
		
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
		RacesAndClasses.getPlugin().getTutorialManager().start(player);
		
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.start);
		TutorialStepContainer currentContainer = RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player);
		validate(container, currentContainer);
		
		RacesAndClasses.getPlugin().getTutorialManager().skip(player);
	}
	
	//tests Race section
	private void testRace(){
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.infoRace);
		TutorialStepContainer currentContainer = RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player);
		validate(container, currentContainer);
		
		notifier.fireContainer(container);
		
		container = new TutorialStepContainer(player, TutorialState.selectRace);
		currentContainer = RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player);
		validate(container, currentContainer);
		
		notifier.fireContainer(container);
	}
	
	//tests Class section
	private void testClass(){
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.infoClass);
		TutorialStepContainer currentContainer = RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player);
		validate(container, currentContainer);
			
		notifier.fireContainer(container);
		
		container = new TutorialStepContainer(player, TutorialState.selectClass);
		currentContainer = RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player);
		validate(container, currentContainer);
			
		notifier.fireContainer(container);
	}
	
	//tests Channel section
	private void testChannels(){
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.channels);
		TutorialStepContainer currentContainer = RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player);
		validate(container, currentContainer);
				
		notifier.fireContainer(container);
				
		for(int i = 2; i <= 6; i++){
			container = new TutorialStepContainer(player, TutorialState.channels, i);
			currentContainer = RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player);
			validate(container, currentContainer);
					
			notifier.fireContainer(container);
		}
	}
	
	//tests End section
	private void testEnd(){
		TutorialStepContainer container = new TutorialStepContainer(player, TutorialState.end);
		TutorialStepContainer currentContainer = RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player);
		validate(container, currentContainer);
		
		RacesAndClasses.getPlugin().getTutorialManager().stop(player);
		
		Assert.assertNull(RacesAndClasses.getPlugin().getTutorialManager().getCurrentState(player));
	}
	
	private void validate(TutorialStepContainer container1, TutorialStepContainer container2){
		Assert.assertEquals(container1.getName(), container2.getName());
		Assert.assertEquals(container1.getState(), container2.getState());
		Assert.assertEquals(container1.getStep(), container2.getStep());
	}
	
	@Test
	public void testSettingState(){
		testStart();
		RacesAndClasses.getPlugin().getTutorialManager().setState(player, "channels");
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
		//RacesAndClasses.getPlugin().getTutorialManager().shutDown();
		notifier = null;
		
		GenerateBukkitServer.dropServer();
	}
}
