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
package de.tobiyas.racesandclasses.APIs;

import java.util.List;
import java.util.Map;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;

public class CooldownApi {

	/**
	 * Plugin to call some fancy stuff on
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * This checks if the player has a cooldown on the given cooldown name
	 * 
	 * Returns true if the player has cooldown, returns false if not.
	 * 
	 * @param player to check
	 * @param cooldownName to check
	 */
	public static boolean hasCooldown(String playerName, String cooldownName){
		return plugin.getCooldownManager().stillHasCooldown(playerName, cooldownName) > 0;
	}
	
	
	
	/**
	 * Returns the cooldown time in seconds the player still has.
	 * Returns -1 (or value < 0) if none.
	 * 
	 * @param player to check 
	 * @param cooldownName to check
	 * 
	 * @return the time in seconds
	 */
	public static int getCooldownOfPlayer(String playerName, String cooldownName){
		return plugin.getCooldownManager().stillHasCooldown(playerName, cooldownName);
	}
	
	
	/**
	 * Sets a cooldown to a player with the given arguments.
	 * The player then has a cooldown oriented on the time passed.
	 * 
	 * @param player to set the cooldown on
	 * @param cooldownName to set the cooldown on
	 * @param timeInSeconds to be set
	 */
	public static void setPlayerCooldown(String playerName, String cooldownName, int timeInSeconds){
		plugin.getCooldownManager().setCooldown(playerName, cooldownName, timeInSeconds);
	}
	
	
	/**
	 * Removes the cooldown of a player
	 * 
	 * @param player to remove
	 * @param cooldownName to remove
	 */
	public static void removeCooldown(String playerName, String cooldownName){
		setPlayerCooldown(playerName, cooldownName, -1);
	}
	
	
	/**
	 * Returns a list of all cooldowns a player has.
	 * 
	 * 
	 * @param player
	 * @return
	 */
	public static List<String> getAllCooldownsOfPlayer(String playerName){
		return plugin.getCooldownManager().getAllCooldownsOfPlayer(playerName);
	}
	
	/**
	 * Returns a list of all cooldowns a player has.
	 * 
	 * 
	 * @param player to get.
	 * @return the Map of Name to time.
	 */
	public static Map<String,Integer> getAllCooldownsForPlayer(String playerName){
		return plugin.getCooldownManager().getAllCooldownsForPlayer(playerName);
	}


	/**
	 * Returns the Max Cooldown for the player for the Trait.
	 * 
	 * @param cooldown to search.
	 * @param playerName to search.
	 * 
	 * @return the Max cooldown
	 */
	public static int getMaxCooldownForTrait(String playerName, String cooldown) {
		RaCPlayer pl = RaCPlayerManager.get().getPlayerByName(playerName);
				
		for(Trait trait : pl.getTraits()){
			if(trait instanceof TraitWithRestrictions){
				TraitWithRestrictions twrTrait = (TraitWithRestrictions) trait;
				if(twrTrait.getCooldownName().equals(cooldown)) return twrTrait.getMaxUplinkTime();
			}
		}
		
		return -1;
	}
}
