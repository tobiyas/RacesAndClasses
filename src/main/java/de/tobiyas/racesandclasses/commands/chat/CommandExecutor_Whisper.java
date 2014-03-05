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
package de.tobiyas.racesandclasses.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.chat.PrivateChat;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_Whisper implements CommandExecutor {
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_Whisper(){		
		plugin = RacesAndClasses.getPlugin();
	
		String command = "whisper";
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
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_whisper_enable()){
			sender.sendMessage(LanguageAPI.translateIgnoreError("something_disabled")
					.replace("value", "Whispers")
					.build());
			return true;
		}
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.whisper)) return true;
		
		if(args.length == 0){
			sender.sendMessage(LanguageAPI.translateIgnoreError("wrong_command_use")
				.replace("command", "/whisper <target> <message>")
				.build());
			return true;
		}
		

		Player target = Bukkit.getPlayer(args[0]);
		if(target == null){
			sender.sendMessage(LanguageAPI.translateIgnoreError("target_not_exist")
					.build());
			return true;
		}
		
		if(args.length == 1){
			sender.sendMessage(LanguageAPI.translateIgnoreError("wrong_command_use")
					.replace("command", "/whisper <target> <message>")
					.build());
			return true;
		}
		
		if(sender.getName().equals(target.getName())){
			sender.sendMessage(LanguageAPI.translateIgnoreError("whisper_yourself")
					.build());
			return true;
		}
		
		String message = "";
		for(int i = 1; i < args.length; i++){
			message += args[i] + " ";
		}
		
		PrivateChat.sendMessageToPlayer(sender, target, message);
		return true;
	}

}
