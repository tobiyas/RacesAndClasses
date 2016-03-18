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
package de.tobiyas.racesandclasses.listeners.generallisteners;

import static de.tobiyas.racesandclasses.translation.languages.Keys.stun_still;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.entitystatusmanager.stun.StunManager;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class StunCancelListener implements Listener {

	/**
	 * The cooldown map to use.
	 */
	private final Map<String, Integer> cooldownMap = new HashMap<String, Integer>();
	
	
	
	/**
	 * The plugin to get the StunManager.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * Init the Listener.
	 */
	public StunCancelListener() {
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		//Start the Stun-Resume Timer.
		new DebugBukkitRunnable("StunReduceTask") {
			@Override
			protected void runIntern() {
				Iterator<Map.Entry<String, Integer>> entryIt = cooldownMap.entrySet().iterator();
				while(entryIt.hasNext()){
					Map.Entry<String, Integer> entry = entryIt.next();
					entry.setValue(entry.getValue() - 1);
					
					if(entry.getValue() < 0){
						entryIt.remove();
					}
				}
			}
		}.runTaskTimer(plugin, 20*1, 20*1);
	}
	
	
	/**
	 * Returns the Stunmanager of the Plugin.
	 * 
	 * @return stun manager.
	 */
	private StunManager getStunManager(){
		return plugin.getStunManager();
	}
	
	/**
	 * Returns if an Entity is stunned at the moment.
	 * 
	 * @param entity to check
	 * 
	 * @return true if is stunned, false otherwise.
	 */
	private boolean isStunned(Entity entity){
		return getStunManager().isStunned(entity);
	}
	
	
	/**
	 * Returns the remaining stun time in seconds.
	 * 
	 * @param entity to check
	 * 
	 * @return returns the Rest time of the Stun in Seconds
	 */
	private int getRemainingStunSeconds(Entity entity){
		int restTimeInTicks = getStunManager().getRestStunTime(entity);
		int restTimeInSeconds = restTimeInTicks / 20;
		
		return restTimeInSeconds;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMovement(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(isStunned(player)){
			event.setCancelled(true);
			
			if(cooldownMap.containsKey(player.getName())) return;
			String time = String.valueOf(getRemainingStunSeconds(player));
			String action = "move";
			LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
			cooldownMap.put(player.getName(), 2);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		if(isStunned(player)){
			event.setCancelled(true);
			
			if(cooldownMap.containsKey(player.getName())) return;
			String time = String.valueOf(getRemainingStunSeconds(player));
			String action = "teleport";
			LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
			cooldownMap.put(player.getName(), 2);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event){
		if(isStunned(event.getDamager())){
			event.setCancelled(true);
			
			if(event.getDamager() instanceof Player){
				Player player = (Player) event.getDamager();
				if(cooldownMap.containsKey(player.getName())) return;
				
				if(cooldownMap.containsKey(player.getName())) return;
				String time = String.valueOf(getRemainingStunSeconds(player));
				String action = "attack";
				LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
				cooldownMap.put(player.getName(), 2);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBowShoot(EntityShootBowEvent event){
		if(isStunned(event.getEntity())){
			event.setCancelled(true);
			
			if(event.getEntity() instanceof Player){
				Player player = (Player) event.getEntity();
				
				if(cooldownMap.containsKey(player.getName())) return;
				String time = String.valueOf(getRemainingStunSeconds(player));
				String action = "shoot your bow";
				LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
				cooldownMap.put(player.getName(), 2);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		
		if(isStunned(player)){
			event.setCancelled(true);
			
			if(cooldownMap.containsKey(player.getName())) return;
			String time = String.valueOf(getRemainingStunSeconds(player));
			String action = "place anything";
			LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
			cooldownMap.put(player.getName(), 2);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		
		if(isStunned(player)){
			event.setCancelled(true);
			
			if(cooldownMap.containsKey(player.getName())) return;
			String time = String.valueOf(getRemainingStunSeconds(player));
			String action = "break anything";
			LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
			cooldownMap.put(player.getName(), 2);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreeperExplode(ExplosionPrimeEvent event){
		if(isStunned(event.getEntity())){
			event.setCancelled(true);
		}
	}
}
