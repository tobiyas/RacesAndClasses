package de.tobiyas.racesandclasses.commands.health;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

public class CommandExecutor_HPTest {

	private CommandExecutor_HP sut;
	private CommandSender sender;
	
	private String playerName = "console";
	
	
	@Before
	public void setup(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		GenerateBukkitServer.generatePlayerOnServer(playerName);
		GenerateBukkitServer.generatePlayerOnServer("other");
		
		ConfigManager configManager = mock(ConfigManager.class, RETURNS_DEEP_STUBS);
		((MockRaCPlugin) RacesAndClasses.getPlugin() ).setConfigManager(configManager);
		
		sut = new CommandExecutor_HP();

		sender = mock(Player.class);
		when(sender.getName()).thenReturn(playerName);
		
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_whisper_enable()).thenReturn(true);
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.whisper)).thenReturn(true);
	}
	
	
	@After
	public void teardown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	
	@Test
	public void command_registration_works(){
		String commandName = "playerhealth";
		
		PluginCommand command = PlugincommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_HP();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
	
	@Test
	public void console_calling_gets_error(){
		sender = mock(CommandSender.class);
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "Only Players can use this command.");
	}
	
	
	@Test
	public void health_displaying_works(){
		when(RacesAndClasses.getPlugin().getPlayerManager().displayHealth(playerName)).thenReturn(true);
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender, never()).sendMessage(anyString());
	}

	@Test
	public void health_displaying_fails_when_display_errors(){
		when(RacesAndClasses.getPlugin().getPlayerManager().displayHealth(playerName)).thenReturn(false);
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "Something gone Wrong. No healthcontainer found for you.");
	}
}
