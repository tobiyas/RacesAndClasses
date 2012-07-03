package de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class EntityDamageByEntityDoubleEvent extends EntityDamageDoubleEvent{
	
	private Entity causer;

	public EntityDamageByEntityDoubleEvent(Entity damager, Entity damagee,
			DamageCause cause, double damage) {
		super(damagee, cause, damage);
		
		this.causer = damager;
		this.damageValue = damage;
	}
	
	public EntityDamageByEntityDoubleEvent(Entity damager, Entity damagee,
			DamageCause cause, double damage, boolean raceCreated) {
		super(damagee, cause, damage, raceCreated);
		
		this.causer = damager;
		this.damageValue = damage;
	}
	
	public EntityDamageByEntityDoubleEvent(EntityDamageByEntityEvent event){
		super(event.getEntity(), event.getCause(), event.getDamage());
		
		this.causer = event.getDamager();
		this.damageValue = event.getDamage();
	}
	
	public Entity getDamager(){
		return causer;
	}

}
