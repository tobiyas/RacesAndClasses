package de.tobiyas.races.datacontainer.eventmanagement.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageDoubleEvent extends EntityDamageEvent{

	protected double damageValue;
	protected boolean racesCreated;
	
	public EntityDamageDoubleEvent(Entity damagee, DamageCause cause, double damageValue) {
		super(damagee, cause, (int) Math.ceil(damageValue));
		this.damageValue = damageValue;
		this.racesCreated = false;
		setCancelled(false);
	}
	
	public EntityDamageDoubleEvent(Entity damagee, DamageCause cause, double damageValue, boolean racesCreated) {
		super(damagee, cause, (int) Math.ceil(damageValue));
		this.damageValue = damageValue;
		this.racesCreated = racesCreated;
		setCancelled(false);
	}
	
	public EntityDamageDoubleEvent(EntityDamageEvent event) {
		super(event.getEntity(), event.getCause(), (int) Math.ceil(event.getDamage()));
		this.damageValue = event.getDamage();
		this.racesCreated = false;
	}

	public double getDoubleValueDamage(){
		return damageValue;
	}
	
	public void setDoubleValueDamage(double damageValue){
		this.damageValue = damageValue;
	}
	
	public boolean isRacesCreated(){
		return racesCreated;
	}
	
	public void setRacesCreated(boolean racesCreated){
		this.racesCreated = racesCreated;
	}
	
	public EntityDamageEvent generateBukkitEvent(){
		return this;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof EntityDamageDoubleEvent))
			return false;
		
		EntityDamageDoubleEvent realEvent = (EntityDamageDoubleEvent) obj;
		
		if(this.damageValue != realEvent.getDoubleValueDamage())
			return false;
		
		if(this.entity != realEvent.getEntity())
			return false;
		
		if(this.racesCreated != realEvent.isRacesCreated())
			return false;
		
		return true;
	}
}
