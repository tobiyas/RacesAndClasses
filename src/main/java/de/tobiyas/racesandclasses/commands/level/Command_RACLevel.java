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
package de.tobiyas.racesandclasses.commands.level;

import static de.tobiyas.racesandclasses.translation.languages.Keys.number_not_readable;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.success;
import static de.tobiyas.racesandclasses.translation.languages.Keys.value_0_not_allowed;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelCalculator;
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelPackage;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class Command_RACLevel extends AbstractCommand {

	/**
	 * Registers the Command "class" to the plugin.
	 */
	public Command_RACLevel(){
		super("raclevel", "lvl");
	}	
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if((sender instanceof Player) && 
				(!RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.levelEdit) 
				|| args.length == 0 )){
			
			postLevel((Player)sender);
			return true;
		}
		
		if(args.length != 3){
			pasteHelp(sender);
			return true;
		}
		
		String subCommand = args[1];
		RaCPlayer player = RaCPlayerManager.get().getPlayerByName(args[0]);
		if(player == null){
			LanguageAPI.sendTranslatedMessage(sender, player_not_exist, "PLAYER", args[0]);
			return true;
		}
		
		int value = 0;
		try{
			value = Integer.valueOf(args[2]);
		}catch(NumberFormatException exp){
			LanguageAPI.sendTranslatedMessage(sender, number_not_readable);
			return true;
		}
		
		PlayerLevelManager manager = player.getLevelManager();
		if(manager == null){
			LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
					"player", player.getName());
			return true;
		}
		
		if(value == 0){
			LanguageAPI.sendTranslatedMessage(sender, value_0_not_allowed,
					"player", player.getName());
			return true;
		}
		
		
		if("exp".equalsIgnoreCase(subCommand)){
			if(value < 0){
				manager.removeExp(-value);
			}
			
			if(value > 0){
				manager.addExp(value);
			}
			
			LanguageAPI.sendTranslatedMessage(sender, success);
			return true;
		}
		
		if("lvl".equalsIgnoreCase(subCommand)){
			if(value > 0){
				manager.addLevel(value);
			}else{
				manager.removeLevel(-value);
			}
			
			LanguageAPI.sendTranslatedMessage(sender, success);
			return true;
		}
		
		pasteHelp(sender);
		return true;
	}

	/**
	 * Posts the level infos to the player.
	 * 
	 * @param sender
	 */
	private void postLevel(Player player) {
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		
		int level = LevelAPI.getCurrentLevel(player);
		int levelExp = (int) LevelAPI.getCurrentExpOfLevel(player);
		
		LevelPackage levelPack = LevelCalculator.calculateLevelPackage(level);
		int maxEXP = levelPack.getMaxEXP();

		pl.sendMessage(ChatColor.YELLOW + "==== Level ====");
		pl.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.AQUA + level);
		pl.sendMessage(ChatColor.YELLOW + "Exp: " + ChatColor.AQUA + levelExp + ChatColor.GRAY + "/" + ChatColor.AQUA + maxEXP);
	}
	
	

	/**
	 * Paste the help for the Level command
	 * 
	 * @param sender to send to
	 */
	private void pasteHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "=== RAC Level ===");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/raclevel <player> exp <value>" 
				+ ChatColor.YELLOW + "  Adds / Removes EXP to player.");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/raclevel <player> lvl <value>"
				+ ChatColor.YELLOW + "  Adds / Removes LEVELS to player.");
	}

}
