package de.tobiyas.racesandclasses.commands.config;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

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
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.generate.PluginCommandFactory;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.generate.plugin.MockRaCPlugin;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CommandExecutor_RaceConfigTest {

	private CommandExecutor_RaceConfig sut;
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
		
		sut = new CommandExecutor_RaceConfig();

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
		String commandName = "raceconfig";
		
		PluginCommand command = PluginCommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		sut = new CommandExecutor_RaceConfig();
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
	
	@Test
	public void calling_with_console_fails(){
		sender = mock(CommandSender.class);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage("only_players");
	}
	
	
	@Test
	public void passing_1_or_more_than_2_arguments_posts_help(){
		sut.onCommand(sender, null, "", new String[]{"arg1"});
		sut.onCommand(sender, null, "", new String[]{"arg1", "arg2", "arg3"});
		
		verify(sender, times(2)).sendMessage("wrong_command_use");
	}
	
	
	@Test
	public void pasting_config_works(){
		Map<String, Object> configMap = new HashMap<String, Object>();
		configMap.put("arg1", 1);
		configMap.put("arg2", "option2");
		
		MemberConfig config = mock(MemberConfig.class);
		when(config.getCurrentConfig(false)).thenReturn(configMap);
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName)).thenReturn(config);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.YELLOW + "=======YOUR CONFIG=======");
		verify(sender).sendMessage(ChatColor.LIGHT_PURPLE + "arg1" + ChatColor.AQUA + ": " + ChatColor.BLUE + 1);
		verify(sender).sendMessage(ChatColor.LIGHT_PURPLE + "arg2" + ChatColor.AQUA + ": " + ChatColor.BLUE + "option2");		
	}
	
	
	@Test
	public void pasting_config_fails_when_memberconfig_not_found(){
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.YELLOW + "=======YOUR CONFIG=======");
		verify(sender).sendMessage("member_config_not_found");
	}
	
	@Test
	public void setting_config_memberConfig_not_found_fails(){
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName)).thenReturn(null);
		
		sut.onCommand(sender, null, "", new String[]{"arg1", "value"});
		
		verify(sender).sendMessage("member_config_not_found");
	}
	
	@Test
	public void setting_config_memberConfig_worked(){
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName).changeAttribute("arg1", "value")).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{"arg1", "value"});
		
		verify(sender).sendMessage("member_config_changed");
	}

	@Test
	public void setting_config_memberConfig_fails_when_memberConfig_setting_fails(){
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName)).thenReturn(mock(MemberConfig.class));
		when(RacesAndClasses.getPlugin().getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName).setValue("arg1", "value")).thenReturn(false);
		
		sut.onCommand(sender, null, "", new String[]{"arg1", "value"});
		
		verify(sender).sendMessage("member_config_attribute_not_found");
	}
	
}
