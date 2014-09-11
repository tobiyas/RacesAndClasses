package de.tobiyas.racesandclasses.APIs;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.entitystatusmanager.hots.Hot;

public class HoTAPI {

	
	/**
	 * Adds a hot to the entity.
	 * 
	 * @param entity to add to
	 * @param hot to add
	 * 
	 * @return true if worked, false if not.
	 */
	public static boolean addHotToEntity(LivingEntity entity, Hot hot){
		if(entity == null || !entity.isValid()) return false;
		return RacesAndClasses.getPlugin().getHotsManager().addHot(entity,hot);
	}
	
	/**
	 * gets all hots an entity.
	 * 
	 * @param entity to get
	 * 
	 * @return The set of hots. Empty if none present.
	 */
	public static Set<Hot> getHotsOfEntity(LivingEntity entity){
		if(entity == null || !entity.isValid()) return new HashSet<Hot>();
		return RacesAndClasses.getPlugin().getHotsManager().get(entity).getAllActiveHots();
	}
	

	/**
	 * Clears all Hots of the passed Entity.
	 * 
	 * @param entity to clear
	 */
	public static void clearHots(LivingEntity entity){
		if(entity == null || !entity.isValid()) return;
		RacesAndClasses.getPlugin().getHotsManager().get(entity).reset();
	}

	
	/**
	 * Clears a Hots with the passed ID off the passed Entity.
	 * 
	 * @param entity to clear
	 * @param id the ID to remove
	 */
	public static void clearHot(LivingEntity entity, String id){
		if(entity == null || !entity.isValid() || id == null || id.isEmpty()) return;
		RacesAndClasses.getPlugin().getHotsManager().get(entity).clearHot(id);
	}
	
	/**
	 * Clears the passed Hot from the Entity.
	 * 
	 * @param entity to clear
	 * @param hot to remove
	 */
	public static void clearHot(LivingEntity entity, Hot hot){
		if(entity == null || !entity.isValid() || hot == null) return;
		RacesAndClasses.getPlugin().getHotsManager().get(entity).clearHot(hot);
	}
}
