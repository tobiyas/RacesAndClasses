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
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitTriggerEvent extends Event implements Cancellable{
	
	private boolean isCancled = false;
	
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
	 * This is a custom event fired when a Trait is triggered.
	 * The world passed is the location the trait is triggered.
	 * 
	 * 
	 * @param world
	 */
	public TraitTriggerEvent(Trait trait, World world) {
		this.trait = trait;
		this.world = world;
	}
	    

	public World getWorld() {
		return world;
	}
	
	public Trait getTriggeredTrait(){
		return trait;
	}
	

	@Override
	public boolean isCancelled() {
		return isCancled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		isCancled = cancel;		
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
