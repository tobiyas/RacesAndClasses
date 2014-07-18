package de.tobiyas.racesandclasses.util.damage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class PreEntityDamageEvent extends EntityDamageByEntityEvent {

	
	static{
		new ListenerClass();
	}
	
	private static class ListenerClass implements Listener{
		public ListenerClass() {
			Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
		}
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void checkCancle(EntityDamageByEntityEvent event){
			if(event instanceof PreEntityDamageEvent){
				((PreEntityDamageEvent)event).realCancle = event.isCancelled();
				event.setCancelled(true);
			}
		}
	}
	
	
	
	private boolean realCancle = false;
	
	
	
	@SuppressWarnings("deprecation")
	public PreEntityDamageEvent(Entity damager, Entity damagee,
			DamageCause cause, double damage) {
		super(damager, damagee, cause, damage);
	}

	
	
	
	public boolean isRealCancle() {
		return realCancle;
	}
	
	
	
	/**
	 * Fires in intern event which is cancled!
	 * <br>Returns 0 if cancled!
	 * 
	 * @param damager that did damage
	 * @param damagee that got damage
	 * @param cause that was done.
	 * @param damage that was done.
	 * 
	 * @return the real damage to do.
	 */
	public static double getRealDamage(Entity damager, Entity damagee, DamageCause cause, double damage){
		PreEntityDamageEvent event = new PreEntityDamageEvent(damager, damagee, cause, damage);
		RacesAndClasses.getPlugin().fireEventToBukkit(event);
		if(event.isRealCancle()) return 0;
		return event.getDamage();
	}
}
