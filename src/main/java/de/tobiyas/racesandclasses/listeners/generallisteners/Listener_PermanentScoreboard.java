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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;

public class Listener_PermanentScoreboard implements Listener {

	/**
	 * the plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * The click Map.
	 */
	private final Map<UUID,ClickData> clickMap = new HashMap<UUID,ClickData>();
	

	public Listener_PermanentScoreboard(){
		plugin = RacesAndClasses.getPlugin();
		plugin.registerEvents(this);
	}
	
	
	@EventHandler
	public void onPlayerChangeItemInhands(PlayerInteractEvent event){
		if(event.isCancelled()) return;
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enable_permanent_scoreboard()) return;
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(event.getItem() != null && event.getItem().getType() != Material.AIR) return;
		
		World world = event.getPlayer().getWorld();
		if(plugin.getConfigManager().getGeneralConfig().getConfig_worldsDisabled().contains(world.getName())){
			return;
		}
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		UUID id = player.getUniqueId();
		if(!clickMap.containsKey(id)) clickMap.put(id, new ClickData());
		
		ClickData data = clickMap.get(id);
		if(data.clicked()) {
			data.clear();
			player.getScoreboardManager().cycleToNext();
			event.setCancelled(true);
		}
	}

	
	private class ClickData{
		private final Queue<Long> clicks = new LinkedList<Long>();
		public void clear() { clicks.clear(); }
		private boolean clicked(){
			long now = System.currentTimeMillis();
			clicks.add(now);
			if(clicks.size() > 3) clicks.poll();
			if(clicks.size() != 3) return false;
			
			long lowestVal = now - 2000;
			for(Long val : clicks) if(val < lowestVal) return false;
			
			return true;
		}
	}
	
}
