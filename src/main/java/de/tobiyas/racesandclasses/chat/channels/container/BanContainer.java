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
package de.tobiyas.racesandclasses.chat.channels.container;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import de.tobiyas.util.config.YAMLConfigExtended;

;

public class BanContainer {

	/**
	 * Map of all banned players to how long they are banned.
	 */
	private HashMap<UUID, Integer> banned = new HashMap<UUID, Integer>();

	public BanContainer() {
	}

	/**
	 * inits the BanContainer from an YML Configuration with an channel name
	 * 
	 * @param config
	 * @param channelPre
	 */
	public BanContainer(YAMLConfigExtended config, String channelPre) {
		Set<String> bannedPlayers = config.getChildren(channelPre + ".banned");
		for (String player : bannedPlayers) {
			try{
				UUID id = UUID.fromString(player);
				
				int time = config.getInt(channelPre + ".banned." + player);
				banned.put(id, time);
			}catch(IllegalArgumentException exp){ continue; }
		}
	}

	/**
	 * bans a player for a certain time. <br>
	 * (time in seconds)
	 * <br>
	 * 
	 * Returns if banning worked. If it worked, true is returned.
	 * If the player is already banned, false is returned.
	 * 
	 * @param playerUUID
	 * @param time
	 */
	public boolean banPlayer(UUID player, int time) {
		if (!banned.containsKey(player)) {
			banned.put(player, time);
			return true;
		}
		
		return false;
	}

	/**
	 * unbans a player from the ban list.
	 * <br>
	 * Returns true if unbanning worked.
	 * Returns false if the Player was not found.
	 * 
	 * @param playerUUID
	 */
	public boolean unbanPlayer(UUID player) {
		return banned.remove(player) != null;
	}

	/**
	 * Saves the current state of the container to the passed
	 * {@link YAMLConfigExtended} under the given channelPrefix.
	 * 
	 * @param config
	 * @param channelPre
	 */
	public void saveContainer(YAMLConfigExtended config, String channelPre) {
		for (UUID name : banned.keySet()) {
			int time = banned.get(name);
			config.set(channelPre + ".banned." + name, time);
		}
		
		if(banned.keySet().size() == 0){
			config.set(channelPre + ".banned.empty", true);
		}
	}

	/**
	 * checks if the player passed is banned in the channel
	 * <br>
	 * Returns the time still banned.
	 * If not found, returns -1.
	 * 
	 * @param playerUUID
	 * @return
	 */
	public int isBanned(UUID player) {
		return banned.containsKey(player) ? banned.get(player) : -1;
	}

	/**
	 * Ticks the ban container to let the poor guys be removed from ban list
	 * after the specified time.
	 * 
	 */
	public void tick() {
		for (UUID name : banned.keySet()) {
			int duration = banned.get(name);
			if (duration == Integer.MAX_VALUE){
				continue;
			}
			
			duration--;
			if (duration < 0){
				banned.remove(name);
			}else{
				banned.put(name, duration);
			}
		}
	}
}
