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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.util.Vector;

public class EntityKnockbackEvent extends EntityEvent implements Cancellable {

	private Vector knockbackVector;
	private boolean isCancled;
	
	
	public EntityKnockbackEvent(Entity target, Entity source) {
		super(target);
		this.knockbackVector = calcVector(target, source);
		isCancled = false;
	}
	
	public EntityKnockbackEvent(Entity what, Vector knockbackVector) {
		super(what);
		this.knockbackVector = knockbackVector;
		isCancled = false;
	}

	@Override
	public boolean isCancelled() {
		return isCancled;
	}

	@Override
	public void setCancelled(boolean cancled) {
		isCancled = cancled;
	}

	@Override
	public HandlerList getHandlers() {
		return null;
	}
	
	private Vector calcVector(Entity target, Entity source){
		Location locTarget = target.getLocation();
		Location locDamager = source.getLocation();
		
		Location result = locTarget.subtract(locDamager);
		Vector knockBack = result.toVector();
		knockBack.normalize();
		return knockBack;
	}
	
	public void multiplyKnockBack(double multi){
		knockbackVector.multiply(multi);
	}
	
	public void addKnockBack(Vector vector){
		knockbackVector.add(vector);
	}
	
	public Vector getKnockback(){
		return knockbackVector;
	}
	
	public void setKnockback(Vector vector){
		this.knockbackVector = vector;
	}
	
	public void setKnockback(Entity target, Entity source){
		knockbackVector = calcVector(target, source);
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof EntityKnockbackEvent))
			return false;
		
		EntityKnockbackEvent realEvent = (EntityKnockbackEvent) obj;
		
		if(this.knockbackVector != realEvent.getKnockback())
			return false;
		
		if(this.entity != realEvent.getEntity())
			return false;
		
		if(this.isCancled != realEvent.isCancelled())
			return false;
		
		return true;
	}

}
