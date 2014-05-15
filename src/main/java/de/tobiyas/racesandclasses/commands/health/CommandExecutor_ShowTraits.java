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

import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_traits;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.traitcontainer.traitgui.TraitInventory;

public class CommandExecutor_ShowTraits extends AbstractCommand {

	private RacesAndClasses plugin;
	
	
	public CommandExecutor_ShowTraits() {
		super("showtraits", new String[]{"rst"});
		plugin = RacesAndClasses.getPlugin();

//		String command = "showtraits";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)){
			LanguageAPI.sendTranslatedMessage(sender, only_players);
			return true;
		}
		
		Player player = (Player) sender;
		Player playerToSearch = player;
		
		if(args.length > 0){
			String playerName = args[0];
			playerToSearch = Bukkit.getPlayer(playerName);
			if(playerToSearch == null){
				LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
						"player", playerName);
				return true;
			}
		}
		
		TraitInventory inventory = new TraitInventory(playerToSearch);
		player.openInventory(inventory);
		
		LanguageAPI.sendTranslatedMessage(sender, open_traits,
				"player", playerToSearch.getName());
		return true;
	}

}
