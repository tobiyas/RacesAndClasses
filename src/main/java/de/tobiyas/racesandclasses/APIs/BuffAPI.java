package de.tobiyas.racesandclasses.APIs;

import java.util.Set;
import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.entitystatusmanager.buffs.BuffContainer;

public class BuffAPI {

	
	/**
	 * Adds a buff to the Buff-List.
	 * <br>Pure Cosmetic. No effects!
	 * 
	 * @param id to add for.
	 * @param name to add
	 * @param endTime to set.
	 */
	public static void addBuff(UUID id, String name, long endTime){
		RacesAndClasses.getPlugin().getBuffManager().addBuff(id, name, endTime);
	}
	
	/**
	 * Removes a buff from the Buff-List.
	 * <br>Pure Cosmetic. No effects!
	 * 
	 * @param id to add for.
	 * @param name to add.
	 */
	public static void removeBuff(UUID id, String name){
		RacesAndClasses.getPlugin().getBuffManager().removedOrUsedBuff(id, name);
	}
	
	
	/**
	 * Gets all Buffs of the ID.
	 * <br>Pure Cosmetic. No effects!
	 * 
	 * @param id to add for.
	 */
	public static Set<BuffContainer> getBuffs(UUID id){
		return RacesAndClasses.getPlugin().getBuffManager().get(id);
	}
	
}
