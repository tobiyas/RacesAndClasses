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

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.entitystatusmanager.poison.PoisonManager;

public class PoisonAPI {

	/**
	 * Return the Poison Manger for the Plugin
	 * 
	 * @return poisonManger
	 */
	private static PoisonManager getPoisonManager(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		return plugin.getPoisonManager();
	}
	
	/**
	 * Poisons an Entity over time.
	 * <br>Time is in ticks.
	 * 
	 * @param entity to poison
	 * @param seconds to poison
	 * @param totalDamage to poison
	 * 
	 * @return true if poison worked, false otherwise.
	 */
	public static boolean poisonEntityForTicks(LivingEntity entity, int ticks, double totalDamage){
		if(entity == null || ticks < 0 || totalDamage < 0){
			return false;
		}
		
		return getPoisonManager().poisonEntity(entity, ticks, totalDamage, 30, DamageCause.POISON);
	}
	
	/**
	 * Poisons an Entity over time.
	 * <br>Time is in Seconds
	 * 
	 * @param entity to poison
	 * @param seconds to poison
	 * @param totalDamage to poison
	 * 
	 * @return true if poison worked, false otherwise.
	 */
	public static boolean poisonEntityForSeconds(LivingEntity entity, int seconds, double totalDamage){
		if(entity == null || seconds < 0 || totalDamage < 0){
			return false;
		}
		
		return getPoisonManager().poisonEntity(entity, seconds * 20, totalDamage, 20, DamageCause.POISON);
	}
	
	
	/**
	 * Removes all Poisons from an Entity.
	 * 
	 * @param entity to remove from
	 * 
	 * @return true if worked, false otherwise.
	 */
	public static boolean removePoison(LivingEntity entity){
		if(entity == null) return false;
		return getPoisonManager().removePoisons(entity);
	}
	
	/**
	 * Returns the remaining damage on Poison for the Entity.
	 * <br>Returns 0 if not poisoned.
	 * 
	 * @param entity to check
	 * 
	 * @return the remaining damage.
	 */
	public static double getRemainingPoisonDamage(LivingEntity entity){
		if(entity == null) return 0;
		return getPoisonManager().getRestPoisonDamage(entity);
	}
	
	
	/**
	 * Returns the ticks the Entity is still poisoned.
	 * 
	 * @param entity to check
	 * 
	 * @return ticks left.
	 */
	public static int getRemainingTicks(LivingEntity entity){
		if(entity == null) return -1;
		return getPoisonManager().getRestPoisonTime(entity);
	}

	/**
	 * Returns the seconds the Entity is still poisoned.
	 * 
	 * @param entity to check
	 * 
	 * @return seconds left.
	 */
	public static int getRemainingSeconds(LivingEntity entity){
		if(entity == null) return -1;
		return getPoisonManager().getRestPoisonTime(entity) / 20;
	}
}
