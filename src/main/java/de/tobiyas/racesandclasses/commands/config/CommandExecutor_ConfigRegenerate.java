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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_ConfigRegenerate implements CommandExecutor {

	/**
	 * The Plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	
	public CommandExecutor_ConfigRegenerate() {
		this.plugin = RacesAndClasses.getPlugin();

		String command = "configregenerate";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		//check perms first.
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.configregen)) return true;
		
		
		//force the config to regenerate.
		plugin.getConfigManager().getGeneralConfig().checkRegenerate(true);
		
		sender.sendMessage(ChatColor.GREEN + "config.yml Regenerated.");
		return true;
	}

}
