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
package de.tobiyas.racesandclasses.commands.racespawn;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.util.autocomplete.AutoCompleteUtils;
import de.tobiyas.util.player.PlayerUtils;

public class CommandExecutor_RaceSpawn extends AbstractCommand {

	private RacesAndClasses plugin;

	
	public CommandExecutor_RaceSpawn(){
		super("racespawn");
		
		plugin = RacesAndClasses.getPlugin();
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enableRaceSpawn()){
			LanguageAPI.sendTranslatedMessage(sender, Keys.something_disabled,
					"value", "Race-Spawns");
			return true;
		}
		
		
		//Console may send someone to his home.
		if(sender == Bukkit.getConsoleSender() && args.length >= 1){
			Player player = PlayerUtils.getPlayer(args[0]);
			if(player == null) return true;
			
			Location spawn = RaceAPI.getRaceSpawnOfPlayer(player);
			if(spawn == null) return true;
			
			player.teleport(spawn);
			return true;
		}
		
		
		if(!(sender instanceof Player)) return false;
		
		Player player = (Player) sender;
		
		//to own spawn
		if(args.length == 0){
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceSpawnUseOwn)) return true;
			
			if(checkCooldown(player)) return true;
			sendPlayerToOwnSpawn(player);
			return true;
		}
		
		//setting spawn
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("set")){
				if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceSpawnCreate)) return true;				
				
				String race = args[1];
				RaceContainer container = RaceAPI.getRaceByName(race);
				if(container == null){
					LanguageAPI.sendTranslatedMessage(sender, Keys.race_not_exist, "race", race);
					return true;
				}
				
				plugin.getAddinManager().getRaceSpawnManager().setRaceSpawn(race, player.getLocation());
				LanguageAPI.sendTranslatedMessage(sender, Keys.race_spawn_created, "race", container.getDisplayName());
				return true;
			}
			
			LanguageAPI.sendTranslatedMessage(player, Keys.wrong_command_use, "command", "/racespawn set <racename>");
			return true;
		}
		
		//to other spawn
		if(args.length == 1){
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			AbstractTraitHolder holder = racPlayer.getRace();
			String own = holder.getDisplayName();
			String wanted = args[0];
			
			if(checkCooldown(player)) return true;
				
			if(own.equalsIgnoreCase(wanted)){
				if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceSpawnUseOwn)) return true;
				sendPlayerToOwnSpawn(player);
				return true;
			}else{
				if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceSpawnUseOther)) return true;
				sendPlayerToSpawn(player, wanted);
				return true;				
			}
		}
		
		return true;
	}
	
	
	/**
	 * Checks if the player has cooldown.
	 * 
	 * @param player to check
	 * 
	 * @return true if has cooldown.
	 */
	private boolean checkCooldown(Player player){
		int cooldown = plugin.getCooldownManager().stillHasCooldown(player.getName(), "command.racespawn");
		if(cooldown > 0){
			LanguageAPI.sendTranslatedMessage(player, Keys.cooldown_is_ready_in, "time", cooldown + " Seconds");
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Sends the player to the spawn passed.
	 * 
	 * @param player to send to spawn
	 * @param spawn to send to
	 */
	private boolean sendPlayerToSpawn(Player player, String spawn){
		Location loc = plugin.getAddinManager().getRaceSpawnManager().getSpawnForRace(spawn);
		
		if(loc == null){
			LanguageAPI.sendTranslatedMessage(player, Keys.race_spawn_no_spawnpoint, "race", spawn);
			return false;
		}
		
		player.teleport(loc);
		
		int cooldownTime = plugin.getConfigManager().getGeneralConfig().getConfig_raceSpawnCooldown();
		plugin.getCooldownManager().setCooldown(player.getName(), "command.racespawn", cooldownTime);
		
		LanguageAPI.sendTranslatedMessage(player, Keys.race_spawn_teleport_success, "race", spawn);
		return false;
	}
	
	
	/**
	 * Sends the player to the spawn passed.
	 * 
	 * @param player to send to spawn
	 * 
	 * @return if worked or not.
	 */
	private boolean sendPlayerToOwnSpawn(Player player){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		AbstractTraitHolder holder = racPlayer.getRace();
		if(holder == null) return false;
		
		return sendPlayerToSpawn(player, holder.getDisplayName());
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		
		String raceStart = null;
		Collection<String> raceNames = RaceAPI.getAllRaceNames();
		if(args.length == 1) { raceStart = args[0]; raceNames.add("set"); }
		if(args.length == 2 && args[0].equalsIgnoreCase("set")) raceStart = args[1];
		
		if(raceStart != null)return AutoCompleteUtils.getAllNamesWith(raceNames, raceStart);
		
		return super.onTabComplete(sender, command, alias, args);
	}

}
