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
