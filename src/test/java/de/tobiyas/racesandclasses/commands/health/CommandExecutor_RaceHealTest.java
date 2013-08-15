package de.tobiyas.racesandclasses.commands.health;

import static org.mockito.Mockito.*;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractChatCommandTest;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;


@PrepareForTest(CertainVersionChecker.class)
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
	public void only_players_can_heal_self(){
		sender = mock(CommandSender.class);
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.healSelf)).thenReturn(true);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "You have to be a Player to use this command.");
	}
	
	
	@Ignore("Powermock can't mock the player.getMaxHealth().  WTF?!?")
	@Test
	public void heal_self_works() throws Exception{
		when(RacesAndClasses.getPlugin().getPermissionManager().checkPermissions(sender, PermissionNode.healSelf)).thenReturn(true);
		when(((Player)sender) .getMaxHealth()).thenReturn(20d);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.GREEN + "You have been healed.");
	}
}
