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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class StunManager {

	/**
	 * The Map containing all Stuns.
	 */
	private Map<Entity, StunOptions> stunTimes = new ConcurrentHashMap<Entity, StunOptions>();
	
	/**
	 * The Bukkit taskID for the StunDecreaser.
	 */
	private int bukkitTaskId = -1;
	
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
		if(bukkitTaskId < 0){
			bukkitTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					Iterator<Map.Entry<Entity, StunOptions>> stunIt = stunTimes.entrySet().iterator();
					while (stunIt.hasNext()){
						Map.Entry<Entity, StunOptions> entry = stunIt.next();
						StunOptions options = entry.getValue();
						Entity entity = entry.getKey();
						
						int newValue = options.timeRemaining - 1;
						if(newValue < 0 || entity.isDead()){
							stunIt.remove();
							if(entity instanceof Player){
								sendStunEndMessage((Player) entity);
							}
						}else{
							entry.getValue().timeRemaining = newValue;
							
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
			}, 1, 1);
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
		if(bukkitTaskId > 0){
			Bukkit.getScheduler().cancelTask(bukkitTaskId);
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
	public boolean stunEntity(Entity entity, int time){
		if(entity == null || time <= 0){
			return false;
		}
		
		int restTime = getRestStunTime(entity);
		if(restTime > time){
			return false;
		}else{
			StunOptions stunOptions = new StunOptions();
			stunOptions.stunLocation = entity.getLocation();
			stunOptions.timeRemaining = time;
			
			stunTimes.put(entity, stunOptions);
			
			if(entity instanceof Player){
				int timeInSeconds = time / 20;
				LanguageAPI.sendTranslatedMessage((Player)entity, stun_message, 
						"time", String.valueOf(timeInSeconds));
			}
			
			return true;
		}
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
	
	
	
	private class StunOptions{
		private Location stunLocation;
		private int timeRemaining;
	}
}
