package de.tobiyas.racesandclasses.configuration.member;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class MemberConfigManagerTest {

	private MemberConfigManager sut;

	
	@BeforeClass
	public static void init(){
		GenerateRaces.generateRaces();
		GenerateBukkitServer.generateServer();
	}
	
	
	@Before
	public void before(){
		sut = new MemberConfigManager();
	}
	
	
	@AfterClass
	public static void teardown(){
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	
	@Ignore("Jenkis fails this") //TODO fixme
	@Test
	public void reload_creates_empty_file_if_not_exists(){
		assertTrue(RacesAndClasses.getPlugin().getDataFolder().list().length == 0);
		sut.reload();
		assertTrue(RacesAndClasses.getPlugin().getDataFolder().list().length == 1);
	}
	
	
	@Ignore("Jenkis fails this") //TODO fixme
	@Test
	public void reload_with_file_works(){
		sut.reload();
		assertTrue(RacesAndClasses.getPlugin().getDataFolder().list().length == 1);
		sut.reload();
		assertTrue(RacesAndClasses.getPlugin().getDataFolder().list().length == 1);
	}
	
	
	@Test
	public void creating_and_retrieving_config_works(){
		String testPlayer = "playerName";
		MemberConfig config = sut.getConfigOfPlayer(testPlayer);
		
		assertNotNull(config);
		
		assertEquals("Global", config.getCurrentChannel());
	}
	
	@Test
	public void saving_config_manager_works(){
		sut.getConfigOfPlayer("player1");
		sut.getConfigOfPlayer("player2");
		
		sut.saveConfigs();
		//TODO check if saving worked.
	}
}
