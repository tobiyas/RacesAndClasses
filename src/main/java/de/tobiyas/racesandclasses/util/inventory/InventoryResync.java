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
package de.tobiyas.racesandclasses.util.inventory;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;

	/**
	 * Resyncs the Inventory of a Player in the next tick
	 * @author Tobiyas
	 *
	 */
	public class InventoryResync extends BukkitRunnable {

		public static void resync(Player player){
			RacesAndClasses plugin = RacesAndClasses.getPlugin();
			new InventoryResync(player).runTaskLater(plugin, 3);
		}
		
		public static void closeAndResync(Player player){
			RacesAndClasses plugin = RacesAndClasses.getPlugin();
			new InventoryResync(player).runTaskLater(plugin, 2);
		}
		
		private final Player player;
		
		public InventoryResync(final Player player){
			this.player = player;
		}
		
		@SuppressWarnings("deprecation") //let's see if it still works
		@Override
		public void run() {			
			player.closeInventory();
			
			//it's an attempt
			if(player != null && player.isOnline()){
				try{
					player.updateInventory();
				}catch(Exception exp){}; //since this is deprecated, we catch everything to prevent errors.
			}
		}
		
	}
