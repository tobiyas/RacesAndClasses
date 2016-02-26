package de.tobiyas.racesandclasses.APIs;

import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class SilenceAndKickAPI {
	
	
	private static boolean racEnabled(){
		return RacesAndClasses.getPlugin() != null;
	}
	
	
	/**
	 * Kicks the Entity that is channeling.
	 * @param entityID that is channeling.
	 * @param timeInMs to kick.
	 * @return true kicked something.
	 */
	public static boolean kickChanneling(UUID entityID, long timeInMs){
		if(!racEnabled()) return false;
		return RacesAndClasses.getPlugin().getSilenceAndKickManager().kickChanneling(entityID, timeInMs);
	}
	
	
	/**
	 * Silences the Entity.
	 * @param entityID that is channeling.
	 * @param timeInMs to kick.
	 * @return true kicked something.
	 */
	public static boolean silence(UUID entityID, long timeInMs){
		if(!racEnabled()) return false;
		return RacesAndClasses.getPlugin().getSilenceAndKickManager().silence(entityID, timeInMs);
	}	
	
	
	
	/**
	 * If the Entity is silenced.
	 * @param entityID to check
	 * @return true if is silenced.
	 */
	public static boolean isSilenced(UUID entityID){
		if(!racEnabled()) return false;
		return RacesAndClasses.getPlugin().getSilenceAndKickManager().isSilenced(entityID);
	}
	
	
	
	/**
	 * Returns the rest time the entity is silenced (in MiliSeconds).
	 * @param entityID to get.
	 */
	public static long getRestSilenceTime(UUID entityID){
		if(!racEnabled()) return 0;
		return RacesAndClasses.getPlugin().getSilenceAndKickManager().getRestSilenceTime(entityID);
	}
	
	
}
