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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class RaceAPI {

	/**
	 * The plugin to get the RaceManager from
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Returns the Race of a player.
	 * If the player has no race, the Default race is returned.
	 * 
	 * @param player to search
	 * @return the {@link RaceContainer} of the player
	 * 
	 * @deprecated use {@link #getRaceOfPlayer(OfflinePlayer)} instead
	 */
	@Deprecated
	public static RaceContainer getRaceOfPlayer(String playerName){
		if(!isRaceSystemActive()) return null;
		return getRaceOfPlayer(Bukkit.getPlayer(playerName));
	}
	
	/**
	 * Returns the Race of a player.
	 * If the player has no race, the Default race is returned.
	 * 
	 * @param player to search
	 * @return the {@link RaceContainer} of the player
	 */
	public static RaceContainer getRaceOfPlayer(Player player){
		if(!isRaceSystemActive()) return null;
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		RaceManager raceManager = plugin.getRaceManager();
		RaceContainer race = racPlayer.getRace();
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
		if(!isRaceSystemActive()) return null;
		
		RaceManager raceManager = plugin.getRaceManager();
		return (RaceContainer) raceManager.getHolderByName(raceName);
	}
	
	
	/**
	* Returns the Race Name of a player.
	* If the player has no Race, the Default Race is returned.
	* 
	* @param player to search
	* 
	* @return the {@link RaceContainer} of the player
	*/
	public static String getRaceNameOfPlayer(Player player){
		if(!isRaceSystemActive()) return null;
		
		RaceContainer container = getRaceOfPlayer(player);
		return (container == null) ? "" : container.getDisplayName();
	}
	
	
	/**
	 * Returns a List of all Race names available
	 * 
	 * @return list of Race names
	 */
	public static List<String> getAllRaceNames(){
		if(!isRaceSystemActive()) return new ArrayList<>();
		return plugin.getRaceManager().getAllHolderNames();
	}
	
	
	/**
	 * Gives the passed Player a Race.
	 * If he already has one, the Race is changed to the new one.
	 * 
	 * Returns true on success, 
	 * false if:
	 *  - player can not be found on Bukkit.getPlayer(player).
	 *  - the new raceName is not found.
	 *  - any internal error occurs.
	 * 
	 * @param player the player that the Race should be changed.
	 * @param className to change to
	 * @return true if worked, false otherwise
	 * 
	 * @deprecated use {@link #addPlayerToRace(OfflinePlayer, String)} instead
	 */
	public static boolean addPlayerToRace(String playerName, String raceName){
		if(!isRaceSystemActive()) return false;
		return addPlayerToRace(Bukkit.getPlayer(playerName), raceName);
	}
	
	
	/**
	 * Returns the Default Race name.
	 * <br>Returns null if races are disabled!
	 * 
	 * @return name of default race.
	 */
	public static String getDefaultRaceName(){
		if(!isRaceSystemActive()) return null;
		return plugin.getRaceManager().getDefaultHolder().getDisplayName();
	}
	
	/**
	 * Gives the passed Player a Race.
	 * If he already has one, the Race is changed to the new one.
	 * 
	 * Returns true on success, 
	 * false if:
	 *  - player can not be found on Bukkit.getPlayer(player).
	 *  - the new raceName is not found.
	 *  - any internal error occurs.
	 * 
	 * @param player the player that the Race should be changed.
	 * @param className to change to
	 * @return true if worked, false otherwise
	 */
	public static boolean addPlayerToRace(Player player, String raceName){
		if(player == null) return false;
		if(!isRaceSystemActive()) return false;
		
		RaceManager manager = plugin.getRaceManager();
		RaceContainer wantedRace = (RaceContainer) manager.getHolderByName(raceName);
		if(wantedRace == null){
			return false;
		}
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		return manager.changePlayerHolder(racPlayer, raceName, true);
	}
	
	
	/**
	 * Returns if the Race system is used or if it is deactivated.
	 * 
	 * @return true if used.
	 */
	public static boolean isRaceSystemActive(){
		return plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces();
	}
	
}
