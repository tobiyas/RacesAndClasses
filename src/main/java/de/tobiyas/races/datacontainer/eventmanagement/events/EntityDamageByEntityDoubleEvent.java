package de.tobiyas.races.datacontainer.eventmanagement.events;

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
	
	@Override
	public EntityDamageByEntityEvent generateBukkitEvent(){
		Entity damager = causer;
		Entity damagee = getEntity();
		
		DamageCause cause = getCause();
		int damage = (int) Math.ceil(damageValue);
		
		EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, damagee, cause, damage);
		return event;
	}

}
