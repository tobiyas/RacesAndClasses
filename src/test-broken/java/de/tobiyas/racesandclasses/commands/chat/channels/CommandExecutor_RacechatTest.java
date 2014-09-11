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
package de.tobiyas.racesandclasses.commands.chat.channels;

import static de.tobiyas.racesandclasses.translation.languages.Keys.plugin_pre;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.generate.PluginCommandFactory;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.generate.plugin.MockRaCPlugin;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CommandExecutor_RacechatTest {

	private CommandExecutor_Racechat sut;
	private Player player;
	private String playerName = "Banane";
	
	@Before
	public void setup(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		
		ConfigManager configManager = mock(ConfigManager.class, RETURNS_DEEP_STUBS);
		
		((MockRaCPlugin) RacesAndClasses.getPlugin() ).setConfigManager(configManager);
		
		
		sut = new CommandExecutor_Racechat();

		player = mock(Player.class);
		when(player.getName()).thenReturn(playerName);
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_channels_enable()).thenReturn(true);
		when(configManager.getGeneralConfig().isConfig_enableRaces()).thenReturn(true);
	}
	
	
	@After
	public void teardown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	@Test
	public void command_registration_works(){
		String commandName = "racechat";
		
		PluginCommand command = PluginCommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_Racechat();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
	
	@Test
	public void not_player_commandSenders_dont_have_access(){
		CommandSender sender = mock(CommandSender.class);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(plugin_pre + "only_players");
	}
	
	
	@Test
	public void channels_disabled_gives_error_message(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_channels_enable()).thenReturn(false);
		
		sut.onCommand(player, null, "", new String[]{});
		
		verify(player).sendMessage(plugin_pre + "something_disabled");
	}
	
	@Test
	public void no_race_selected_returns_error(){
		sut.onCommand(player, null, null, new String[]{"message"});
		
		verify(player).sendMessage(plugin_pre + "no_race_selected");
	}
	
	@Test
	public void sending_message_with_selected_race_works_and_empty_message_fails(){
		String raceName = "race";
		
		RaceContainer container = mock(RaceContainer.class);
		when(container.getName()).thenReturn(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(container);
		
		sut.onCommand(player, null, null, new String[]{});
		
		verify(player).sendMessage(plugin_pre + "send_empty_message");
	}
	
	@Test
	public void sending_message_with_selected_race_works(){
		String message = "hallo";
		String raceName = "race";
		
		RaceContainer container = mock(RaceContainer.class);
		when(container.getName()).thenReturn(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(container);
		
		sut.onCommand(player, null, null, new String[]{message});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).broadcastMessageToChannel(raceName, player, message + " ");
	}

	@Test
	public void sending_message_with_selected_race_with_more_args_works(){
		String message = "hallo test ";
		String raceName = "race";
		
		RaceContainer container = mock(RaceContainer.class);
		when(container.getName()).thenReturn(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(container);
		
		sut.onCommand(player, null, null, new String[]{"hallo", "test"});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).broadcastMessageToChannel(raceName, player, message);
	}
}
