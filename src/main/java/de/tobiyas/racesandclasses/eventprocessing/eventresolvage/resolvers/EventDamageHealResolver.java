package de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealEvent;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;

public class EventDamageHealResolver {

	/**
	 * Returns the amount of Damage / Heal that is done / taken
	 * 
	 * @param event to parse
	 * 
	 * @return the parsed value
	 */
	public static double getDamageHealFromEvent(Event event) {
		
		if(event instanceof EntityDamageEvent){
			return CompatibilityModifier.EntityDamage.safeGetDamage((EntityDamageEvent) event);
		}
		
		if(event instanceof EntityHealEvent){
			return CompatibilityModifier.EntityHeal.safeGetAmount((EntityHealEvent) event);
		}
		
		return 0;
	}
	
	
	/**
	 * Returns the damage cause from the event
	 * 
	 * @param event the event to check
	 * 
	 * @return the cause or NULL
	 */
	public static DamageCause getDamageCauseFromEvent(Event event){
		if(event instanceof EntityDamageEvent){
			return ((EntityDamageEvent) event).getCause();
		}
		
		return null;
	}
	
	/**
	 * Returns an Regain reason from the Event passed
	 * 
	 * @param event to check
	 * 
	 * @return the Heal cause or null
	 */
	public static RegainReason getRegainReasonFromEvent(Event event){
		if(event instanceof EntityRegainHealthEvent){
			return ((EntityRegainHealthEvent) event).getRegainReason();
		}
		
		return null;
	}

}
