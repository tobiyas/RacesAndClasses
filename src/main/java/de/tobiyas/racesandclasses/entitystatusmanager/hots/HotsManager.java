package de.tobiyas.racesandclasses.entitystatusmanager.hots;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class HotsManager implements Listener{

	private final Map<LivingEntity,HotContainer> hotContainers = new HashMap<LivingEntity, HotContainer>();
	private final RacesAndClasses plugin;
	
	public HotsManager() {
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		//Scheduler for Hots.
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				Iterator<Entry<LivingEntity,HotContainer>> it = hotContainers.entrySet().iterator();
				while(it.hasNext()){
					Entry<LivingEntity,HotContainer> entry = it.next();
					HotContainer container = entry.getValue();
					if(!container.stillValid()) {
						it.remove();
						continue;
					}
					
					container.tick();
				}
			}
		}, 10, 10);
	}
	
	
	/**
	 * Returns the Hot container for that entity.
	 * 
	 * @param entity to get.
	 * @return
	 */
	public HotContainer get(LivingEntity entity){
		if(!hotContainers.containsKey(entity)) hotContainers.put(entity, new HotContainer(entity));
		return hotContainers.get(entity);
	}
	
	
	/**
	 * adds the Hot for that entity.
	 * 
	 * @param entity to add
	 * @param hot to add
	 * 
	 * @return
	 */
	public boolean addHot(LivingEntity entity, Hot hot){
		HotContainer container = get(entity);
		return container.addHot(hot);
	}
	
	
	
	
	
	//////////////////////////
	///Event handler stuff////
	//////////////////////////
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent event){
		hotContainers.remove(event.getPlayer());
	}
	
	
	@EventHandler
	public void playerDies(PlayerDeathEvent event){
		HotContainer container = get(event.getEntity());
		container.reset();
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void entityDies(EntityDeathEvent event){
		hotContainers.remove(event.getEntity());
	}
	
	

}
