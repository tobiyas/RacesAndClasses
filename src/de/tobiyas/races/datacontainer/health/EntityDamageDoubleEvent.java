package de.tobiyas.races.datacontainer.health;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageDoubleEvent extends EntityDamageEvent{

	private double damageValue;
	
	public EntityDamageDoubleEvent(Entity damagee, DamageCause cause, double damageValue) {
		super(damagee, cause, (int) Math.ceil(damageValue));
		this.damageValue = damageValue;
		setCancelled(false);
	}
	
	public EntityDamageDoubleEvent(EntityDamageEvent event) {
		super(event.getEntity(), event.getCause(), (int) Math.ceil(event.getDamage()));
		setCancelled(false);
	}

	public double getDoubleValueDamage(){
		return damageValue;
	}
	
	public void setDoubleValueDamage(double damageValue){
		this.damageValue = damageValue;
	}
}
