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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.persistence.file.YAMLPeristanceSaver;

public class CommandExecutor_SaveNow implements CommandExecutor{

	private RacesAndClasses plugin;
	
	public CommandExecutor_SaveNow(){
		plugin = RacesAndClasses.getPlugin();

		String command = "racsave";
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
		
		boolean async = false;
		if(args.length > 0 && args[0].contains("a")){
			async = true;
		}
		
		long timeBefore = System.currentTimeMillis();
		sender.sendMessage(ChatColor.GREEN + "Flushing data to files NOW " 
				+ ChatColor.LIGHT_PURPLE + (async ? "a" : "") + "synchronusly" 
				+ ChatColor.GREEN + ".");
		YAMLPeristanceSaver.flushNow(async, true);
		
		if(!async){
			long timeTaken = System.currentTimeMillis() - timeBefore;
			sender.sendMessage(ChatColor.GREEN + "Flushing done. Taken " + ChatColor.LIGHT_PURPLE + timeTaken
					+ ChatColor.GREEN + "ms.");
		}
		
		return true;
	}

	
}
