package de.tobiyas.racesandclasses.eventprocessing.worldresolver;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.WorldEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

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
		String playerWorld = player.getWorld().getName();
		
		for(String world : RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_worldsDisabled()){
			if(world.equalsIgnoreCase(playerWorld)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the passed Player associated to the player name is on a disabled world.
	 * 
	 * @param playerName to check.
	 */
	public static boolean isOnDisabledWorld(String playerName){
		Player player = Bukkit.getPlayer(playerName);
		return isOnDisabledWorld(player);
	}
}
