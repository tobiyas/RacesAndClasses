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
package de.tobiyas.racesandclasses.chat.channels.container;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.util.config.YAMLConfigExtended;

public class BanContainerTest {

	private BanContainer sut;
	private int stdBanTime = 1337;
	
	private String bannedPlayer = "banned";
	private String notBannedPlayer = "notBanned";
	
	
	@Before
	public void before(){
		sut = new BanContainer();
	}
	
	@After
	public void after(){
		sut = null;
	}
	
	
	@Test
	public void adding_player_adds_player_and_sets_time_correct(){
		sut.banPlayer(bannedPlayer, stdBanTime);
		assertEquals(stdBanTime, sut.isBanned(bannedPlayer));
	}
	
	@Test
	public void adding_player_twice_gives_error(){
		assertTrue(sut.banPlayer(bannedPlayer, stdBanTime));
		assertEquals(stdBanTime, sut.isBanned(bannedPlayer));

		assertFalse(sut.banPlayer(bannedPlayer, stdBanTime));
		assertEquals(stdBanTime, sut.isBanned(bannedPlayer));
	}
	
	@Test
	public void removing_existing_player_works(){
		sut.banPlayer(bannedPlayer, stdBanTime);
		
		assertTrue(sut.unbanPlayer(bannedPlayer));
		assertEquals(-1, sut.isBanned(bannedPlayer));
	}
	
	@Test
	public void removing_non_existent_player_fails(){
		assertFalse(sut.unbanPlayer(notBannedPlayer));
	}
	
	@Test
	public void ticking_container_reduces_time_banned(){
		sut.banPlayer(bannedPlayer, stdBanTime);
		
		sut.tick();
		
		int expectedTimeRemaining = stdBanTime - 1; 
		assertEquals(expectedTimeRemaining, sut.isBanned(bannedPlayer));
	}
	
	@Test
	public void ticking_out_removes_player_from_list(){
		sut.banPlayer(bannedPlayer, 2);
		for(int i = 0; i < 5; i++){
			//just do some ticking
			sut.tick();
		}
		
		assertEquals(-1, sut.isBanned(bannedPlayer));
	}
	
	@Test
	public void ticking_unlimited_ban_does_not_change_time(){
		sut.banPlayer(bannedPlayer, Integer.MAX_VALUE);
		for(int i = 0; i < 500; i++){
			//just do some ticking
			sut.tick();
		}
		
		assertEquals(Integer.MAX_VALUE, sut.isBanned(bannedPlayer));
	}
	
	@Test
	public void loading_from_YAML_works() throws IOException{
		File tempFile = File.createTempFile("banContainerTest", ".yml");
		String channelPre = "temp";
		
		try{
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile).load();
			if(!config.getValidLoad()){
				fail("banContainerTest file could not be created.");
			}
			
			config.createSection(channelPre + ".banned");
			config.set(channelPre + ".banned." + bannedPlayer, stdBanTime);
			
			sut = new BanContainer(config, channelPre);
			
			assertEquals(stdBanTime, sut.isBanned(bannedPlayer));
		}finally{
			tempFile.delete();
		}
	}
	
	
	@Test
	public void saving_to_YAML_works() throws IOException{
		File tempFile = File.createTempFile("banContainerTest", ".yml");
		String channelPre = "temp";
		
		try{
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile).load();
			if(!config.getValidLoad()){
				fail("banContainerTest file could not be created.");
			}
			
			config.createSection(channelPre);
			
			sut.banPlayer(bannedPlayer, stdBanTime);
			sut.saveContainer(config, channelPre);
			
			assertTrue(config.contains(channelPre + ".banned." + bannedPlayer));
			assertEquals(stdBanTime, config.getInt(channelPre + ".banned." + bannedPlayer));
		}finally{
			tempFile.delete();
		}
	}
	
}
