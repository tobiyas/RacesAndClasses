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
package de.tobiyas.racesandclasses.listeners.racechangelistener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfig;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.HolderSelectionPreconditions.HolderPreconditionResult;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

/**
 * This class listens to {@link AfterRaceSelectedEvent} + subclasses.
 * <br>It checks if the player has permission to this race (if active).
 * 
 * @author tobiyas
 */
public class RaceChangeSelectionListener implements Listener {
	
	
	/**
	 * The Main plugin
	 */
	private RacesAndClasses plugin;
	
	
	/**
	 * The CooldownManagerForCommands
	 */
	private CooldownManager cooldownManager;
	
	/**
	 * Registers to Bukkit EventListener
	 */
	public RaceChangeSelectionListener(){	
		plugin = RacesAndClasses.getPlugin();
		cooldownManager = plugin.getCooldownManager();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerhasPermissionToRace(PreRaceSelectEvent event){
		if(event.getRaceToSelect() == plugin.getRaceManager().getDefaultHolder()) return;
		if(!event.isCheckPermissions()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForRaces()){
			RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
			String raceName = event.getRaceToSelect().getDisplayName();
			
			String permissionNode = PermissionNode.racePermPre + raceName;
			if(!plugin.getPermissionManager().checkPermissionsSilent(player.getPlayer(), permissionNode)){
				event.setCancelled("You do not have the Permission to select the Race" + raceName);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void checkClassHasPermissionForRace(PreRaceSelectEvent event){
		if(event.getRaceToSelect() == plugin.getClassManager().getDefaultHolder()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_useRaceClassSelectionMatrix()){
			RaCPlayer playerSelecting = RaCPlayerManager.get().getPlayer(event.getPlayer());
			
			String raceName = event.getRaceToSelect().getDisplayName();
			
			AbstractTraitHolder holder = playerSelecting.getclass();
			if(holder == null) return; //no class -> no restrictions.
			String className = holder.getDisplayName();
			
			//check if the Race can select the class passed.
			boolean valid = plugin.getConfigManager().getRaceToClassConfig().isValidCombination(raceName, className);
			if(!valid) event.setCancelled("Your class can not select the race: " + raceName);
		}
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerMeetsPreconditions(PreRaceSelectEvent event){
		Player player = event.getPlayer();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		
		AbstractTraitHolder newHolder = event.getHolderToSelect();
		if(newHolder != null){
			HolderPreconditionResult result = newHolder.getPreconditions().checkPreconditions(racPlayer);
			if(result != HolderPreconditionResult.RESTRICTIONS_MET){
				//TODO implement some sort of Precondition resolution
				String translated = LanguageAPI.translate(racPlayer, result.name().toLowerCase(), "HOLDER", "Specific Race");
				event.setCancelled(translated);
			}
		}
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerHasUplinkOnChange(PreRaceSelectEvent event){
		if(!event.isCheckCooldown()) return;
		
		String playerName = event.getPlayer().getName();
		String commandName = "racechange";
		
		int remainingCooldown = cooldownManager.stillHasCooldown(playerName, commandName);
		if(remainingCooldown > 0){
			String message = ChatColor.RED + "You still have " + ChatColor.LIGHT_PURPLE + remainingCooldown 
					+ ChatColor.RED + " seconds cooldown on that command";
			event.setCancelled(message);
		}
	}
	
	
	@EventHandler
	public void givePlayerUplinkAfterSelect(AfterRaceSelectedEvent event){
		if(!event.isGiveCooldown()) return;
		if(event.getRaceToSelect() == plugin.getRaceManager().getDefaultHolder()) return;
		
		String playerName = event.getPlayer().getName();
		String commandName = "racechange";
		
		int time = plugin.getConfigManager().getGeneralConfig().getConfig_raceChangeCommandUplink();
		cooldownManager.setCooldown(playerName, commandName, time);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void rescanHP(AfterRaceSelectedEvent selectEvent){
		if(selectEvent.getPlayer() == null) return;
		if(selectEvent.getPlayer().getName() == null) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(selectEvent.getPlayer());
		plugin.getPlayerManager().getContainer(player).rescan();
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void sendCommandAfterChange(AfterRaceChangedEvent event){
		sendCommandAfterSelect((AfterRaceSelectedEvent) event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void sendCommandAfterSelect(AfterRaceSelectedEvent selectEvent){
		if(selectEvent.getPlayer() == null) return;
		if(selectEvent.getPlayer().getName() == null) return;
		
		
		List<String> commands = plugin.getConfigManager().getGeneralConfig().getConfig_race_commands_after_change();
		if(commands.isEmpty()) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(selectEvent.getPlayer());
		
		for(String command : commands){
			if(command == null || command.isEmpty()) continue;
			boolean asConsole = command.contains("%CONSOLE%");
			boolean asOp = command.contains("%OP%");
			
			//Remove First char if is a dash.
			command = command.startsWith("/") ? command.substring(1) : command;
			
			//Replace stuff in the Command:
			command = command.replace("%CONSOLE%", "");
			command = command.replace("%OP%", "");
			
			command = command.replace("%CLASS%", OrEmpty(ClassAPI.getClassNameOfPlayer(player)));
			command = command.replace("%RACE%", OrEmpty(RaceAPI.getRaceNameOfPlayer(player)));
			command = command.replace("%PLAYER%", player.getName());
			command = command.replace("%DISPLAY%", player.getDisplayName());
			
			//Set op, if wanted:
			boolean opBefore = player.isOp();
			if(asOp && !asConsole) player.setOp(true);
			
			try{
				//Run the Command:
				if(asConsole) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				else player.chat("/"+command);				
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(asOp && !asConsole && !opBefore) player.setOp(false);
			}
		}
	}
	
	private String OrEmpty(String value){
		return value == null ? "" : value;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void removeClassAfterChange(AfterRaceChangedEvent event){
		sendCommandAfterSelect((AfterRaceSelectedEvent) event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void removeClassAfterChange(AfterRaceSelectedEvent selectEvent){
		GeneralConfig config = plugin.getConfigManager().getGeneralConfig();
		if(!config.isConfig_classes_removeClassOnRaceChange()) return;
		if(!config.isConfig_enableRaces()) return;
		if(!config.isConfig_classes_enable()) return;
		
		//Reset!
		String defaultClass = ClassAPI.getDefaultClassName();
		ClassAPI.addPlayerToClass(selectEvent.getPlayer(), defaultClass);
	}
}
