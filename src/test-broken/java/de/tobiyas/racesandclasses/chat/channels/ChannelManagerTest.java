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
package de.tobiyas.racesandclasses.chat.channels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelTicker;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ChannelTicker.class})
public class ChannelManagerTest {

	private ChannelManager sut;
	private YAMLConfigExtended config;

	@Before
	public void init() throws IOException{
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		config = new YAMLConfigExtended(File.createTempFile("channel", ".yml"));
		
		sut = new ChannelManager(config);
		//when(RacesAndClasses.getPlugin().getChannelManager()).thenReturn(sut);
		
		PowerMockito.mockStatic(ChannelTicker.class);
		
		when(Bukkit.getWorlds()).thenReturn(new LinkedList<World>());
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(new LinkedList<String>());
		when(Bukkit.getOnlinePlayers()).thenReturn(new Player[]{});
	}
	
	
	@After
	public void tearDown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	
	@Test
	public void constructor_works(){
		sut.init();
		
		assertEquals(ChannelLevel.GlobalChannel, sut.getChannelLevel("Global"));
		assertEquals(ChannelLevel.LocalChannel, sut.getChannelLevel("Local"));
		assertEquals(ChannelLevel.GlobalChannel, sut.getChannelLevel("Tutorial"));
	}
	
	@Test
	public void init_with_races_and_worlds_work(){
		List<World> worlds = new LinkedList<World>();
		
		World world1 = mock(World.class);
		when(world1.getName()).thenReturn("World1");
		worlds.add(world1);
		
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("World2");
		worlds.add(world2);
		
		World world3 = mock(World.class);
		when(world3.getName()).thenReturn("World3");
		worlds.add(world3);
		
		
		
		List<String> races = new LinkedList<String>();
		races.add("race1");
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName("race1")).thenReturn(mock(RaceContainer.class));

		races.add("race2");
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName("race2")).thenReturn(mock(RaceContainer.class));
		
		races.add("race3");
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName("race3")).thenReturn(mock(RaceContainer.class));
		
		
		when(Bukkit.getWorlds()).thenReturn(worlds);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(races);
		
		sut.init();
		
		assertEquals(ChannelLevel.RaceChannel, sut.getChannelLevel("race1"));
		assertEquals(ChannelLevel.RaceChannel, sut.getChannelLevel("race2"));
		assertEquals(ChannelLevel.RaceChannel, sut.getChannelLevel("race3"));

		assertEquals(ChannelLevel.WorldChannel, sut.getChannelLevel("World1"));
		assertEquals(ChannelLevel.WorldChannel, sut.getChannelLevel("World2"));
		assertEquals(ChannelLevel.WorldChannel, sut.getChannelLevel("World3"));
	}
	
	
	@Test
	public void registering_a_new_channel_works(){
		String privateChannelName = "channelName";
		ChannelLevel level = ChannelLevel.PrivateChannel;
		
		sut.registerChannel(level, privateChannelName);
		
		assertEquals(level, sut.getChannelLevel(privateChannelName));
	}
	
	@Test
	public void get_channel_level_with_null_returns_NONE(){
		assertEquals(ChannelLevel.NONE, sut.getChannelLevel(null));
	}
	
	@Test
	public void get_channel_level_channel_not_found_returns_NONE(){
		assertEquals(ChannelLevel.NONE, sut.getChannelLevel("invalid"));
	}
	
	
	@Test
	public void adding_player_to_channel_works(){
		sut.init();
		
		String playerName = "player";
		String channelName = "Global";
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		
		assertEquals(ChannelLevel.GlobalChannel, sut.getChannelLevel(channelName));
		sut.joinChannel(Bukkit.getPlayer(playerName), channelName, "", false);
		
		assertTrue(sut.isMember(playerName, channelName));
	}
	
	@Test
	public void listing_all_channels_works(){
		sut.init();
		
		List<String> channelNames = sut.listAllChannels();
		assertEquals(3, channelNames.size());
		
		assertTrue(channelNames.contains("Global"));
		assertTrue(channelNames.contains("Local"));
		assertTrue(channelNames.contains("Tutorial"));
	}
	
	@Test
	public void listing_all_public_channels_only_lists_public_channels(){
		String privateChannelName = "privateChannel";
		
		sut.init();
		assertNotNull(sut.registerChannel(ChannelLevel.PrivateChannel, privateChannelName));
		
		assertEquals(ChannelLevel.PrivateChannel, sut.getChannelLevel(privateChannelName));
		
		List<String> channelNames = sut.listAllPublicChannels();
		assertEquals(3, channelNames.size());
		
		assertTrue(channelNames.contains("Global"));
		assertTrue(channelNames.contains("Local"));
		assertTrue(channelNames.contains("Tutorial"));
		
		assertFalse(channelNames.contains(privateChannelName));
	}
	
	@Test
	public void adding_channel_with_player_sends_message_to_player_and_adds_him(){
		String privateChannelName = "privateChannel";
		String playerName = "player";
		
		when(RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_PlayerJoinFormat()).thenReturn("{color}[{nick}] Player: &f[{sender}] {color}has joined the Channel.");
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		
		sut.init();
		assertTrue(sut.registerChannel(ChannelLevel.PrivateChannel, privateChannelName, Bukkit.getPlayer(playerName)));
		
		assertEquals(ChannelLevel.PrivateChannel, sut.getChannelLevel(privateChannelName));
		assertTrue(sut.isMember(playerName, privateChannelName));
		
		verify(Bukkit.getPlayer(playerName)).sendMessage("[privateChannel] Player: §f[player] has joined the Channel.");
	}
	
	@Test
	public void registering_already_registered_channel_fails(){
		String privateChannelName = "Global";
		String playerName = "player";
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		
		sut.init();
		assertFalse(sut.registerChannel(ChannelLevel.PrivateChannel, privateChannelName, Bukkit.getPlayer(playerName)));
		
		assertFalse(sut.isMember(playerName, privateChannelName));
		
		verify(Bukkit.getPlayer(playerName)).sendMessage(ChatColor.RED + "Channel: " + ChatColor.AQUA + privateChannelName + ChatColor.RED + " already exists.");
	}
	
	@Test
	public void creating_channel_with_password_works(){
		String privateChannelName = "privateChannel";
		String playerName = "player";
		
		when(RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_PlayerJoinFormat()).thenReturn("{color}[{nick}] Player: &f[{sender}] {color}has joined the Channel.");
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		
		sut.init();
		assertTrue(sut.registerChannel(ChannelLevel.PasswordChannel, privateChannelName, "password", Bukkit.getPlayer(playerName)));
		
		assertEquals(ChannelLevel.PasswordChannel, sut.getChannelLevel(privateChannelName));
		assertTrue(sut.isMember(playerName, privateChannelName));
		
		
		verify(Bukkit.getPlayer(playerName)).sendMessage(ChatColor.GREEN + "The channel " + ChatColor.AQUA + privateChannelName + 
								ChatColor.GREEN + " has been created successfully");
	}
	
	@Test
	public void post_channel_info_fails_on_non_existent_channel(){
		Player player = mock(Player.class);
		
		sut.postChannelInfo(player, "invalid");
		
		verify(player).sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + "invalid" + ChatColor.RED + " could not be found.");
	}
	
	@Test
	public void post_channel_info_works(){
		String playerName = "player";
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		Player player = Bukkit.getPlayer(playerName);
		
		sut.init();
		sut.postChannelInfo(player, "Global");
		
		
		String otherPlayer = "otherPlayer";
		GenerateBukkitServer.generateOfflinePlayerOnServer(otherPlayer);

		sut.joinChannel(player, "Global", "", false);
		sut.joinChannel(Bukkit.getPlayer(otherPlayer), "Global", "", false);
		
		sut.postChannelInfo(player, "Global");

		verify(player, times(2)).sendMessage(ChatColor.YELLOW + "ChannelName: " + ChatColor.AQUA + "Global");
		verify(player, times(2)).sendMessage(ChatColor.YELLOW + "ChannelLevel: " + ChatColor.AQUA + ChannelLevel.GlobalChannel.name());
		verify(player, times(2)).sendMessage(ChatColor.YELLOW + "ChannelColor: " + "COLOR");
		verify(player, times(2)).sendMessage(ChatColor.YELLOW + "ChannelFormat: " + ChatColor.RESET);
		verify(player, times(2)).sendMessage(ChatColor.YELLOW + "Has Password: " + ChatColor.AQUA + false);
		
		verify(player, times(2)).sendMessage(ChatColor.YELLOW + "===== Channel Members: =====");
		verify(player).sendMessage(ChatColor.RED + "This channel has currently no Members.");
		
		verify(player).sendMessage(ChatColor.GREEN + playerName + ", " + ChatColor.RED + otherPlayer + " (offline)");
	}
	
	@Test
	public void broadcast_message_to_channel_works(){
		String playerName = "player";
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		Player player = Bukkit.getPlayer(playerName);
		
		when(RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_globalchat_default_color()).thenReturn(ChatColor.GRAY.toString());
		when(RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_globalchat_default_format()).thenReturn("{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		
		sut.init();
		sut.joinChannel(player, "Global", "", false);
		
		sut.broadcastMessageToChannel("Global", player, "test");
		
		verify(player).sendMessage("§7[Global] §f§f[player§f]§7: test");
	}
	
	@Test
	public void broadcasting_message_to_channel_when_not_found_fails(){
		Player player = mock(Player.class);
		
		sut.broadcastMessageToChannel("invalid", player, "something");
		
		verify(player).sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + "invalid" + ChatColor.RED + " was not found.");
	}
	
	@Test
	public void leaving_channel_works(){
		String playerName = "player";
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		Player player = Bukkit.getPlayer(playerName);
		
		sut.init();
		sut.joinChannel(player, "Global", "", false);
		assertTrue(sut.isMember(playerName, "Global"));
		
		sut.leaveChannel(player, "Global", false);
		assertFalse(sut.isMember(playerName, "Global"));
	}

	
	@Test
	public void leaving_channel_fails_if_not_member(){
		String playerName = "player";
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		Player player = Bukkit.getPlayer(playerName);
		
		sut.init();
		assertFalse(sut.isMember(playerName, "invalid"));
		
		sut.leaveChannel(player, "invalid", true);
		sut.leaveChannel(player, "invalid", false);

		assertFalse(sut.isMember(playerName, "invalid"));
		
		verify(player).sendMessage(ChatColor.RED + "The Channel: " + "invalid" + " does not exist.");
	}
	
	
	@Test
	public void player_join_server_and_leave_server_lets_him_join_and_leave_all_channels(){
		String playerName = "player";
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		Player player = Bukkit.getPlayer(playerName);
		
		List<World> worlds = new LinkedList<World>();
		
		World world1 = mock(World.class);
		when(world1.getName()).thenReturn("World1");
		worlds.add(world1);
		when(player.getWorld()).thenReturn(world1);
		
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("World2");
		worlds.add(world2);
		
		World world3 = mock(World.class);
		when(world3.getName()).thenReturn("World3");
		worlds.add(world3);
		
		
		
		List<String> races = new LinkedList<String>();
		races.add("race1");
		RaceContainer race = mock(RaceContainer.class);
		when(race.getName()).thenReturn("race1");
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName("race1")).thenReturn(race);

		races.add("race2");
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName("race2")).thenReturn(mock(RaceContainer.class));
		
		races.add("race3");
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderByName("race3")).thenReturn(mock(RaceContainer.class));
		
		
		when(Bukkit.getWorlds()).thenReturn(worlds);
		when(RacesAndClasses.getPlugin().getRaceManager().listAllVisibleHolders()).thenReturn(races);
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(race);
		
		sut.init();
		
		sut.playerLogin(player);
		
		assertTrue(sut.isMember(playerName, "Global"));
		assertTrue(sut.isMember(playerName, "race1"));
		assertTrue(sut.isMember(playerName, "World1"));
		
		sut.playerQuit(player);
		
		assertFalse(sut.isMember(playerName, "Global"));
		assertFalse(sut.isMember(playerName, "race1"));
		assertFalse(sut.isMember(playerName, "World1"));
	}
	
	
	@Test
	public void remove_channel_works(){
		String channelName = "42";
		
		sut.init();
		
		sut.registerChannel(ChannelLevel.PrivateChannel, channelName);
		assertEquals(ChannelLevel.PrivateChannel, sut.getChannelLevel(channelName));
		
		
		sut.removeChannel(channelName);
		assertEquals(ChannelLevel.NONE, sut.getChannelLevel(channelName));		
	}
	
	@Test
	public void player_change_world_changes_worls_channel(){
		String playerName = "player";
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		Player player = Bukkit.getPlayer(playerName);
		
		List<World> worlds = new LinkedList<World>();
		
		World world1 = mock(World.class);
		when(world1.getName()).thenReturn("World1");
		worlds.add(world1);
		when(player.getWorld()).thenReturn(world1);
		
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("World2");
		worlds.add(world2);
		
		World world3 = mock(World.class);
		when(world3.getName()).thenReturn("World3");
		worlds.add(world3);
		
		when(Bukkit.getWorlds()).thenReturn(worlds);
		
		sut.init();
		sut.joinChannel(player, "World2", "", false);
		
		assertTrue(sut.isMember(playerName, "World2"));
		sut.playerChangedWorld(world2, player);
		
		assertFalse(sut.isMember(playerName, "World2"));
		assertTrue(sut.isMember(playerName, "World1"));
	}
}
