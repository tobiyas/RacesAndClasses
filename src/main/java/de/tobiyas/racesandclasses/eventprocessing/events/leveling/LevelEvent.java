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
package de.tobiyas.racesandclasses.eventprocessing.events.leveling;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class LevelEvent extends PlayerEvent {

	private final static HandlerList handlers = new HandlerList();
	
	private final UUID playerUUID;
	
	/**
	 * Creates a new Level Event. 
	 * Manditory is the Player that the event is associated to.
	 * 
	 * @param player the Event is associated to.
	 */	
	public LevelEvent(UUID playerUUID){
		super(Bukkit.getPlayer(playerUUID));
		this.playerUUID = playerUUID;
	}

	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	

	public static HandlerList getHandlerList() {
        return handlers;
    }



	/**
	 * @return the player name
	 */
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	
	
	
}
