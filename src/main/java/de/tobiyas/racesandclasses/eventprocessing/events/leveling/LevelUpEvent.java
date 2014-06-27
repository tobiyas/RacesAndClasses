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

import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;

public class LevelUpEvent extends LevelEvent{

	private final static HandlerList handlers = new HandlerList();
	
	
	/**
	 * The level the player started
	 */
	protected int fromLevel;
	
	
	/**
	 * The Level the Player is now
	 */
	protected int toLevel;
	
	
	/**
	 * Indicates the Player has done a Level up.
	 * 
	 * @param player that leveled.
	 * @param fromLevel the Level the Player was before
	 * @param toLevel the Level the Player is afterwards.
	 */
	public LevelUpEvent(RaCPlayer player, int fromLevel, int toLevel) {
		super(player);
		
		this.fromLevel = fromLevel;
		this.toLevel = toLevel;
	}


	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	

	public static HandlerList getHandlerList() {
        return handlers;
    }



	/**
	 * @return the fromLevel
	 */
	public int getFromLevel() {
		return fromLevel;
	}



	/**
	 * @return the toLevel
	 */
	public int getToLevel() {
		return toLevel;
	}
	
	
}
