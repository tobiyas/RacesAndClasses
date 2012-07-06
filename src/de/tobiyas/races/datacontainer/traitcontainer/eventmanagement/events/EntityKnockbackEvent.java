package de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events;

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

}
