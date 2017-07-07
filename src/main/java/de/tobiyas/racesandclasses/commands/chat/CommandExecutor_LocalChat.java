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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;

public class CommandExecutor_LocalChat extends AbstractCommand{

	public CommandExecutor_LocalChat(){
		super("localchat", new String[]{"lc", "l"});
	}
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			LanguageAPI.sendTranslatedMessage(sender,"only_players");
			return true;
		}
		
		Player player = (Player) sender;		
		if(args.length == 0){
			LanguageAPI.sendTranslatedMessage(sender,"send_empty_message");
			return true;
		}
		
		String message = join(" ", args);
		plugin.getChannelManager().broadcastMessageToChannel("Local", player, message);
		return true;
	}
	
	
	private static String join(String delimiter, String[] args){
		StringBuilder builder = new StringBuilder();
		for(String arg : args) builder.append(delimiter).append(arg);
		return builder.length() == 0 ? "" : builder.substring(delimiter.length());
	}

}
