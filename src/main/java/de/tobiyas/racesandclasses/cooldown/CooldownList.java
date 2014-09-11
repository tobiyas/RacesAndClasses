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

public class CooldownList extends LinkedList<CooldownContainer>{
	private static final long serialVersionUID = 4706011594354900363L;


	public CooldownList() {
	}

	
	/**
	 * Checks if the player with the cooldown name is present in the list.
	 * 
	 * Returns true if present
	 * 
	 * @param player to check
	 * @param cooldownName to check
	 * @return true if present, false otherwise
	 */
	public boolean contains(String playerName, String cooldownName){
		return get(playerName, cooldownName) != null;
	}
	
	
	/**
	 * Returns the container belonging to the player + cooldown name
	 * 
	 * Returns null if not found.
	 * 
	 * @param player
	 * @param cooldownName
	 * @return
	 */
	public CooldownContainer get(String playerName, String cooldownName){
		for(CooldownContainer container : this){
			if(container.getPlayerName().equalsIgnoreCase(playerName) &&
					container.getCooldownName().equalsIgnoreCase(cooldownName)){
				return container;
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * Removes a container from the cooldown list
	 * 
	 * @param player
	 * @param cooldownName
	 * @return
	 */
	public CooldownContainer remove(String playerName, String cooldownName){
		CooldownContainer container = get(playerName, cooldownName);
		if(container != null){
			this.remove(container);
		}
		
		return container;
	}
	
	
	/**
	 * adds a container to the cooldown list
	 * 
	 * @param player
	 * @param cooldownName
	 */
	public void add(String playerName, String cooldownName, int cooldownTime){
		remove(playerName, cooldownName);
		this.add(new CooldownContainer(playerName, cooldownName, cooldownTime));
	}
	
	
	/**
	 * Ticks all containers and removes old ones
	 */
	public void tickAll(){
		List<CooldownContainer> remove = new LinkedList<CooldownContainer>();
		
		for(CooldownContainer container : this){
			container.tick();
			if(container.getCooldownTime() <= 0){
				remove.add(container);
			}
		}
		
		for(CooldownContainer removed : remove){
			this.remove(removed);
		}
	}

}
