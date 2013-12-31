package de.tobiyas.racesandclasses.commands.chat.channels;

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
		
		verify(sender).sendMessage("only_players");
	}
	
	
	@Test
	public void channels_disabled_gives_error_message(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_channels_enable()).thenReturn(false);
		
		sut.onCommand(player, null, "", new String[]{});
		
		verify(player).sendMessage("something_disabled");
	}
	
	@Test
	public void no_race_selected_returns_error(){
		sut.onCommand(player, null, null, new String[]{"message"});
		
		verify(player).sendMessage("no_race_selected");
	}
	
	@Test
	public void sending_message_with_selected_race_works_and_empty_message_fails(){
		String raceName = "race";
		
		RaceContainer container = mock(RaceContainer.class);
		when(container.getName()).thenReturn(raceName);
		
		when(RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(playerName)).thenReturn(container);
		
		sut.onCommand(player, null, null, new String[]{});
		
		verify(player).sendMessage("send_empty_message");
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
