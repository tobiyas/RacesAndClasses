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
package de.tobiyas.racesandclasses.commands.force;

import static de.tobiyas.racesandclasses.translation.languages.Keys.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_ForceClass extends AbstractCommand {

	/**
	 * The plugin called stuff upon
	 */
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_ForceClass() {
		super("racforceclass");
		
		plugin = RacesAndClasses.getPlugin();

//		String command = "racforceclass";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if(!plugin.getConfigManager().getGeneralConfig().isConfig_classes_enable()){
			LanguageAPI.sendTranslatedMessage(sender, "something_disabled",
					"value", "Class");
			return true;
		}
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.forceChange)) return true;
		
		if(args.length < 2){
			LanguageAPI.sendTranslatedMessage(sender, wrong_command_use,
					"command", "/racforceclass <player> <class name>");
			return true;
		}
		
		String playerToChange = args[0];
		String newClass = args[1];
		
		RaCPlayer toChange = RaCPlayerManager.get().getPlayerByName(playerToChange);
		if(toChange == null){
			LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
					"player", playerToChange);
			return true;
		}
		
		ClassManager classManager = plugin.getClassManager();
		if(classManager.getHolderByName(newClass) == null){
			LanguageAPI.sendTranslatedMessage(sender, class_not_exist,
					"class", newClass);
			return true;
		}
		
		boolean worked = false;
		if(classManager.getHolderOfPlayer(toChange) == classManager.getDefaultHolder()){
			 worked = classManager.addPlayerToHolder(toChange, newClass, true);
		}else{
			worked = classManager.changePlayerHolder(toChange, newClass, true);
		}
		
		if(!worked){
			sender.sendMessage(ChatColor.RED + "This did not work. :(");
			return true;
		}
		
		if(toChange.isOnline()){
			LanguageAPI.sendTranslatedMessage(toChange, class_changed_to,
					"class", newClass);
		}
		
		LanguageAPI.sendTranslatedMessage(sender, class_changed_to_other,
				"player", toChange.getName(), "class", newClass);
		
		return true;
	}

}
