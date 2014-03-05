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
package de.tobiyas.racesandclasses.listeners.traitgui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.traitgui.TraitInventory;
import de.tobiyas.racesandclasses.util.inventory.InventoryResync;

public class TraitGuiListener implements Listener {
	
	/**
	 * The Main plugin
	 */
	private RacesAndClasses plugin;
	
	
	/**
	 * Registers to Bukkit EventListener
	 */
	public TraitGuiListener(){
		plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void traitInventoryGuiMoveCancle(InventoryClickEvent event){
		if(event.getView() instanceof TraitInventory){
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void traitInventoryGuiClose(InventoryCloseEvent event){
		if(event.getView() instanceof TraitInventory){
			if(event.getPlayer() instanceof Player){
				Player player = (Player) event.getPlayer();
				InventoryResync.resync(player);
			}
		}
	}
}
