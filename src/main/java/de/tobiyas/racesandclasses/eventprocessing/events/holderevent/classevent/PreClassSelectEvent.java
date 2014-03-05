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
package de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;

/**
 * This Event is fired, when a Player tries to select a Class. 
 * 
 * @author Tobiyas
 */
public class PreClassSelectEvent extends HolderPreSelectEvent{

	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	
	/**
	 * A player has selected a class.
	 * 
	 * @param player that selected the class
	 * @param classToSelect that was selected
	 */
	public PreClassSelectEvent(Player player, ClassContainer classToSelect) {
		super(player,  classToSelect);
	}

	/**
	 * A player has selected a class.
	 * 
	 * @param player that selected the class
	 * @param raceToSelect that was selected
	 * @param checkPermissions if the permissions should be checked
	 * @param checkCooldown if the Cooldown should be checked
	 */
	public PreClassSelectEvent(Player player, ClassContainer classToSelect, boolean checkPermissions, boolean checkCooldown) {
		super(player, classToSelect, checkCooldown, checkPermissions);
	}
	

	public ClassContainer getClassToSelect() {
		return (ClassContainer) holderToSelect;
	}



	/**
	 * needed for Bukkit to get the list of Handlers interested
	 * @return
	 */
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
