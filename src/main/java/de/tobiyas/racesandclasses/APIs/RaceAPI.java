package de.tobiyas.racesandclasses.APIs;

import java.util.List;

import org.bukkit.Bukkit;

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
	 * @param playerName to search
	 * @return the {@link RaceContainer} of the player
	 */
	public static RaceContainer getRaceOfPlayer(String playerName){
		RaceManager raceManager = plugin.getRaceManager();
		RaceContainer race = (RaceContainer) raceManager.getHolderOfPlayer(playerName);
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
	 *  - playerName can not be found on Bukkit.getPlayer(playerName).
	 *  - the new raceName is not found.
	 *  - any internal error occurs.
	 * 
	 * @param playerName the player that the Race should be changed.
	 * @param className to change to
	 * @return true if worked, false otherwise
	 */
	public static boolean addPlayerToRace(String playerName, String raceName){
		if(Bukkit.getPlayer(playerName) == null) return false;
		
		RaceManager manager = plugin.getRaceManager();
		RaceContainer wantedRace = (RaceContainer) manager.getHolderByName(raceName);
		if(wantedRace == null){
			return false;
		}
		
		return manager.changePlayerHolder(playerName, raceName, true);
	}
}
