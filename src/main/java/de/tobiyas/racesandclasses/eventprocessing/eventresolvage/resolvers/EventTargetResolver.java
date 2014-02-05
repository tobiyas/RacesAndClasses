package de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventTargetResolver {

	/**
	 * Returns the Target Entity of the Event
	 * 
	 * @param event to check
	 * 
	 * @return the entity or null if not found
	 */
	public static Entity getTargetEntityFromEvent(Event event){
		if(event instanceof PlayerInteractEntityEvent){
			return ((PlayerInteractEntityEvent) event).getRightClicked();
		}
		
		if(event instanceof EntityDamageEvent){
			return ((EntityDamageEvent) event).getEntity();
		}
		
		if(event instanceof EntityTargetEvent){
			return ((EntityTargetEvent) event).getEntity();
		}
		
		return null;
	}
	
	
	/**
	 * Returns the block that is a target in this event
	 * 
	 * @param event to parse from
	 * 
	 * @return the block or null
	 */
	public static Block getTargetBlockFromEvent(Event event){
		if(event instanceof PlayerInteractEvent){
			if(((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_BLOCK){
				return ((PlayerInteractEvent) event).getClickedBlock();
			}
		}
		
		if(event instanceof BlockEvent){
			return ((BlockEvent) event).getBlock();
		}
		
		return null;
	}
}
