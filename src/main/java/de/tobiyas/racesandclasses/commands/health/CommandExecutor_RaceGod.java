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

import static de.tobiyas.racesandclasses.translation.languages.Keys.failed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.success;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_RaceGod extends AbstractCommand {
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_RaceGod(){
		super("racegod", new String[]{"rgod"});
		plugin = RacesAndClasses.getPlugin();

//		String command = "racegod";
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
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.god))
			return true;
		
		Player target = null;
		if(args.length == 1){
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(args[0]);
			target = racPlayer == null ? null : racPlayer.getPlayer();
		}
		
		if(args.length == 0){
			if(!(sender instanceof Player)){
				LanguageAPI.sendTranslatedMessage(sender, only_players, new HashMap<String, String>());
				return true;
			}
			
			target = (Player) sender;
		}
		
		if(args.length > 1){
			LanguageAPI.sendTranslatedMessage(sender, wrong_command_use,
					"command", "/racegod [playername]");
			return true;
		}
		
		if(target == null){
			LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
					"player", args[0]);
			return true;
		}
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(target);
		if(plugin.getPlayerManager().switchGod(racPlayer)){
			LanguageAPI.sendTranslatedMessage(sender, success);
		}else{
			LanguageAPI.sendTranslatedMessage(sender, failed);
		}
			
		return true;
	}

}
