/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.entitystatusmanager.dot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.Vollotile;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class DotManager implements Listener {

	/**
	 * The plugin to call stuff on.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * The taskID for the Bukkit task
	 */
	private BukkitTask bukkitTask;
	
	/**
	 * The Poison Map for the Plugin
	 */
	private final Map<LivingEntity, Collection<DotContainer>> dotDamageMap = new ConcurrentHashMap<LivingEntity, Collection<DotContainer>>(); 
	
	
	/**
	 * Creates the PoisonManager
	 */
	public DotManager() {
		plugin = RacesAndClasses.getPlugin();
		plugin.registerEvents(this);
	}
	
	
	/**
	 * Inits the Manager
	 */
	public void init(){
		dotDamageMap.clear();
		
		if(bukkitTask == null){
			bukkitTask = new DebugBukkitRunnable("PoisonTicker") {
				@Override
				public void runIntern() {
					Iterator<Map.Entry<LivingEntity, Collection<DotContainer>>> poisonTickerIterator = dotDamageMap.entrySet().iterator();
					while(poisonTickerIterator.hasNext()){
						Map.Entry<LivingEntity, Collection<DotContainer>> entry = poisonTickerIterator.next();
						LivingEntity entity = entry.getKey();
						Iterator<DotContainer> itDots = entry.getValue().iterator();
						
						while(itDots.hasNext()){
							DotContainer dotContainer = itDots.next();
							if(entity.isDead() || !entity.isValid()){
								poisonTickerIterator.remove();
								break;
							}
							
							int newTicks = dotContainer.getTicks() - 1;
							dotContainer.setTicks(newTicks);
							
							if(newTicks % dotContainer.getDamageEveryTicks() == 0){
								if(damageEntity(entity, dotContainer)) playDotParticleEffects(entity, dotContainer);
							}
							
							//Ticks is done -> End!
							if(newTicks < 0) itDots.remove();
						}
					}
				}
				
			}.runTaskTimer(plugin, 5, 5);
		}
	}
	
	
	/**
	 * Plays poison particle effects on the Entity.
	 * @param entity to use the location on.
	 */
	protected void playDotParticleEffects(LivingEntity entity, DotContainer option) {
		Location loc = entity.getLocation().clone().add(0,1,0);
		
		ParticleContainer effect = option.getDamageType().getParticleContainer();
		Vollotile.get().sendOwnParticleEffectToAll(effect, loc);
	}
	


	/**
	 * Damages an Entity for the Options passed
	 * 
	 * @param entity to damage
	 * @param options to apply
	 */
	protected boolean damageEntity(LivingEntity entity, DotContainer options) {
		double damagePerTick = options.getDamageOnTick();
		EntityDamageEvent event = CompatibilityModifier.EntityDamage.safeCreateEvent(entity, options.getDamageType().getCause(), damagePerTick);
		plugin.fireEventToBukkit(event);
		
		RaCPlayer damager = options.getDamager();
		Player realDamager = damager.isOnline() ? damager.getRealPlayer() : null;
		if(!event.isCancelled()){
			double newDamage = CompatibilityModifier.EntityDamage.safeGetDamage(event);
			CompatibilityModifier.LivingEntity.safeDamageEntityByEntity(entity, realDamager, newDamage);
		}
		
		return !event.isCancelled();
	}



	/**
	 * Removes all Stuff not needed any more.
	 */
	public void deinit(){
		if(bukkitTask != null){
			bukkitTask.cancel();
			bukkitTask = null;
		}
	}
	
	
	
	/**
	 * Dots an Entity.
	 * 
	 * @param entity to Dot
	 * @param builder the builder for the Dot.
	 * 
	 * @return if it worked.
	 */
	public boolean dotEntity(LivingEntity entity, DotBuilder builder){
		if(entity == null || !builder.valid()) return false;
		
		//Build the container and check if we already have something with that name:
		DotContainer newContainer = builder.build();
		Collection<DotContainer> dotsOfEntity = dotDamageMap.get(entity);
		
		//Add if not present!
		if(dotsOfEntity == null) { dotsOfEntity = new HashSet<>(); dotDamageMap.put(entity, dotsOfEntity); }
		
		//New is always better! :D
		for(Iterator<DotContainer> it = dotsOfEntity.iterator(); it.hasNext();){
			DotContainer next = it.next();
			if(next.getName().equalsIgnoreCase(newContainer.getName())) it.remove();
		}
		
		//Finally add!
		dotsOfEntity.add(newContainer);
		return true;
	}

	/**
	 * Removes all Poison effects from the entity.
	 * 
	 * @param entity to un poison.
	 * @return true if worked, false otherwise.
	 */
	public int removeAllDots(LivingEntity entity){
		if(entity == null) return 0;
		
		Collection<DotContainer> removed = dotDamageMap.remove(entity);
		return removed == null ? 0 : removed.size();
	}
	
	/**
	 * Removes all dots with that name.
	 * 
	 * @param entity to clear.
	 * @param name to remove.
	 * 
	 * @return true if worked, false otherwise.
	 */
	public int removeAllDots(LivingEntity entity, String name){
		if(entity == null) return 0;

		Collection<DotContainer> containers = dotDamageMap.get(entity);
		if(containers == null) return 0;
		
		//Remove every dot with that name.
		int removed = 0;
		for(Iterator<DotContainer> it = containers.iterator(); it.hasNext(); ){
			if(it.next().getName().equalsIgnoreCase(name)) { it.remove(); removed++; }
		}
		
		return removed;
	}
	
	/**
	 * Removes all dots with that name.
	 * 
	 * @param entity to clear.
	 * @param name to remove.
	 * 
	 * @return true if worked, false otherwise.
	 */
	public int removeAllDots(LivingEntity entity, DamageType type){
		if(entity == null) return 0;
		
		Collection<DotContainer> containers = dotDamageMap.get(entity);
		if(containers == null) return 0;
		
		//Remove every dot with that name.
		int removed = 0;
		for(Iterator<DotContainer> it = containers.iterator(); it.hasNext(); ){
			if(it.next().getDamageType() == type) { it.remove(); removed++; }
		}
		
		return removed;
	}
	
	
	/**
	 * Gets all Dots of the entity.
	 * <br>This passes a copy!
	 * 
	 * @param entity to use.
	 * @return all Dots.
	 */
	public Collection<DotContainer> getAllDotsOnEntity(LivingEntity entity){
		if(entity == null) return new ArrayList<>(0); 
		
		Collection<DotContainer> dots = dotDamageMap.get(entity);
		return dots == null ? new ArrayList<DotContainer>(0) : dots;
	}
	

	/**
	 * Gets all Dots of the entity of a type.
	 * <br>This passes a copy!
	 * 
	 * @param entity to use.
	 * @param type type to filter.
	 * @return all Dots of the passed type.
	 */
	public Collection<DotContainer> getAllDotsOnEntity(LivingEntity entity, DamageType type) {
		if(entity == null || type == null) return new ArrayList<>(0); 
		
		Collection<DotContainer> dots = getAllDotsOnEntity(entity);
		for(Iterator<DotContainer> it = dots.iterator(); it.hasNext();){
			if(it.next().getDamageType() != type) it.remove();
		}
		
		return dots;
	}
	
	
	//Simple functions to clean up:
	@EventHandler public void onEntityDied(EntityDeathEvent event){ dotDamageMap.remove(event.getEntity()); }	
	@EventHandler public void onPlayerDied(PlayerDeathEvent event) { onEntityDied(event); }
	
}
