package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class AbstractTraitHolderTest {
	
	
	@Before
	public void init(){
		GenerateRaces.generateRaces();
		GenerateBukkitServer.generateServer();
	}
	
	
	@After
	public void teardown(){
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	@Test
	public void constructor_sets_values_correct(){
		//TODO test stuff!
	}
	
}
