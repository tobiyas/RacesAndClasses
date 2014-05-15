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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;

public class RaceAPI {

	/**
	 * The plugin to get the RaceManager from
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Returns the Race of a player.
	 * If the player has no race, the Default race is returned.
	 * 
	 * @param playerUUID to search
	 * @return the {@link RaceContainer} of the player
	 * 
	 * @deprecated use {@link #getRaceOfPlayer(OfflinePlayer)} instead
	 */
	@Deprecated
	public static RaceContainer getRaceOfPlayer(String playerName){
		return getRaceOfPlayer(Bukkit.getPlayer(playerName));
	}
	
	/**
	 * Returns the Race of a player.
	 * If the player has no race, the Default race is returned.
	 * 
	 * @param playerUUID to search
	 * @return the {@link RaceContainer} of the player
	 */
	public static RaceContainer getRaceOfPlayer(Player player){
		RaceManager raceManager = plugin.getRaceManager();
		RaceContainer race = (RaceContainer) raceManager.getHolderOfPlayer(player.getUniqueId());
		if(race != null){
			return race;
		}else{
			return (RaceContainer) raceManager.getDefaultHolder();
		}
	}
	

	/**
	 * Returns the {@link RaceContainer} by the name.
	 * If the Race is not found, Null is returned.
	 * 
	 * @param raceName to search
	 * @return the Race corresponding to the name
	 */
	public static RaceContainer getRaceByName(String raceName){
		RaceManager raceManager = plugin.getRaceManager();
		return (RaceContainer) raceManager.getHolderByName(raceName);
	}
	
	
	/**
	 * Returns a List of all Race names available
	 * 
	 * @return list of Race names
	 */
	public static List<String> getAllRaceNames(){
		return plugin.getRaceManager().getAllHolderNames();
	}
	
	
	/**
	 * Gives the passed Player a Race.
	 * If he already has one, the Race is changed to the new one.
	 * 
	 * Returns true on success, 
	 * false if:
	 *  - playerUUID can not be found on Bukkit.getPlayer(playerUUID).
	 *  - the new raceName is not found.
	 *  - any internal error occurs.
	 * 
	 * @param playerUUID the player that the Race should be changed.
	 * @param className to change to
	 * @return true if worked, false otherwise
	 * 
	 * @deprecated use {@link #addPlayerToRace(OfflinePlayer, String)} instead
	 */
	public static boolean addPlayerToRace(String playerName, String raceName){
		return addPlayerToRace(Bukkit.getPlayer(playerName), raceName);
	}
	
	
	/**
	 * Returns the Default Race name.
	 * 
	 * @return name of default race.
	 */
	public static String getDefaultRaceName(){
		return plugin.getRaceManager().getDefaultHolder().getName();
	}
	
	/**
	 * Gives the passed Player a Race.
	 * If he already has one, the Race is changed to the new one.
	 * 
	 * Returns true on success, 
	 * false if:
	 *  - playerUUID can not be found on Bukkit.getPlayer(playerUUID).
	 *  - the new raceName is not found.
	 *  - any internal error occurs.
	 * 
	 * @param playerUUID the player that the Race should be changed.
	 * @param className to change to
	 * @return true if worked, false otherwise
	 */
	public static boolean addPlayerToRace(Player player, String raceName){
		if(player == null) return false;
		
		RaceManager manager = plugin.getRaceManager();
		RaceContainer wantedRace = (RaceContainer) manager.getHolderByName(raceName);
		if(wantedRace == null){
			return false;
		}
		
		return manager.changePlayerHolder(player.getUniqueId(), raceName, true);
	}
}
