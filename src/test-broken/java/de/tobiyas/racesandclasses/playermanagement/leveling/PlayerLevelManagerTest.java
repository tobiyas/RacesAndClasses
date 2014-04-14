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
package de.tobiyas.racesandclasses.playermanagement.leveling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import org.bukkit.Bukkit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.MemberConfigManager;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerReceiveEXPEvent;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.CustomPlayerLevelManager;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class PlayerLevelManagerTest {

	private String playerName = "player";
	private CustomPlayerLevelManager sut;
	
	@Before
	public void init(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		MemberConfig mockMemberConfig = mock(MemberConfig.class, Mockito.RETURNS_DEEP_STUBS);
		MemberConfigManager manager = mock(MemberConfigManager.class);
		
		when(manager.getConfigOfPlayer(playerName)).thenReturn(mockMemberConfig);
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager()).thenReturn(manager);
		
		
		sut = new CustomPlayerLevelManager(playerName);
	}
	
	
	@After
	public void tearDown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	
	@Test
	public void constructor_works(){
		assertEquals(1, sut.getCurrentLevel());
		assertEquals(0, sut.getCurrentExpOfLevel());
		assertEquals(playerName, sut.getPlayerUUID());
	}
	
	@Test
	public void setter_getter_work(){
		int level = 42;
		int levelExp = 1337;
		
		sut.setCurrentLevel(level);
		sut.setCurrentExpOfLevel(levelExp);
		
		assertEquals(level, sut.getCurrentLevel());
		assertEquals(levelExp, sut.getCurrentExpOfLevel());
		assertEquals(playerName, sut.getPlayerUUID());
	}
	
	
	@Test
	public void addEXP_adds_exp(){
		int expAdding = 42;
		
		assertTrue(sut.addExp(expAdding));
		
		assertEquals(1, sut.getCurrentLevel());
		assertEquals(expAdding, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager()).callEvent(any(PlayerReceiveEXPEvent.class));
	}
	
	@Test
	public void addEXP_returns_false_if_exp_is_negative (){
		assertFalse(sut.addExp(-42));
	}
	
	
	@Test
	public void addEXP_for_level_up_works(){
		int expAdding = 42;
		int expLevelUp = LevelCalculator.calculateLevelPackage(1).getMaxEXP() + expAdding;
		
		assertTrue(sut.addExp(expLevelUp));

		assertEquals(2, sut.getCurrentLevel());
		assertEquals(expAdding, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager(), times(2)).callEvent(any(LevelEvent.class));
	}
	
	@Test
	public void addEXP_for_multi_level_up_works(){
		int expAdding = 42;
		int expLevelUp = LevelCalculator.calculateLevelPackage(1).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(2).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(3).getMaxEXP() + expAdding;
		
		assertTrue(sut.addExp(expLevelUp));

		assertEquals(4, sut.getCurrentLevel());
		assertEquals(expAdding, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager(), times(4)).callEvent(any(LevelEvent.class));
	}
	
	
	@Test
	public void removeEXP_removes_exp(){
		int expAdding = 42;
		int half = expAdding / 2;
		
		assertTrue(sut.addExp(expAdding));
		assertTrue(sut.removeExp(half));
		
		assertEquals(1, sut.getCurrentLevel());
		assertEquals(half, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager(), times(2)).callEvent(any(LevelEvent.class));
	}
	
	
	@Test
	public void removeEXP_returns_false_if_exp_is_negative (){
		assertFalse(sut.removeExp(-42));
	}
	
	
	@Test
	public void removeEXP_for_level_down_works(){
		int expAdding = 42;
		int expLevelUp = LevelCalculator.calculateLevelPackage(1).getMaxEXP();
		
		assertTrue(sut.addExp(expLevelUp + expAdding));
		
		assertTrue(sut.removeExp(expLevelUp));

		assertEquals(1, sut.getCurrentLevel());
		assertEquals(expAdding, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager(), times(4)).callEvent(any(LevelEvent.class));
	}
	
	@Test
	public void removeEXP_for_multi_level_down_works(){
		int expAdding = 42;
		int expLevelUp = LevelCalculator.calculateLevelPackage(1).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(2).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(3).getMaxEXP();
		
		assertTrue(sut.addExp(expLevelUp + expAdding));
		assertTrue(sut.removeExp(expLevelUp));
		

		assertEquals(1, sut.getCurrentLevel());
		assertEquals(expAdding, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager(), times(8)).callEvent(any(LevelEvent.class));
	}

	
	@Test
	public void removeEXP_sets_to_level1_with_0_exp_when_too_much_lost(){
		int expAdding = 42;
		int expLevelUp = LevelCalculator.calculateLevelPackage(1).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(2).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(3).getMaxEXP();
		
		assertTrue(sut.addExp(expLevelUp + expAdding));
		assertTrue(sut.removeExp(expLevelUp * 2));
		

		assertEquals(1, sut.getCurrentLevel());
		assertEquals(0, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager(), times(8)).callEvent(any(LevelEvent.class));
	}

	
	@Test
	public void saving_and_loading_works(){
		int expAdding = 42;
		int expLevelUp = LevelCalculator.calculateLevelPackage(1).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(2).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(3).getMaxEXP();
		
		sut.addExp(expLevelUp + expAdding);
		sut.save();
		
		sut = new CustomPlayerLevelManager(playerName);
		sut.reloadFromYaml();
		
		assertEquals(expAdding, sut.getCurrentExpOfLevel());
		assertEquals(4, sut.getCurrentLevel());
	}
	
	@Test
	public void checkLevelChanged_on_addition_works(){
		int expAdding = 42;
		int expLevelUp = LevelCalculator.calculateLevelPackage(1).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(2).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(3).getMaxEXP();
		
		sut.setCurrentExpOfLevel(expLevelUp + expAdding);
		sut.checkLevelChanged();
		
		assertEquals(4, sut.getCurrentLevel());
		assertEquals(expAdding, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager(), times(3)).callEvent(any(LevelEvent.class));
	}
	
	@Test
	public void checkLevelChanged_on_remove_works(){
		int expAdding = 42;
		int expLevelUp = LevelCalculator.calculateLevelPackage(1).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(2).getMaxEXP() +
				LevelCalculator.calculateLevelPackage(3).getMaxEXP();
		
		sut.setCurrentExpOfLevel(-expLevelUp + expAdding);
		sut.setCurrentLevel(4);
		
		sut.checkLevelChanged();
		
		assertEquals(1, sut.getCurrentLevel());
		assertEquals(expAdding, sut.getCurrentExpOfLevel());
		
		verify(Bukkit.getPluginManager(), times(3)).callEvent(any(LevelEvent.class));
	}
}
