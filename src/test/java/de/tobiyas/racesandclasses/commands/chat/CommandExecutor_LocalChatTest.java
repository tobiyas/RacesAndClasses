package de.tobiyas.racesandclasses.commands.chat;

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
import de.tobiyas.racesandclasses.generate.PluginCommandFactory;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.generate.plugin.MockRaCPlugin;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CommandExecutor_LocalChatTest {

	
	private CommandExecutor_LocalChat sut;
	private CommandSender sender;
	private String playerName = "console";
	
	
	@Before
	public void setup(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		
		ConfigManager configManager = mock(ConfigManager.class, RETURNS_DEEP_STUBS);
		((MockRaCPlugin) RacesAndClasses.getPlugin() ).setConfigManager(configManager);
		
		sut = new CommandExecutor_LocalChat();

		sender = mock(Player.class);
		when(sender.getName()).thenReturn(playerName);
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_channels_enable()).thenReturn(true);
	}
	
	
	@After
	public void teardown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	
	@Test
	public void command_registration_works(){
		String commandName = "localchat";
		
		PluginCommand command = PluginCommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_LocalChat();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
	
	@Test
	public void command_without_message_fails(){
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(plugin_pre + "send_empty_message");
	}
	
	
	@Test
	public void command_as_console_fails(){
		sender = mock(CommandSender.class);
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(plugin_pre + "only_players");
	}
	

	@Test
	public void command_with_message_works(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.broadcast)).thenReturn(true);
		String message = "Hallo!";
		
		sut.onCommand(sender, null, "", new String[]{message});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).broadcastMessageToChannel("Local", sender, message + " ");
	}
}
