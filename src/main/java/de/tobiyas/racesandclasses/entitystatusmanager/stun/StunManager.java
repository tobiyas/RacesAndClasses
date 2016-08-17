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
package de.tobiyas.racesandclasses.entitystatusmanager.stun;

import static de.tobiyas.racesandclasses.translation.languages.Keys.stun_ended;
import static de.tobiyas.racesandclasses.translation.languages.Keys.stun_message;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.configuration.statusimun.StatusEffect;
import de.tobiyas.racesandclasses.eventprocessing.events.stun.PlayerStunnedEvent;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.util.schedule.DebugBukkitRunnable;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class StunManager {

	private final int TICK_TIME = 1;
	
	/**
	 * The Map containing all Stuns.
	 */
	private Map<Entity, StunOptions> stunTimes = new ConcurrentHashMap<Entity, StunOptions>();
	
	/**
	 * The Map of StunImmun
	 */
	private Map<Entity, StunReduceContainer> stunReduces = new ConcurrentHashMap<Entity, StunReduceContainer>();
	
	/**
	 * The Bukkit taskID for the StunDecreaser.
	 */
	private BukkitTask bukkitTask = null;
	
	/**
	 * The Plugin to call stuff on.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * Creates the Stun Manager with all stuff.
	 */
	public StunManager() {
		plugin = RacesAndClasses.getPlugin();
	}
	
	/**
	 * Inits the StunManager and starts the Decreasing task.
	 */
	public void init(){
		if(bukkitTask == null){
			bukkitTask = new DebugBukkitRunnable("StunManagerTick"){
				
				@Override
				protected void runIntern() {
					Iterator<Map.Entry<Entity, StunOptions>> stunIt = stunTimes.entrySet().iterator();
					while (stunIt.hasNext()){
						Map.Entry<Entity, StunOptions> entry = stunIt.next();
						StunOptions options = entry.getValue();
						Entity entity = entry.getKey();
						
						int newValue = options.timeRemaining - 1;
						if(newValue < 0 || entity.isDead()){
							stunIt.remove();
							notifyStunOver(entity);
							if(entity instanceof Player){
								sendStunEndMessage((Player) entity);
							}
						}else{
							options.timeRemaining = newValue;
							
							double distanceStun = options.stunLocation.distance(entity.getLocation());
							if(distanceStun > 0.2){
								entity.teleport(options.stunLocation);
							}
							
							if(entity instanceof LivingEntity){
								if(newValue % 10 == 0){ //every 1/2 second.
									playStunEffect(entity);
								}
							}
						}
					}
				}
			}.runTaskTimer(plugin, 20, TICK_TIME);
			
			new DebugBukkitRunnable("StunManagerRemoveOld"){
				@Override
				protected void runIntern() {
					Iterator<Map.Entry<Entity, StunReduceContainer>> stunReduceIt = stunReduces.entrySet().iterator();
					while(stunReduceIt.hasNext()){
						Map.Entry<Entity,StunReduceContainer> entry = stunReduceIt.next();
						if(!entry.getKey().isValid()){
							entry.getValue().stop();
							stunReduceIt.remove();
						}
					}					
				}
			}.runTaskTimer(RacesAndClasses.getPlugin(), 20, 20);
		}
	}
	
	/**
	 * Sends the player a stun ended message
	 * 
	 * @param entity
	 */
	protected void sendStunEndMessage(Player entity) {
		LanguageAPI.sendTranslatedMessage((Player)entity, stun_ended);
	}

	/**
	 * Plays a StunEffect on the Location of the Player.
	 * 
	 * @param eyeLocation to play effect.
	 */
	protected void playStunEffect(Entity stunnedEntity) {
		List<Entity> near = stunnedEntity.getNearbyEntities(50, 50, 50);
		
		for(Entity entity : near){
			if(entity instanceof Player){
				VollotileCodeManager.getVollotileCode().playCriticalHitEffect((Player)entity, stunnedEntity);
			}
		}
	}

	/**
	 * Deinits the StunManager.
	 */
	public void deinit(){
		if(bukkitTask != null){
			bukkitTask.cancel();
		}
	}
	
	
	/**
	 * Returns the Rest stun time of the Entity.
	 * 
	 * @param entity to get from
	 * 
	 * @return time left in ticks or -1 if NOT stunned.
	 */
	public int getRestStunTime(Entity entity){
		if(entity == null){
			return -1;
		}
		
		if(!isStunned(entity)){
			return -1;
		}
		
		int time = stunTimes.get(entity).timeRemaining;
		return time;
	}
	
	
	/**
	 * Stuns an Entity for x ticks.
	 * 
	 * @param entity to stun
	 * @param time to stun
	 * 
	 * @return if it worked.
	 */
	public boolean stunEntity(Entity stunner, Entity entity, int time){
		if(entity == null || time <= 0) return false;
		
		//Check if imun:
		if(isImun(entity)) return false;
		
		StunReduceContainer container = stunReduces.get(entity);
		if(container == null) {
			container = new StunReduceContainer(entity);
			stunReduces.put(entity, container);
		}
		
		time = container.getReducedTicks(time);
		int restTime = getRestStunTime(entity);
		
		if(restTime > time){
			return false;
		}else{
			
			//Check for canceled Damage. This indicates Imun.
			if(stunner != null){
				if(EnemyChecker.areAllies(stunner, entity)) return false;
			}
			

			if(entity instanceof Player){
				PlayerStunnedEvent event = new PlayerStunnedEvent((Player)entity, time);
				plugin.fireEventToBukkit(event);
				
				if(event.isCancelled() || event.getTimeInTicks() <= 0) return false;
				time = event.getTimeInTicks();
			}
			
			StunOptions stunOptions = new StunOptions();
			stunOptions.stunLocation = entity.getLocation();
			stunOptions.timeRemaining = time / TICK_TIME;
			
			stunTimes.put(entity, stunOptions);

			//stun reduce notification.
			container.notifyStun();
			
			if(entity instanceof Player){
				int timeInSeconds = time / 20;
				LanguageAPI.sendTranslatedMessage((Player)entity, stun_message, 
						"time", String.valueOf(timeInSeconds));
			}
			
			return true;
		}
	}
	
	
	/**
	 * If the entity is imun against stun.
	 * @param entity to check.
	 * @return true if imun.
	 */
	private boolean isImun(Entity entity){
		if(entity == null) return false;
		
		String name = entity.getCustomName();
		return plugin.getConfigManager().getStatusImunManager().isImun(name, StatusEffect.STUN);
	}
	
	
	/**
	 * Removes all Stun effects from the entity.
	 * 
	 * @param entity to unStun.
	 * @return true if worked, false otherwise.
	 */
	public boolean removeStun(Entity entity){
		if(entity == null){
			return false;
		}
		
		if(!isStunned(entity)){
			return false;
		}
		
		stunTimes.remove(entity);
		notifyStunOver(entity);
		return true;
	}
	
	
	/**
	 * Returns if the Entity is stunned.
	 * 
	 * @param entity to check
	 * 
	 * @return true if stunned, false otherwise.
	 */
	public boolean isStunned(Entity entity){
		return stunTimes.containsKey(entity);
	}
	
	
	
	private void notifyStunOver(Entity entity){
		//stun reduce notification.
		StunReduceContainer container = stunReduces.get(entity);
		if(container == null) {
			container = new StunReduceContainer(entity);
			stunReduces.put(entity, container);
		}
		
		container.notifyStunStop();
	}
	
	
	
	private class StunOptions{
		private Location stunLocation;
		private int timeRemaining;
	}
}
