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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public abstract class AbstractChannelContainerTest {

	protected ChannelContainer sut;
	
	protected String neededPW = "";
	
	protected String channelName = "test";
	
	protected String addingPlayer = "addMe";
	protected String notAddedPlayer = "meNoWant";
	
	protected String offlinePlayer = "offline";
	
	@Before
	public void generateTestEnvironmente() throws ChannelInvalidException{
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		GenerateBukkitServer.generatePlayerOnServer(addingPlayer);
		GenerateBukkitServer.generatePlayerOnServer(notAddedPlayer);
		
		
		sut = generateSut();
	}
	
	protected abstract ChannelContainer generateSut() throws ChannelInvalidException;
	
	@After
	public void tearDown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	
	@Test
	public void add_Player_to_container_adds_player(){
		sut.addPlayerToChannel(addingPlayer, neededPW, false);		
		assertTrue(sut.isMember(addingPlayer));
	}
	
	
	@Test
	public void removing_player_removes_player(){
		sut.addPlayerToChannel(addingPlayer, neededPW, false);		
		assertTrue(sut.isMember(addingPlayer));
		
		sut.removePlayerFromChannel(addingPlayer, false);
		assertFalse(sut.isMember(addingPlayer));
	}
	
	@Test
	public void adding_already_added_player_returns_false(){
		sut.addPlayerToChannel(addingPlayer, neededPW, false);
		
		assertTrue(sut.isMember(addingPlayer));
		sut.addPlayerToChannel(addingPlayer, neededPW, false);
		
		Player playerMock = Bukkit.getPlayer(addingPlayer);
		verify(playerMock, times(1)).sendMessage(anyString());
	}
	
	@Test
	public void banned_player_can_not_join(){
		sut.addPlayerToChannel(addingPlayer, neededPW, false);
		
		sut.banAndRemovePlayer(addingPlayer, 1337);
		assertTrue(sut.isBanned(addingPlayer));
		assertFalse(sut.isMember(addingPlayer));
		
		sut.addPlayerToChannel(addingPlayer, neededPW, false);
		assertFalse(sut.isMember(addingPlayer));
		
		Player mockPlayer = Bukkit.getPlayer(addingPlayer);
		verify(mockPlayer, times(1)).sendMessage(anyString());
		
		sut.unbanPlayer(addingPlayer);
		assertFalse(sut.isBanned(addingPlayer));
		
		sut.addPlayerToChannel(addingPlayer, neededPW, false);
		assertTrue(sut.isMember(addingPlayer));
	}
	
	@Test
	public void muting_player_mutes_player(){
		sut.addPlayerToChannel(addingPlayer, neededPW, false);
		
		sut.mutePlayer(addingPlayer, 1337);
		assertTrue(sut.isMuted(addingPlayer));
		
		sut.unmutePlayer(addingPlayer);
		assertFalse(sut.isMuted(addingPlayer));
	}
	
	@Test
	public void setting_channelAdmin_works(){
		assertEquals("", sut.getAdmin());
		sut.addPlayerToChannel(addingPlayer, neededPW, false);
		
		sut.setAdmin(addingPlayer);
		assertEquals(addingPlayer, sut.getAdmin());
	}
	
	@Test
	//This test needs to be overriden for Password related tests
	public void setting_password_works(){
		String password = "123";
		sut.setPassword(password);
		
		//This needs to be different for Password related Tests
		assertEquals(neededPW, sut.getPassword());
	}
	
	
	@Test
	public void saving_channel_works() throws IOException{
		File tempFile = File.createTempFile("channelContainerTemp", ".yml");
		try{
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile).load();
			
			if(!config.getValidLoad()){
				fail("Config could not be loaded correct.");
			}
			
			sut.saveChannel(config);
			
			String channelPre = "channel." + sut.getChannelLevel().name() + "." + sut.getChannelName();
			
			assertTrue(config.isConfigurationSection(channelPre));
			assertTrue(config.isConfigurationSection(channelPre + ".banned")); //TODO check why this fails
			assertTrue(config.isConfigurationSection(channelPre + ".muted")); //TODO check why this fails
			
			assertEquals("§f[", config.getString(channelPre + ".prefix"));
			assertEquals("§f]", config.getString(channelPre + ".suffix"));
			assertEquals("", config.getString(channelPre + ".channelColor"));
			assertEquals("[]", config.getString(channelPre + ".members"));
			assertEquals("", config.getString(channelPre + ".channelFormat"));
			assertEquals(neededPW, config.getString(channelPre + ".channelPassword"));
			assertEquals("", config.getString(channelPre + ".channelAdmin"));
			
			assertTrue(config.getBoolean(channelPre + ".saveLoad"));
						
		}finally{
			tempFile.delete();
		}
	}
	
	@Test
	public void editing_channel_works() throws IOException{
		sut.setAdmin(addingPlayer);
		
		Player player = Bukkit.getPlayer(addingPlayer);
		sut.editChannel(player, "format", "123");
		sut.editChannel(player, "color", "123");
		sut.editChannel(player, "prefix", "123");
		sut.editChannel(player, "suffix", "123");
		sut.editChannel(player, "password", neededPW);
		sut.editChannel(player, "format", "123");
		
		sut.editChannel(player, "invalied", "123");
		
		//easiest way to check the stats
		File tempFile = File.createTempFile("channelContainerTemp", ".yml");
		try{
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile).load();
			
			if(!config.getValidLoad()){
				fail("Config could not be loaded correct.");
			}
			
			sut.saveChannel(config);
			
			String channelPre = "channel." + sut.getChannelLevel().name() + "." + sut.getChannelName();
			
			assertTrue(config.isConfigurationSection(channelPre));
			assertTrue(config.isConfigurationSection(channelPre + ".banned")); //TODO check why this fails
			assertTrue(config.isConfigurationSection(channelPre + ".muted")); //TODO check why this fails
			
			assertEquals("123", config.getString(channelPre + ".prefix"));
			assertEquals("123", config.getString(channelPre + ".suffix"));
			assertEquals("123", config.getString(channelPre + ".channelColor"));
			assertEquals("[addMe]", config.getString(channelPre + ".members"));
			assertEquals("123", config.getString(channelPre + ".channelFormat"));
			assertEquals("addMe", config.getString(channelPre + ".channelAdmin"));
			
			assertTrue(config.getBoolean(channelPre + ".saveLoad"));
		}finally{
			tempFile.delete();
		}
	}
	
	@Test
	public void test_player_post_info(){
		sut.setAdmin(addingPlayer);
		
		Player player = Bukkit.getPlayer(addingPlayer);
		sut.postInfo(player);
		
		verify(player, times(8)).sendMessage(anyString());
	}
	
	
	@Test
	public void loading_from_yaml_file_works() throws IOException, ChannelInvalidException{
		//easiest way to check the stats
		File tempFile = File.createTempFile("channelContainerTemp", ".yml");
		try{
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile).load();
			
			if(!config.getValidLoad()){
				fail("Config could not be loaded correct.");
			}
			
			String channelName = "test123";
			ChannelLevel level = sut.getChannelLevel();
			String channelPre = "channel." + level + "." + channelName;
			
			config.createSection(channelPre);
			config.createSection(channelPre + ".banned");
			config.createSection(channelPre + ".muted");
			
			config.set(channelPre + ".prefix", "123");
			config.set(channelPre + ".suffix", "123");
			config.set(channelPre + ".channelColor", "123");
			config.set(channelPre + ".members", "[addMe]");
			config.set(channelPre + ".channelFormat", "123");
			config.set(channelPre + ".channelPassword", neededPW);
			config.set(channelPre + ".channelAdmin", "addMe");
			
			config.set(channelPre + ".saveLoad", true);
			config.save();
			
			sut = ChannelContainer.constructFromYml(config, channelName, level);
			
			//Just test some fields
			assertEquals(addingPlayer, sut.getAdmin());
			assertEquals(neededPW, sut.getPassword());
			
		}finally{
			tempFile.delete();
		}
	}
	
	
}
