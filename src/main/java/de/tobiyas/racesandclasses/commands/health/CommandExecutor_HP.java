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

import static de.tobiyas.racesandclasses.translation.languages.Keys.no_healthcontainer_found;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;

public class CommandExecutor_HP implements CommandExecutor {

	private RacesAndClasses plugin;
	
	public CommandExecutor_HP(){
		plugin = RacesAndClasses.getPlugin();

		String command = "playerhealth";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(LanguageAPI.translateIgnoreError(only_players)
					.build());
			return true;
		}
		
		Player player = (Player) sender;
		if(!plugin.getPlayerManager().displayHealth(player.getUniqueId())){
			sender.sendMessage(LanguageAPI.translateIgnoreError(no_healthcontainer_found)
					.build());
		}
		
		plugin.getPlayerManager().getSpellManagerOfPlayer(player.getUniqueId()).getManaManager().outputManaToPlayer();
		plugin.getPlayerManager().getPlayerLevelManager(player.getUniqueId()).forceDisplay();
		return true;
	}

}
