/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.commands.health;

import static de.tobiyas.racesandclasses.translation.languages.Keys.plugin_pre;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;

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
		
		verify(sender).sendMessage(plugin_pre + "only_players");
	}
	
	@Test
	public void show_traits_works_for_own_traits(){
		when(Bukkit.getPlayer(playerName)).thenReturn((Player) sender);
		
		sut.onCommand(sender, null, "", new String[]{});
		
		verify(sender, times(1)).sendMessage(plugin_pre + "open_traits");
		
		verify((Player) sender, times(1)).openInventory(any(TraitInventory.class));
	}
	
	@Test
	public void show_traits_fails_for_other_player_if_not_found(){
		when(Bukkit.getPlayer(playerName)).thenReturn((Player) sender);
		
		sut.onCommand(sender, null, "", new String[]{"other"});
		
		verify(sender, times(1)).sendMessage(plugin_pre + "player_not_exist");
		
		verify((Player) sender, never()).openInventory(any(TraitInventory.class));
	}
	
	@Test
	public void show_traits__for_other_player_works(){
		String otherPlayer = "otherPlayer";
		GenerateBukkitServer.generatePlayerOnServer(otherPlayer);
		
		when(Bukkit.getPlayer(playerName)).thenReturn((Player) sender);
		
		sut.onCommand(sender, null, "", new String[]{otherPlayer});
		
		verify(sender, times(1)).sendMessage(plugin_pre + "open_traits");
		
		verify((Player) sender, times(1)).openInventory(any(TraitInventory.class));
	}
}
