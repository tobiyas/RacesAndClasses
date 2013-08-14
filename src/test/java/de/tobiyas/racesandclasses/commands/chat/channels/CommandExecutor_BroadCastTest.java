package de.tobiyas.racesandclasses.commands.chat.channels;

import static org.mockito.Mockito.*;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PlugincommandFactory;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.generate.plugin.MockRaCPlugin;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CommandExecutor_BroadCastTest {

	private CommandExecutor_BroadCast sut;
	private CommandSender sender;
	private String playerName = "console";
	
	
	@Before
	public void setup(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		
		ConfigManager configManager = mock(ConfigManager.class, RETURNS_DEEP_STUBS);
		((MockRaCPlugin) RacesAndClasses.getPlugin() ).setConfigManager(configManager);
		
		sut = new CommandExecutor_BroadCast();

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
		String commandName = "globalbroadcast";
		
		PluginCommand command = PlugincommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_BroadCast();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}

	
	@Test
	public void broadcast_with_disabled_channels_fails(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.broadcast)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_channels_enable()).thenReturn(false);
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "Channels are disabled.");
	}
	
	@Test
	public void broudcast_without_permissions_fails(){
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender, never()).sendMessage(anyString());
		verify(RacesAndClasses.getPlugin().getChannelManager(), never()).broadcastMessageToChannel(anyString(), any(CommandSender.class), anyString());
	}
	
	@Test
	public void broudcast_without_message_fails(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.broadcast)).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "No message given.");
	}

	@Test
	public void broudcast_with_message_works(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.broadcast)).thenReturn(true);
		String message = "Hallo!";
		
		sut.onCommand(sender, null, "", new String[]{message});
		
		verify(RacesAndClasses.getPlugin().getChannelManager()).broadcastMessageToChannel("Global", sender, message + " ");
	}
}
