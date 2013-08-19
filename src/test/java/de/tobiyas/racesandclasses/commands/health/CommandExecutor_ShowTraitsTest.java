package de.tobiyas.racesandclasses.commands.health;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;

import static org.mockito.Mockito.*;
import de.tobiyas.racesandclasses.commands.AbstractChatCommandTest;
import de.tobiyas.racesandclasses.traitcontainer.traitgui.TraitInventory;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class CommandExecutor_ShowTraitsTest extends AbstractChatCommandTest {

	public CommandExecutor_ShowTraitsTest() {
		super(CommandExecutor_ShowTraits.class, "showtraits");
	}
	
	
	@Test
	public void console_sender_can_not_view_traits(){
		CommandSender sender = mock(CommandSender.class);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender).sendMessage(ChatColor.RED + "[RaC] Only players can use this command.");
	}
	
	@Test
	public void show_traits_works_for_own_traits(){
		when(Bukkit.getPlayer(playerName)).thenReturn((Player) sender);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender, times(1)).sendMessage(ChatColor.GREEN + "[RaC] Opening Traits of " + ChatColor.LIGHT_PURPLE + playerName 
				+ ChatColor.GREEN + ".");
		
		verify((Player) sender, times(1)).openInventory(any(TraitInventory.class));
	}
	
	@Test
	public void show_traits_fails_for_other_player_if_not_found(){
		when(Bukkit.getPlayer(playerName)).thenReturn((Player) sender);
		
		sut.onCommand(sender, null, "", new String[]{"other"});
		
		verify(sender, times(1)).sendMessage(ChatColor.RED + "[Rac]" + " Could not find player: " + ChatColor.LIGHT_PURPLE + "other" + ChatColor.RED + ".");
		
		verify((Player) sender, never()).openInventory(any(TraitInventory.class));
	}
	
	@Test
	public void show_traits__for_other_player_works(){
		String otherPlayer = "otherPlayer";
		GenerateBukkitServer.generatePlayerOnServer(otherPlayer);
		
		when(Bukkit.getPlayer(playerName)).thenReturn((Player) sender);
		
		sut.onCommand(sender, null, "", new String[]{otherPlayer});
		
		verify(sender, times(1)).sendMessage(ChatColor.GREEN + "[RaC] Opening Traits of " + ChatColor.LIGHT_PURPLE + otherPlayer 
				+ ChatColor.GREEN + ".");
		
		verify((Player) sender, times(1)).openInventory(any(TraitInventory.class));
	}
}
