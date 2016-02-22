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
package de.tobiyas.racesandclasses.listeners.classchangelistener;

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
import de.tobiyas.racesandclasses.configuration.racetoclass.RaceNotFoundException;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.HolderSelectionPreconditions.HolderPreconditionResult;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

/**
 * This class listens to {@link AfterClassSelectedEvent} + subclasses.
 * <br>It checks if the selecting player has the Race - selection - possibility to do so (if active).
 * <br>It also checks if the player has permission to this class (if active).
 * 
 * @author tobiyas
 */
public class ClassChangeSelectionListener implements Listener {
	
	
	/**
	 * The Main plugin
	 */
	private RacesAndClasses plugin;
	
	/**
	 * The command cooldown manager looking for cooldown
	 */
	private CooldownManager cooldownManager;
	
	
	/**
	 * Registers to Bukkit EventListener
	 */
	public ClassChangeSelectionListener(){	
		plugin = RacesAndClasses.getPlugin();
		cooldownManager = plugin.getCooldownManager();
		
		plugin.registerEvents(this);
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void checkRaceHasPermissionForClass(PreClassSelectEvent event){
		if(event.getClassToSelect() == plugin.getClassManager().getDefaultHolder()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_useRaceClassSelectionMatrix()){
			Player playerSelecting = event.getPlayer();
			RaCPlayer player = RaCPlayerManager.get().getPlayer(playerSelecting);
			
			String className = event.getClassToSelect().getDisplayName();
			
			AbstractTraitHolder holder = player.getRace();
			String raceName = holder.getDisplayName();
			
			//check if the Race can select the class passed.
			try{
				List<String> classList = plugin.getConfigManager().getRaceToClassConfig().getClassesValidForRace(raceName);
				if(classList != null && !classList.contains(className)){
					event.setCancelled("Your race can not select the class: " + className);
				}
			}catch(RaceNotFoundException exp){
				//If no class is found in list,
				//we assume that there is no limitation.
			}
			
		}
	}
	
		
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerhasPermissionToClass(PreClassSelectEvent event){
		if(event.getClassToSelect() == plugin.getClassManager().getDefaultHolder()) return;
		if(!event.isCheckPermissions()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForClasses()){
			Player player = event.getPlayer();
			String className = event.getClassToSelect().getDisplayName();
			
			String permissionNode = PermissionNode.classPermPre + className;
			if(!plugin.getPermissionManager().checkPermissionsSilent(player, permissionNode)){
				event.setCancelled("You do not have the Permission to select the Class " + className);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerHasUplinkOnChange(PreClassSelectEvent event){
		if(!event.isCheckCooldown()) return;
		if(event.getClassToSelect() == plugin.getClassManager().getDefaultHolder()) return;
		
		String playerName = event.getPlayer().getName();
		String commandName = "classchange";
		
		int remainingCooldown = cooldownManager.stillHasCooldown(playerName, commandName);
		if(remainingCooldown > 0){
			String message = ChatColor.RED + "You still have " + ChatColor.LIGHT_PURPLE + remainingCooldown 
					+ ChatColor.RED + " seconds cooldown on that command";
			event.setCancelled(message);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerMeetsPreconditions(PreClassSelectEvent event){
		Player player = event.getPlayer();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		
		AbstractTraitHolder newHolder = event.getHolderToSelect();
		if(newHolder != null){
			HolderPreconditionResult result = newHolder.getPreconditions().checkPreconditions(racPlayer);
			if(result != HolderPreconditionResult.RESTRICTIONS_MET){
				//TODO implement some sort of Precondition resolution
				String translated = LanguageAPI.translate(racPlayer, result.name().toLowerCase(), "HOLDER", "Specific Class");
				event.setCancelled(translated);
			}
		}
	}
	
	
	@EventHandler
	public void givePlayerUplinkAfterSelect(AfterClassSelectedEvent event){
		if(!event.isGiveCooldown()) return;
		
		String playerName = event.getPlayer().getName();
		String commandName = "classchange";
		
		int time = plugin.getConfigManager().getGeneralConfig().getConfig_raceChangeCommandUplink();
		cooldownManager.setCooldown(playerName, commandName, time);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void rescanHP(AfterClassSelectedEvent selectEvent){
		if(selectEvent.getPlayer() == null) return;
		if(selectEvent.getPlayer().getName() == null) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(selectEvent.getPlayer());
		plugin.getPlayerManager().displayHealth(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void resetHotkeys(AfterClassSelectedEvent selectEvent){
		if(selectEvent.getPlayer() == null) return;
		if(selectEvent.getPlayer().getName() == null) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(selectEvent.getPlayer());
		player.getHotkeyInventory().changeToBuildInv();
		player.getHotkeyInventory().clearAllSlots();
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void sendCommandAfterChange(AfterClassChangedEvent event){
		sendCommandAfterSelect((AfterClassSelectedEvent)event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void sendCommandAfterSelect(AfterClassSelectedEvent selectEvent){
		if(selectEvent.getPlayer() == null) return;
		if(selectEvent.getPlayer().getName() == null) return;
		
		
		List<String> commands = plugin.getConfigManager().getGeneralConfig().getConfig_class_commands_after_change();
		if(commands.isEmpty()) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(selectEvent.getPlayer());
		
		for(String command : commands){
			if(command == null || command.isEmpty()) continue;
			boolean asConsole = command.contains("%CONSOLE%");
			
			//Remove First char if is a dash.
			command = command.startsWith("/") ? command.substring(1) : command;
			
			//Replace stuff in the Command:
			command = command.replace("%CONSOLE%", "");
			command = command.replace("%CLASS%", ClassAPI.getClassNameOfPlayer(player));
			command = command.replace("%RACE%", RaceAPI.getRaceNameOfPlayer(player));
			command = command.replace("%PLAYER%", player.getName());
			command = command.replace("%DISPLAY%", player.getDisplayName());
			
			//Run the Command:
			if(asConsole) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			else player.chat("/"+command);
		}
	}
}
