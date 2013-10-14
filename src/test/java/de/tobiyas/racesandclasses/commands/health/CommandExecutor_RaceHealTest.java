package de.tobiyas.racesandclasses.commands.health;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractChatCommandTest;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;


public class CommandExecutor_RaceHealTest extends AbstractChatCommandTest {

	public CommandExecutor_RaceHealTest() {
		super(CommandExecutor_RaceHeal.class, "raceheal");
	}

	@Test
	public void heal_self_with_no_permissions_fails(){
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender, never()).sendMessage(anyString());
	}
	
	@Test
	public void heal_other_with_no_permissions_fails(){
		sut.onCommand(sender, null, "", new String[]{"other"});
		
		verify(sender, never()).sendMessage(anyString());
	}
	
	
	@Test
	public void only_players_can_heal_self(){
		sender = mock(CommandSender.class);
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.healSelf)).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "You have to be a Player to use this command.");
	}
	
	
	@Test
	public void heal_self_works() throws Exception{
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.healSelf)).thenReturn(true);
		when(((Player)sender) .getMaxHealth()).thenReturn(20d);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.GREEN + "You have been healed.");
	}
	
	@Test
	public void getting_error_when_more_than_one_arg(){
		sut.onCommand(sender, null, "", new String[]{"arg1", "arg2"});
		verify(sender).sendMessage(ChatColor.RED + "Wrong usage. Use: /raceheal [PlayerName]");
	}
	
	@Test
	public void fails_when_other_is_not_found(){
		String otherName = "other";
		
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.healOther)).thenReturn(true);
		when(Bukkit.getPlayer(otherName)).thenReturn(null);
		
		sut.onCommand(sender, null, "", new String[]{otherName});
		
		verify(sender).sendMessage(ChatColor.RED + "Player: " + ChatColor.LIGHT_PURPLE + otherName + ChatColor.RED + " could not be found.");
	}
	
	@Test
	public void fails_when_player_is_offline(){
		String otherName = "other";
		
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.healOther)).thenReturn(true);
		Player otherPlayer = mock(Player.class);
		when(otherPlayer.isOnline()).thenReturn(false);
		when(Bukkit.getPlayer(otherName)).thenReturn(otherPlayer);
		
		sut.onCommand(sender, null, "", new String[]{otherName});
		
		verify(sender).sendMessage(ChatColor.RED + "Player: " + ChatColor.LIGHT_PURPLE + otherName + ChatColor.RED + " could not be found.");
	}
	
	@Test
	public void healing_others_works(){
		String otherName = "other";
		GenerateBukkitServer.generatePlayerOnServer(otherName);
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.healOther)).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{otherName});
		
		verify(sender).sendMessage(ChatColor.GREEN + "You have healed: " + ChatColor.LIGHT_PURPLE +  otherName);
		verify(Bukkit.getPlayer(otherName)).sendMessage(ChatColor.GREEN + "You have been healed from: " + ChatColor.LIGHT_PURPLE + sender.getName());
	}
}