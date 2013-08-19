package de.tobiyas.racesandclasses.commands.chat;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class CommandExecutor_WhisperTest {

	private CommandExecutor_Whisper sut;
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
		
		sut = new CommandExecutor_Whisper();

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
		String commandName = "whisper";
		
		PluginCommand command = PluginCommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_Whisper();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
	@Test
	public void fails_when_register_is_disabled(){
		when(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_whisper_enable()).thenReturn(false);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "[RaC] Whispering is not active, sorry.");
	}

	
	@Test
	public void fails_when_no_permission(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.whisper)).thenReturn(false);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender, never()).sendMessage(anyString());
	}
	
	@Test
	public void fails_with_no_args(){
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "You have to specify a player and a message.");
	}
	
	@Test
	public void fails_with_no_message(){
		sut.onCommand(sender, null, "", new String[]{playerName});
		
		verify(sender).sendMessage(ChatColor.RED + "Please give a message to send.");
	}
	
	@Test
	public void fails_with_no_player_found(){
		sut.onCommand(sender, null, "", new String[]{"invalid"});
		
		verify(sender).sendMessage(ChatColor.RED + "Target does not exist or is not online.");
	}
	
	@Test
	public void fails_with_sending_to_self(){
		sut.onCommand(sender, null, "", new String[]{playerName, "message"});
		
		verify(sender).sendMessage(ChatColor.RED + "You can't whisper yourself.");
	}
	
	@Test
	public void works_correct(){
		sut.onCommand(sender, null, "", new String[]{"other", "message", "toll"});
		
		verify(sender).sendMessage(ChatColor.LIGHT_PURPLE + "[Whisper to " + ChatColor.YELLOW + 
				"other" + ChatColor.LIGHT_PURPLE + "]: " + "message toll ");
		
		verify(Bukkit.getPlayer("other")).sendMessage(ChatColor.LIGHT_PURPLE + "[Whisper from " + ChatColor.YELLOW + 
				playerName + ChatColor.LIGHT_PURPLE + "]: " + "message toll ");
	}
}
