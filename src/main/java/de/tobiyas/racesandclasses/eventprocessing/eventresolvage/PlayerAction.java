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
package de.tobiyas.racesandclasses.eventprocessing.eventresolvage;

public enum PlayerAction {

	/**
	 * player interacts with nothing
	 */
	INTERACT_AIR,
	
	/**
	 * Player is interacting with block
	 */
	INTERACT_BLOCK,
	
	/**
	 * Player is interacting with entity
	 */
	INTERACT_ENTITY,
	
	/**
	 * Player hits a block
	 */
	HIT_BLOCK,

	/**
	 * Player hits in Air.
	 */
	HIT_AIR,
	
	/**
	 * The Player has moved.
	 * <br>This includes teleportation
	 */
	PLAYER_MOVED,
	
	/**
	 * Player is getting targeted
	 */
	PLAYER_TARGETED,
	
	/**
	 * Player is taking damage
	 */
	TAKE_DAMAGE,
	
	/**
	 * The Player is dealing damage
	 */
	DO_DAMAGE,
	
	/**
	 * Player is casting a spell
	 */
	CAST_SPELL,
	
	/**
	 * The Player is changing his spell
	 */
	CHANGE_SPELL, 
	
	/**
	 * The player changes his arrow.
	 */
	CHANGE_ARROW,
	
	/**
	 * This indicates the player did nothing.
	 */
	NONE,
	
	/**
	 * No action realized...
	 */
	UNKNOWN, 

}
