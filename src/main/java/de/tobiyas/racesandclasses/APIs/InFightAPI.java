package de.tobiyas.racesandclasses.APIs;

import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class InFightAPI {

	/**
	 * If the Player with the ID is in Fight.
	 * 
	 * @param id to check
	 * @return true if in fight, false if not.
	 */
	public static boolean isInFight(UUID id){
		if(id == null) return false;
		return RacesAndClasses.getPlugin().getInFightManager().isInFight(id);
	}
	
	
	/**
	 * If the Player with the ID is in Fight.
	 * 
	 * @param player to check
	 * @return true if in fight, false if not.
	 */
	public static boolean isInFight(RaCPlayer player){
		if(player == null) return false;
		return player.isOnline() && RacesAndClasses.getPlugin().getInFightManager().isInFight(player.getUniqueId());
	}
	
}
