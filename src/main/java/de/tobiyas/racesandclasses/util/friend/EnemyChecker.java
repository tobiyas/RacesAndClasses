package de.tobiyas.racesandclasses.util.friend;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.EntityDamageByEntity;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.Shooter;

public class EnemyChecker {

	
	/**
	 * Checks if the these 2 are Allies.
	 * 
	 * @param attacker to check
 	 * @param receiver to check
 	 * 
	 * @return true if they are allies.
	 */
	public static boolean areAllies(Entity attacker, Entity receiver){
		if(attacker == null || receiver == null) return false;
		if(attacker instanceof Projectile){
			attacker = Shooter.getShooter((Projectile) attacker);
			if(attacker == null) return false;
		}
		
		
		EntityDamageByEntityEvent event = EntityDamageByEntity.safeCreateEvent(attacker, receiver, DamageCause.CUSTOM, 0.1);
		RacesAndClasses.getPlugin().fireEventToBukkit(event);
		
		//only check cancles!
		return event.isCancelled();
	}
	
	/**
	 * Checks if the these 2 are enemies.
	 * 
	 * @param attacker to check
	 * @param receiver to check
	 * 
	 * @return true if they are enemies.
	 */
	public static boolean areEnemies(Entity attacker, Entity receiver){
		return !areAllies(attacker, receiver);
	}
	
	/**
	 * Checks if the these 2 are enemies.
	 * 
	 * @param attacker to check
	 * @param receiver to check
	 * 
	 * @return true if they are enemies.
	 */
	public static boolean isApplyable(Entity attacker, Entity receiver, TargetType type){
		switch(type){
			case ENEMY:  return areEnemies(attacker, receiver);
			case FRIEND: return areAllies(attacker, receiver);
			case ALL: return true;
			
			default: return false;
		}
	}
}
