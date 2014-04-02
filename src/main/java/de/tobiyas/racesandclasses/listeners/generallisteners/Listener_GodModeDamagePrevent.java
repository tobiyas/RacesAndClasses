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

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class Listener_GodModeDamagePrevent implements Listener{

	private RacesAndClasses plugin;
	
	public Listener_GodModeDamagePrevent() {
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void checkPlayerGodMode(EntityDamageEvent event){
		if(event.getEntityType() != EntityType.PLAYER){
			return;
		}
		
		//safe cast because of check before
		Player player = (Player) event.getEntity();
		if(plugin.getPlayerManager().isGod(player)){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void checkPlayerGodMode(EntityDeathEvent event){
		if(event.getEntityType() != EntityType.PLAYER){
			return;
		}
		
		//safe cast because of check before
		Player player = (Player) event.getEntity();
		if(plugin.getPlayerManager().isGod(player)){
			player.sendMessage(ChatColor.GREEN + "Sorry, even " + ChatColor.GOLD + "GOD"
				+ ChatColor.GREEN + " could not prevent your death.  " + ChatColor.BLUE + ":(");
		}
	}

}
