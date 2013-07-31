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
