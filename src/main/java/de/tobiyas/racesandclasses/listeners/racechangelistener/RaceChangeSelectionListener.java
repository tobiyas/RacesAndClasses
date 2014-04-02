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
import de.tobiyas.racesandclasses.configuration.racetoclass.RaceNotFoundException;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;
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
			Player player = event.getPlayer();
			String raceName = event.getRaceToSelect().getName();
			
			String permissionNode = PermissionNode.racePermPre + raceName;
			if(!plugin.getPermissionManager().checkPermissionsSilent(player, permissionNode)){
				event.setCancelled("You do not have the Permission to select the Race" + raceName);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void checkClassHasPermissionForRace(PreRaceSelectEvent event){
		if(event.getRaceToSelect() == plugin.getClassManager().getDefaultHolder()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_useRaceClassSelectionMatrix()){
			Player playerSelecting = event.getPlayer();
			
			String raceName = event.getRaceToSelect().getName();
			
			AbstractTraitHolder holder = plugin.getClassManager().getHolderOfPlayer(playerSelecting);
			if(holder == null) return; //no class -> no restrictions.
			String className = holder.getName();
			
			//check if the Race can select the class passed.
			try{
				List<String> classList = plugin.getConfigManager().getRaceToClassConfig().getClassesValidForRace(raceName);
				if(classList != null && !classList.contains(className)){
					event.setCancelled("Your class can not select the race: " + raceName);
				}
			}catch(RaceNotFoundException exp){
				//If no class is found in list,
				//we assume that there is no limitation.
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
		
		String playerName = event.getPlayer().getName();
		String commandName = "racechange";
		
		int time = plugin.getConfigManager().getGeneralConfig().getConfig_raceChangeCommandUplink();
		cooldownManager.setCooldown(playerName, commandName, time);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void rescanHP(AfterRaceSelectedEvent selectEvent){
		if(selectEvent.getPlayer() == null) return;
		if(selectEvent.getPlayer().getName() == null) return;
		
		Player player = selectEvent.getPlayer();
		
		plugin.getPlayerManager().checkPlayer(player);
		plugin.getPlayerManager().displayHealth(player);
	}
}
