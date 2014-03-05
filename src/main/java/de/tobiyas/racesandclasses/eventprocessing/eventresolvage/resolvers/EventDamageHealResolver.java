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
package de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper.RegainResource;
import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.mana.ManaRegenerationEvent;
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


	/**
	 * Analyzes the event and passes the Resource gained back.
	 * This can be null if none fit.
	 * 
	 * @param event that was passed.
	 * 
	 * @return the analyzed {@link RegainResource}
	 */
	public static RegainResource getRegainResource(Event event) {
		if(event instanceof ManaRegenerationEvent){
			return RegainResource.MANA;
		}
		
		if(event instanceof FoodLevelChangeEvent){
			return RegainResource.HUNGER;
		}
		
		if(event instanceof EntityRegainHealthEvent){
			return RegainResource.HEALTH;
		}
		
		return null;
	}

}
