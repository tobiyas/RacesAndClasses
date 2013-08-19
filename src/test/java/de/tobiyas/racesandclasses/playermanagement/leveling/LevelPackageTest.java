package de.tobiyas.racesandclasses.playermanagement.leveling;

import org.junit.Test;

import static org.junit.Assert.*;

public class LevelPackageTest {

	@Test
	public void constructor_works(){
		int level = 42;
		int maxExp = 1337;
		
		LevelPackage sut = new LevelPackage(level, maxExp);
		
		assertEquals(level, sut.getLevel());
		assertEquals(maxExp, sut.getMaxEXP());
	}
	
	@Test
	public void constructor_setting_negative_values_sets_to_1(){
		int level = -42;
		int maxExp = -1337;
		
		LevelPackage sut = new LevelPackage(level, maxExp);
		
		assertEquals(1, sut.getLevel());
		assertEquals(1, sut.getMaxEXP());
	}
}
