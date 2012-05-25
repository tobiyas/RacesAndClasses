/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races.listeners;


import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.ReturnFilter;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;


public class Listener_Entity implements Listener {
	private Races plugin;

	public Listener_Entity(){
		plugin = Races.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		ArrayList<ReturnFilter> filters = TraitEventManager.getTraitEventManager().modifyEvent(event);
		for(ReturnFilter filter : filters){
			switch(filter.getOperation()){
				case 0: event.setDamage((Integer) filter.getNewValue()); break;
				default: continue;
			}
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event){
		// TODO handle that event
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event){
		
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		ArrayList<ReturnFilter> filters = TraitEventManager.getTraitEventManager().modifyEvent(event);
		for(ReturnFilter filter : filters){
			switch(filter.getOperation()){
				case 0: event.setDamage((Integer) filter.getNewValue()); break;
				default: continue;
			}
		}
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event){
		// TODO handle that event
	}


}
