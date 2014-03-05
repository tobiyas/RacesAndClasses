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
