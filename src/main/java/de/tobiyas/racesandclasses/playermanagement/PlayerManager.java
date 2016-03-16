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
package de.tobiyas.racesandclasses.playermanagement;

import java.util.HashMap;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class PlayerManager{
	
	/**
	 * This is the map of each player's health container
	 */
	private HashMap<RaCPlayer, PlayerContainer> playerData;
	
	/**
	 * The plugin to access other parts of the Plugin.
	 */
	private RacesAndClasses plugin;
	
	/**
	 * Creates a new PlayerManager and resets all values
	 */
	public PlayerManager(){
		playerData = new HashMap<RaCPlayer, PlayerContainer>();
		plugin = RacesAndClasses.getPlugin();
		
		new DebugBukkitRunnable("RaCPlayerDataTicker") {
			@Override
			protected void runIntern() {
				for(PlayerContainer container : playerData.values()){
					container.tick();
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	/**
	 * Loads the health manager from the playerdata.yml
	 */
	public void init(){
		playerData.clear();
	}
	
	/**
	 * Shuts down the Player Manager.
	 */
	public void shutdown(){
		for(PlayerContainer container: playerData.values()){
			container.shutdown();
		}
		
		playerData.clear();
	}
	
	
	public synchronized PlayerContainer getContainer(RaCPlayer player){
		PlayerContainer container = playerData.get(player);
		if(container == null) {
			container = new PlayerContainer(player);
			playerData.put(player, container);
			container.init();
		}
		
		return container;
	}

	/**
	 * Returns the current size of the PlayerManager.
	 * 
	 * @return the size of the player manager.
	 */
	public String getPlayerNumber() {
		return String.valueOf(playerData.size());
	}

}
