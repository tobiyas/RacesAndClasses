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
package de.tobiyas.racesandclasses.eventprocessing.events.entitydamage;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class EntityHealEvent extends EntityRegainHealthEvent {

	
	public EntityHealEvent(Entity target, double amount, RegainReason reason) {
		super(target, amount, reason);
	}
	
	
	/**
	 * Compatibility to old Bukkit version pre 1.6
	 * 
	 * @param target
	 * @param amount
	 * @param reason
	 * 
	 * @deprecated prefer other constructor
	 */
	public EntityHealEvent(Entity target, int amount, RegainReason reason) {
		super(target, amount, reason);
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof EntityHealEvent))
			return false;
		
		EntityHealEvent realEvent = (EntityHealEvent) obj;
		
		if(this.entity != realEvent.getEntity())
			return false;
		
		return super.equals(obj);
	}
}
