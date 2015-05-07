package de.tobiyas.racesandclasses.APIs;

import java.util.Set;
import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.entitystatusmanager.debuff.Debuff;

public class DebuffAPI {

	
	/**
	 * Returns all Debuffs on that entity.
	 * 
	 * @param entity to check
	 * 
	 * @return all Debuffs on that entity.
	 */
	public static Set<Debuff> getAllDebuffs(UUID entity){
		return RacesAndClasses.getPlugin().getDebuffManager().getAllDebuffs(entity);
	}
	
	
	/**
	 * Registers a new Debuff.
	 * 
	 * @param debuff to add
	 */
	public static void registerNewDebuff(Debuff debuff){
		RacesAndClasses.getPlugin().getDebuffManager().register(debuff);
	}
	
	
	
	/**
	 * Cancles the Debuff passed.
	 * 
	 * @param debuff to cancle
	 */
	public static void cancelDebuff(Debuff debuff){
		RacesAndClasses.getPlugin().getDebuffManager().cancel(debuff);
	}
}
