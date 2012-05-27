/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races.listeners;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;


public class Listener_Entity implements Listener {
	private Races plugin;

	public Listener_Entity(){
		plugin = Races.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		TraitEventManager.getTraitEventManager().modifyEvent(event);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event){
		// TODO handle that event
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event){
		TraitEventManager.getTraitEventManager().modifyEvent(event);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		TraitEventManager.getTraitEventManager().modifyEvent(event);
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event){
		// TODO handle that event
	}


}
