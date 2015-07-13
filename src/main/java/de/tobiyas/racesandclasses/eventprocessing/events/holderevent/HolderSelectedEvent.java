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
package de.tobiyas.racesandclasses.eventprocessing.events.holderevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public abstract class HolderSelectedEvent extends Event{

	private static HandlerList handlers = new HandlerList();
	
	
	/**
	 * The player selecting the class
	 */
	protected Player player;
	
	/**
	 * The holders the player has selected to be
	 */
	protected AbstractTraitHolder holderToSelect;

	/**
	 * If the player should get an Cooldown after change.
	 */
	protected final boolean giveCooldown;
	
	
	/**
	 * A player has selected a holders.
	 * 
	 * @param player that selected the holders
	 * @param holderToSelect that was selected
	 */
	public HolderSelectedEvent(Player player, AbstractTraitHolder holderToSelect) {
		this.player = player;
		this.holderToSelect = holderToSelect;
		
		this.giveCooldown = true;
	}
	
	/**
	 * A player has selected a holders.
	 * 
	 * @param player that selected the holders
	 * @param holderToSelect that was selected
	 * @param if the player should get an Cooldown
	 */
	public HolderSelectedEvent(Player player, AbstractTraitHolder holderToSelect, boolean giveCooldown) {
		this.player = player;
		this.holderToSelect = holderToSelect;
		
		this.giveCooldown = giveCooldown;
	}
	
	
	public Player getPlayer() {
		return player;
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


	/**
	 * Returns the new Holder the player selects
	 * 
	 * @return
	 */
	public AbstractTraitHolder getHolderToSelect() {
		return holderToSelect;
	}

	public boolean isGiveCooldown() {
		return giveCooldown;
	}
}
