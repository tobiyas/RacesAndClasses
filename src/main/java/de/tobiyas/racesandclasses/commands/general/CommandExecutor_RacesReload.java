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

import static de.tobiyas.racesandclasses.translation.languages.Keys.reload_message;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_RacesReload extends AbstractCommand {
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_RacesReload(){
		super("racesreload");
		
		plugin = RacesAndClasses.getPlugin();

//		String command = "racesreload";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.reload)) return true;
		
		long timeTaken = 0;
		if(args.length == 1){
			timeTaken = plugin.fullReload(true, args[0].contains("gc"));
		}else
			timeTaken = plugin.fullReload(true, false);
		
		plugin.getDebugLogger().log("[RaC] Reload called by: " + sender.getName() + " took: " + timeTaken + " ms.");
		
		LanguageAPI.sendTranslatedMessage(sender, reload_message,
				"time", String.valueOf(timeTaken));
		return true;
	}

}
