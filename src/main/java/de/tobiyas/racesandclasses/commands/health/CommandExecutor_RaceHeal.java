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

import static de.tobiyas.racesandclasses.translation.languages.Keys.healed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.healed_by;
import static de.tobiyas.racesandclasses.translation.languages.Keys.healed_other;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_RaceHeal extends AbstractCommand {
	
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_RaceHeal(){
		super("raceheal", new String[]{"rh", "rheal"});
		plugin = RacesAndClasses.getPlugin();
	}
	
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command, String label,
			String[] args) {
	
		if(args.length == 0){
			healSelf(sender);
			return true;
		}
		
		if(args.length == 1){
			String otherName = args[0];
			healOther(sender, otherName);
			return true;
		}
		
		LanguageAPI.sendTranslatedMessage(sender, wrong_command_use,
				"command", "/raceheal [PlayerName]");
		return true;
	}

	
	/**
	 * The Player tries to heal himself.
	 * This only workes for Players using this command,
	 * with Permissions.
	 * 
	 * @param sender that tries to heal himself.
	 */
	private void healSelf(CommandSender sender){
		if(plugin.getPermissionManager().checkPermissions(sender, PermissionNode.healSelf)){
			if(sender instanceof Player){
				Player player = (Player) sender;
				RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
				
				double maxHealth = CompatibilityModifier.BukkitPlayer.safeGetMaxHealth(player);
				CompatibilityModifier.BukkitPlayer.safeSetHealth(maxHealth, player);
				
				racPlayer.getManaManager().fillMana(10000);
				
				LanguageAPI.sendTranslatedMessage(sender, healed);
			}else{
				LanguageAPI.sendTranslatedMessage(sender, only_players);
			}
		}
	}
	
	
	/**
	 * The Player tries to heal someone else.
	 * This only works with Permissions and if other
	 * Player is existent and online.
	 * 
	 * @param sender that tries to heal himself.
	 */
	private void healOther(CommandSender sender, String otherName){
		if(plugin.getPermissionManager().checkPermissions(sender, PermissionNode.healOther)){
			RaCPlayer otherRaCPlayer = RaCPlayerManager.get().getPlayerByName(otherName);
			if(otherRaCPlayer != null && otherRaCPlayer.isOnline()){
				Player player = (Player) sender;
				double maxHealth = CompatibilityModifier.BukkitPlayer.safeGetMaxHealth(otherRaCPlayer.getPlayer());
				CompatibilityModifier.BukkitPlayer.safeSetHealth(maxHealth, otherRaCPlayer.getPlayer());
				
				otherRaCPlayer.getManaManager().fillMana(10000);
				
				LanguageAPI.sendTranslatedMessage(sender, healed_other,
						"player", otherRaCPlayer.getName());
				
				LanguageAPI.sendTranslatedMessage(otherRaCPlayer, healed_by,
						"player", player.getName());
			}else{
				LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
						"player", otherName);
			}
		}
	}
}
