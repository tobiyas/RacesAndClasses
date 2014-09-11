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
package de.tobiyas.racesandclasses.cooldown;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class CooldownManager {

	/**
	 * The cooldown map.
	 * Syntax for keys is: "playername.cooldownname".
	 * The time (second argument) is given in seconds
	 */
	protected CooldownList cooldownList;
	
	
	/**
	 * The task id of the scheduler task reducing the cooldown
	 */
	private int taskId;
	
	
	/**
	 * Creates a new map for the cooldown
	 */
	public CooldownManager() {
		cooldownList = new CooldownList();
	}
	
	
	/**
	 * This starts the scheduler task decreasing uplink times
	 */
	public void init(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new UplinkReducingTask(), 20, 20);
	}
	
	/**
	 * Shuts the scheduler task down correct
	 */
	public void shutdown(){
		Bukkit.getScheduler().cancelTask(taskId);
	}

	
	/**
	 * Checks if the Player with the passed name
	 * still has uplink for the given cooldown.
	 * 
	 * The return value is the time in seconds that he still has uplink.
	 * If the value is -1 (or <= 0) he has no uplink.
	 * 
	 * @param player
	 * @param cooldownName
	 * @return
	 */
	public int stillHasCooldown(String player, String cooldownName){
		synchronized (cooldownList) {
			if(!cooldownList.contains(player, cooldownName)){
				return -1;
			}
			
			return cooldownList.get(player, cooldownName).getCooldownTime();
		}
	}
	
	
	/**
	 * Gives the passed player a cooldown on the passed cooldown name.
	 * The time is in seconds.
	 * 
	 * If a value <= 0 is passed, the player with it's cooldownName is removed from the list.
	 * 
	 * @param player to set to
	 * @param cooldownName to set
	 * @param time in seconds
	 */
	public void setCooldown(String player, String cooldownName, int time){
		synchronized (cooldownList) {
			if(time <= 0 ){
				cooldownList.remove(player, cooldownName);
			}else{
				cooldownList.add(player, cooldownName, time);			
			}
		}	
	}
	

	/**
	 * Returns a List of all cooldown names the player has at this moment.
	 * 
	 * @param player to check
	 * @return 
	 */
	public List<String> getAllCooldownsOfPlayer(String playerName) {
		List<String> playerCooldownList = new LinkedList<String>();
		
		synchronized (cooldownList) {
			for(CooldownContainer container : cooldownList){
				if(container.getPlayerName().equalsIgnoreCase(playerName)){
					playerCooldownList.add(container.getCooldownName());
				}
			}
		}
		
		return playerCooldownList;
	}
	
	
	/**
	 * Ticks the map.
	 * This means that each time is reduced by 1.
	 * It is meant to be called once per second.
	 */
	protected void tick(){
		if(cooldownList.isEmpty()){
			return; //early out to not block the map
		}
		
		synchronized (cooldownList) {
			cooldownList.tickAll();
		}
	}
	
	
	/**
	 * This is an intern class that is only responsible for reducing the
	 * uplink of the {@value CooldownManager#cooldownMap}.
	 * 
	 * @author tobiyas
	 *
	 */
	protected class UplinkReducingTask implements Runnable{

		@Override
		public void run() {
			tick();
		}
		
	}


}
