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

import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to_other;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.something_disabled;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_ForceRace extends AbstractCommand {

	/**
	 * The plugin called stuff upon
	 */
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_ForceRace() {
		super("racforcerace");
		
		plugin = RacesAndClasses.getPlugin();
	}

	@Override
	public boolean onInternalCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces()){
			LanguageAPI.sendTranslatedMessage(sender, something_disabled,
					"value", "Races");
			return true;
		}
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.forceChange)) return true;
		
		if(args.length < 2){
			LanguageAPI.sendTranslatedMessage(sender, wrong_command_use,
					"command", "/racforcerace <player> <race name>");
			return true;
		}
		
		String playerToChange = args[0];
		String newRace = args[1];
		
		RaCPlayer toChange = RaCPlayerManager.get().getPlayerByName(playerToChange);
		if(toChange == null){
			LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
					"player", playerToChange);
			return true;
		}
		
		RaceManager raceManager = plugin.getRaceManager();
		if(raceManager.getHolderByName(newRace) == null){
			LanguageAPI.sendTranslatedMessage(sender, race_not_exist,
					"race", newRace);
			return true;
		}
		
		boolean worked = false;
		if(raceManager.getHolderOfPlayer(toChange) == raceManager.getDefaultHolder()){
			worked = raceManager.addPlayerToHolder(toChange, newRace, true);
		}else{
			worked = raceManager.changePlayerHolder(toChange, newRace, true);
		}
		
		if(!worked){
			sender.sendMessage(ChatColor.RED + "This did not work. :(");
			return true;
		}
		
		if(toChange.isOnline()){
			LanguageAPI.sendTranslatedMessage(toChange, race_changed_to,
					"race", newRace);
		}
		
		LanguageAPI.sendTranslatedMessage(sender, race_changed_to_other,
				"race", newRace, "player", playerToChange);
		return true;
	}

}
