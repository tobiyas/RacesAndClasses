package de.tobiyas.racesandclasses.playermanagement.leveling;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LevelCalculatorTest {

	@Before
	public void init(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
	}

	@After
	public void tearDown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	
	@Test
	public void verifyGeneratorStringWorks_works(){
		String validGeneratorString = "({level} * {level}) * 1000";
		String invalidGeneratorString = "({level} * x * invalid * Banane / {level}) * 1000 )";
		
		assertTrue(LevelCalculator.verifyGeneratorStringWorks(validGeneratorString));
		assertFalse(LevelCalculator.verifyGeneratorStringWorks(invalidGeneratorString));
	}
	
	@Test
	public void calcMaxExpForLevel_works(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
			.thenReturn("({level} * {level} * {level}) * 1000");
		
		int level = 10;
		int expNeededForLevel10 = level * level * level * 1000;
		
		int actualValue = LevelCalculator.calcMaxExpForLevel(level);
		
		assertEquals(expNeededForLevel10, actualValue);
	}
	
	@Test
	public void calcMaxExpForLevel_bypasses_to_working_calculation_when_string_is_broken(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
			.thenReturn("Banane Banane, Affe, Affe, Banane!!! ?");
		
		int level = 10;
		int expNeededForLevel10 = level * level * 1000;
		
		int actualValue = LevelCalculator.calcMaxExpForLevel(level);
		
		assertEquals(expNeededForLevel10, actualValue);
	}
	
	@Test
	public void calculateLevelPackage_works_for_positive_levels(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
		.thenReturn("({level}) * 1000");
		
		int level = 10;
		int expNeededForLevel10 = level * 1000;
		
		assertEquals(new LevelPackage(level, expNeededForLevel10), LevelCalculator.calculateLevelPackage(level));
	}
	
	@Test
	public void calculateLevelPackage_passes_level1_package_for_negative_levels(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
		.thenReturn("({level}) * 1000");
		
		int level = -10;
		int expectedLevel = 1;
		int expNeededForLevel10 = expectedLevel * 1000;
		
		assertEquals(new LevelPackage(expectedLevel, expNeededForLevel10), LevelCalculator.calculateLevelPackage(level));
	}
	
	@Test
	public void calculatePercentageOfLevel_works(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString())
		.thenReturn("{level} * {level} * 42 * 1000");
		
		double expectedPecentage = 0.421337;
		
		int level = 98621;
		int currentEXP = (int) (LevelCalculator.calculateLevelPackage(level).getMaxEXP() * expectedPecentage);
		
		double actualPercentage = LevelCalculator.calculatePercentageOfLevel(level, currentEXP);
		
		assertEquals(100 * expectedPecentage, actualPercentage, 0.0001);
	}

}
