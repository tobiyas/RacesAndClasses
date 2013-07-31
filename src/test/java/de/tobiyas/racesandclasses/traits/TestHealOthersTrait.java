package de.tobiyas.racesandclasses.traits;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class TestHealOthersTrait {

	@Before
	public void generateStuff(){
		GenerateBukkitServer.generateServer();
		
		GenerateBukkitServer.generatePlayerOnServer("PlayerHealer");
		GenerateBukkitServer.generatePlayerOnServer("PlayerHealed");
	}
	
	@Test
	public void mainSuccessTest(){
		
	}
	
	
	@After
	public void dropStuff(){
		GenerateBukkitServer.dropServer();
	}
}
