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
package de.tobiyas.racesandclasses.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PrivateChat {
	
	public static void sendMessageToPlayer(CommandSender sender, CommandSender reciever, String message){
		reciever.sendMessage(ChatColor.LIGHT_PURPLE + "[Whisper from " + ChatColor.YELLOW + 
				sender.getName() + ChatColor.LIGHT_PURPLE + "]: " + message);
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "[Whisper to " + ChatColor.YELLOW + 
				reciever.getName() + ChatColor.LIGHT_PURPLE + "]: " + message);
	}
}
