package de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class EntityHealEvent extends EntityRegainHealthEvent {

	private double amount;
	private Entity source;
	
	public EntityHealEvent(Entity target, double amount, Entity source, RegainReason reason) {
		super(target, (int) amount, reason);
		this.amount = amount;
		this.source = source;
	}
	
	public EntityHealEvent(Entity target, double amount, RegainReason reason) {
		super(target, (int) amount, reason);
		this.amount = amount;
		this.source = null;
	}
	
	public EntityHealEvent(EntityRegainHealthEvent event){
		super(event.getEntity(), event.getAmount(), event.getRegainReason());
		this.amount = event.getAmount();
		this.source = null;
	}
	
	public double getDoubleValueAmount(){
		return amount;
	}
	
	public void setDoubleValueAmount(double amount){
		this.amount = amount;
		this.setAmount((int) amount);
	}
	
	public Entity getSource(){
		return source;
	}
}
