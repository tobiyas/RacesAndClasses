package de.tobiyas.racesandclasses.infight;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class InFightManager implements Listener {

	/**
	 * All IDs that are in Fight.
	 */
	private final Map<UUID,Long> inFight = new HashMap<UUID,Long>();
	
	/**
	 * The Maximal time in Fight possible.
	 */
	private long timeInFight = 1000 * 10;
	
	
	public InFightManager() {
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
	}
	
	public void reload(){
		//TODO replace with config.
		timeInFight = 1000 * 10;
		
		inFight.clear();
		
		OutOfFightHealer.kill();
		OutOfFightHealer.launch();
	}
	
	
	/**
	 * If the Entity with the following ID is in Fight.
	 * 
	 * @param id to check
	 * @return true if in fight.
	 */
	public boolean isInFight(UUID id){
		if(!inFight.containsKey(id)) return false;
		
		long lastFightTime = inFight.get(id);
		long now = System.currentTimeMillis();
		
		return (now - lastFightTime) < timeInFight;
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerGotDamage(EntityDamageEvent event){
		if(event.isCancelled()) return;
		
		if(event.getEntity().getType() == EntityType.PLAYER){
			inFight.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerDamaged(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		
		if(event.getDamager().getType() == EntityType.PLAYER){
			inFight.put(event.getDamager().getUniqueId(), System.currentTimeMillis());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void gotAggro(EntityTargetEvent event){
		if(event.isCancelled()) return;
		if(event.getTarget() == null) return;
		
		if(event.getTarget().getType() == EntityType.PLAYER){
			inFight.put(event.getTarget().getUniqueId(), System.currentTimeMillis());
		}
	}
	
}
