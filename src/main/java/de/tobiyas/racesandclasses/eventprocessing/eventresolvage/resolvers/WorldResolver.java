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
package de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.WorldEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;

public class WorldResolver {

	/**
	 * Extracts the world of the location of an Event
	 * 
	 * @param event
	 * @return
	 */
	public static String getWorldNameOfEvent(Event event){
		try{
			if(event instanceof PlayerEvent){
				PlayerEvent playerEvent = (PlayerEvent) event;
				if(playerEvent.getPlayer() == null){
					return "noWorldFoundForEvent";
				}
				
				return playerEvent.getPlayer().getLocation().getWorld().getName();
			}
			
			if(event instanceof BlockEvent){
				BlockEvent blockEvent = (BlockEvent) event;
				return blockEvent.getBlock().getWorld().getName();
			}
			
			if(event instanceof InventoryEvent){
				InventoryEvent inventoryEvent = (InventoryEvent) event;
				if(inventoryEvent.getViewers().isEmpty()){
					return "noWorldFoundForEvent";
				}
				
				return inventoryEvent.getViewers().get(0).getWorld().getName();
			}
			
			if(event instanceof WorldEvent){
				WorldEvent worldEvent = (WorldEvent) event;
				return worldEvent.getWorld().getName();
			}
			
			if(event instanceof EntityEvent){
				EntityEvent entityEvent = (EntityEvent) event;
				Entity entity = entityEvent.getEntity();
				if(entity == null){
					return "noWorldFoundForEvent";
				}
				
				return entity.getWorld().getName();
			}
			
			//TODO check if all work
		}catch(Exception exp){
			//nothing needed here.
			//returns default value below.
		}
		
		return "noWorldFoundForEvent";
	}

	/**
	 * Checks if the passed Player is on a disabled world.
	 * 
	 * @param player to check.
	 */
	public static boolean isOnDisabledWorld(Player player) {
		if(player == null) return false;
		
		return isDisabledWorld(player.getWorld());
	}
	
	/**
	 * Checks if the passed Player is on a disabled world.
	 * 
	 * @param player to check.
	 */
	public static boolean isOnDisabledWorld(RaCPlayer player) {
		return isDisabledWorld(player.getWorld());
	}
	
	/**
	 * Checks if the passed Player associated to the player name is on a disabled world.
	 * 
	 * @param player to check.
	 */
	public static boolean isOnDisabledWorld(String playerName){
		RaCPlayer player = RaCPlayerManager.get().getPlayerByName(playerName);
		return isOnDisabledWorld(player);
	}

	/**
	 * Checks if the World is disabled.
	 * 
	 * @param world to check
	 * @return true if disabled.
	 */
	public static boolean isDisabledWorld(World world) {
		if(world == null) return false;
		
		String worldName = world.getName();
		for(String disabled : RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_worldsDisabled()){
			if(disabled.equalsIgnoreCase(worldName)){
				return true;
			}
		}
		
		return false;
	}
}
