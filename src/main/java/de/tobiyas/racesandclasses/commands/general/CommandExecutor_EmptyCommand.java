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
package de.tobiyas.racesandclasses.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class CommandExecutor_EmptyCommand implements CommandExecutor {

	private RacesAndClasses plugin;
	
	
	public CommandExecutor_EmptyCommand(String commandName) {
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand(commandName).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command " + commandName + ".");
		}

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		sender.sendMessage(ChatColor.RED + "The Plugin: '" + ChatColor.AQUA + "RacesAndClasses" 
				+ ChatColor.RED + "' had errors on Startup! All commands are disabled.");

		return true;
	}

}
