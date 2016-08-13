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
package de.tobiyas.racesandclasses.commands.debug;

import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_holder;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.racbuilder.gui.base.BaseSelectionInventory;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.util.inventorymenu.stats.StringSelectionInterface;

public class CommandExecutor_Edit extends AbstractCommand {
	
	/**
	 * If the Command is disabled.
	 */
	private boolean disabled = true;
	
	
	public CommandExecutor_Edit() {
		super("racedit", new String[]{"racedit"});
	}

	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!(sender instanceof Player)){
			LanguageAPI.sendTranslatedMessage(sender, only_players);
			return true;
		}
		
		if(disabled){
			sender.sendMessage(ChatColor.RED + "Feature is not ready to use.");
			return true;
		}
		
		Player player = (Player) sender;
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.racEdit)) return true;

		
		if(args.length > 0 && args[0].equalsIgnoreCase("test")){
			player.openInventory(new StringSelectionInterface(player, null, new HashMap<String, Object>(), "null", plugin));
			return true;
		}
		
		
		LanguageAPI.sendTranslatedMessage(sender, open_holder);
		player.openInventory(new BaseSelectionInventory(player, plugin));
		
		return true;
	}

}
