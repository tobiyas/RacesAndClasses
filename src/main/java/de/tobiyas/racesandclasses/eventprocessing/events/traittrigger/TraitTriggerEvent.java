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
package de.tobiyas.racesandclasses.eventprocessing.events.traittrigger;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitTriggerEvent extends Event {
	
	/**
	 * Some strange list. Whatever it tells...
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * World the event triggered on.
	 */
	protected World world;
	
	
	/**
	 * The trait triggered.
	 */
	protected final Trait trait;
	
	/**
	 * The Player that triggered the Event.
	 */
	protected final Player player;
	
	
	/**
	 * This is a custom event fired when a Trait is triggered.
	 * The world passed is the location the trait is triggered.
	 * 
	 * 
	 * @param world
	 */
	public TraitTriggerEvent(Player player, Trait trait, World world) {
		this.trait = trait;
		this.world = world;
		this.player = player;
	}
	
	
	/**
	 * Builds this from a event wrapper.
	 * 
	 * @param wrapper to build from
	 */
	public TraitTriggerEvent(EventWrapper wrapper, Trait trait){
		this.trait = trait;
		this.player = wrapper.getPlayer();
		this.world = wrapper.getWorld();
	}
	    

	public World getWorld() {
		return world;
	}
	
	public Trait getTriggeredTrait(){
		return trait;
	}
	

	public Player getPlayer() {
		return player;
	}

	
	//needed for custom events.
	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
