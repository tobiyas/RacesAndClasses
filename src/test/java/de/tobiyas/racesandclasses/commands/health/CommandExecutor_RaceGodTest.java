package de.tobiyas.racesandclasses.commands.health;

import static de.tobiyas.racesandclasses.translation.languages.Keys.failed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.plugin_pre;
import static de.tobiyas.racesandclasses.translation.languages.Keys.success;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractChatCommandTest;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_RaceGodTest extends AbstractChatCommandTest{

	public CommandExecutor_RaceGodTest() {
		super(CommandExecutor_RaceGod.class, "racegod");
	}
	
	
	@Test
	public void fails_on_no_permission(){
		this.sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender, never()).sendMessage(anyString());
	}

	@Test
	public void fails_with_console_sender_and_no_args(){
		sender = mock(CommandSender.class);
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.god)).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(plugin_pre + only_players);
	}
	
	@Test
	public void fails_when_more_than_1_arg(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.god)).thenReturn(true);
		sut.onCommand(sender, null, "", new String[]{"arg1", "arg2"});
		
		verify(sender).sendMessage(plugin_pre + wrong_command_use);
	}
	
	@Test
	public void changing_god_with_player_on_self_works(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.god)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getPlayerManager().switchGod(playerName)).thenReturn(true);
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(plugin_pre + success);
	}
	
	@Test
	public void changing_god_with_player_on_self_fails_when_healthmanager_fails(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.god)).thenReturn(true);
		when(RacesAndClasses.getPlugin().getPlayerManager().switchGod(playerName)).thenReturn(false);
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(plugin_pre + failed);
	}
	
	@Test
	public void fails_when_player_in_args_not_found(){
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.god)).thenReturn(true);
		when(Bukkit.getPlayer("invalid")).thenReturn(null);
		sut.onCommand(sender, null, "", new String[]{"invalid"});
		
		verify(sender).sendMessage(plugin_pre + player_not_exist);
	}
	
}
