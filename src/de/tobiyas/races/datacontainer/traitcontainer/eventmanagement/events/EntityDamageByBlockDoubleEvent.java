package de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;

public class EntityDamageByBlockDoubleEvent extends EntityDamageDoubleEvent {

	private Block causer;

	public EntityDamageByBlockDoubleEvent(Block damager, Entity damagee,
			DamageCause cause, double damage) {
		super(damagee, cause, (int) damage);
		
		this.causer = damager;
		this.damageValue = damage;
	}
	
	public EntityDamageByBlockDoubleEvent(EntityDamageByBlockEvent event){
		super(event.getEntity(), event.getCause(), event.getDamage());
		
		this.causer = event.getDamager();
		this.damageValue = event.getDamage();
	}
	
	
	public Block getDamager(){
		return causer;
	}
}
