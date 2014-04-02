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
package de.tobiyas.racesandclasses.commands.config;

import static de.tobiyas.racesandclasses.translation.languages.Keys.member_config_attribute_not_found;
import static de.tobiyas.racesandclasses.translation.languages.Keys.member_config_changed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.member_config_not_found;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;

public class CommandExecutor_RaceConfig implements CommandExecutor {

	private RacesAndClasses plugin;
	
	public CommandExecutor_RaceConfig(){
		plugin = RacesAndClasses.getPlugin();

		String command = "raceconfig";
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
			sender.sendMessage(LanguageAPI.translateIgnoreError(only_players).build());
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0){
			String your = LanguageAPI.translateIgnoreError("your").build();
			player.sendMessage(ChatColor.YELLOW + "=======" + your.toUpperCase() + " CONFIG=======");
			MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getUniqueId());
			if(config == null){
				LanguageAPI.sendTranslatedMessage(sender, member_config_not_found);
				return true;
			}
			
			Map<String, Object> configLines = config.getCurrentConfig(false);
			
			for(String attribute : configLines.keySet()){
				String value = String.valueOf(configLines.get(attribute));
				player.sendMessage(ChatColor.LIGHT_PURPLE + attribute + ChatColor.AQUA + ": " + ChatColor.BLUE + value);
			}
			
			return true;
		}
		
		if(args.length == 2){
			String attribute = args[0];
			String value = args[1];
			
			MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getUniqueId());
			if(config == null){
				LanguageAPI.sendTranslatedMessage(sender, member_config_not_found);
				return true;
			}
			
			boolean worked = config.changeAttribute(attribute, value);
			if(worked){
				LanguageAPI.sendTranslatedMessage(sender, member_config_changed,
						"attribute", attribute, "value", value);
			}else{
				LanguageAPI.sendTranslatedMessage(sender, member_config_attribute_not_found,
						"attribute", attribute);
			}
			
			return true;
		}
		
		
		LanguageAPI.sendTranslatedMessage(sender, "wrong_command_use",
				"command", "/raceconfig <attribute> <value>");
		return true;
	}

}
