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

import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;

public class EntityHealOtherEntityEvent extends EntityHealEvent {

	private Entity source;


	/**
	 * This event has an additional source for Healing
	 * 
	 * @param target
	 * @param amount
	 * @param reason
	 * @param source
	 */
	public EntityHealOtherEntityEvent(Entity target, double amount,
			RegainReason reason, Entity source) {
		super(target, CertainVersionChecker.isAbove1_6() ? amount : (int) amount, reason);
		
		this.source = source;
	}


	/**
	 * Gets the entity healing
	 * @return
	 */
	public Entity getSource() {
		return source;
	}

	/**
	 * Changes the Entity healing
	 * @param source
	 */
	public void setSource(Entity source) {
		this.source = source;
	}

}
