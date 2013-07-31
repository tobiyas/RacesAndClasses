package de.tobiyas.racesandclasses.eventprocessing.worldresolver;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.WorldEvent;

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
				return playerEvent.getPlayer().getLocation().getWorld().getName().toLowerCase();
			}
			
			if(event instanceof BlockEvent){
				BlockEvent blockEvent = (BlockEvent) event;
				return blockEvent.getBlock().getWorld().getName();
			}
			
			if(event instanceof InventoryEvent){
				InventoryEvent inventoryEvent = (InventoryEvent) event;
				return inventoryEvent.getViewers().get(0).getWorld().getName();
			}
			
			if(event instanceof WorldEvent){
				WorldEvent worldEvent = (WorldEvent) event;
				return worldEvent.getWorld().getName();
			}
			
			//TODO check if all work
			
		}catch(Exception exp){
			//nothing needed here.
			//returns default value below.
		}
		
		return "noWorldFoundForEvent";
	}
}
