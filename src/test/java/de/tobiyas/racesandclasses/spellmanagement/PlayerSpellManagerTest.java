package de.tobiyas.racesandclasses.spellmanagement;

import org.junit.After;
import org.junit.Before;

import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;

public class PlayerSpellManagerTest {

	
	@Before
	public void init(){
		GenerateRaces.generateRaces();
	}
	
	
	@After
	public void tearDown(){
		GenerateRaces.dropMock();
	}
	
	
	public void rescanWorks(){
		//TODO
	}
}
