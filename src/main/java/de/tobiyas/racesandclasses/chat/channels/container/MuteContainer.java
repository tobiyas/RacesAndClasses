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

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.util.config.YAMLConfigExtended;

public class MuteContainer {

	private final HashMap<RaCPlayer, Integer> muted = new HashMap<RaCPlayer, Integer>();
	
	public MuteContainer(){
	}
	
	public MuteContainer(YAMLConfigExtended config, String channelPre){
		Set<String> mutedPlayers = config.getChildren(channelPre + ".muted");
		for(String player : mutedPlayers){
			try{
				UUID id = UUID.fromString(player);
				RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(id);
				
				int time = config.getInt(channelPre + ".muted." + player);
				muted.put(racPlayer, time);
			}catch(IllegalArgumentException exp){ continue; }
		}
	}
	
	/**
	 * Mutes a player for the passed Time.
	 * 
	 * Returns true if the player got muted.
	 * Returns false if the player is already muted.
	 * 
	 * @param player
	 * @param time
	 * @return
	 */
	public boolean mutePlayer(RaCPlayer player, int time){
		if(!muted.containsKey(player)){
			muted.put(player, time);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Unmutes a player.
	 * 
	 * Returns true if the player got unmuted.
	 * Returns false if the player was not found.
	 * 
	 * @return
	 * @param player
	 */
	public boolean unmutePlayer(RaCPlayer player){
		return muted.remove(player) != null;
	}
	
	/**
	 * Saves the current {@link MuteContainer} to the passed config
	 * under the channelPrefix passed.
	 * 
	 * @param config
	 * @param channelPre
	 */
	public void saveContainer(YAMLConfigExtended config, String channelPre){
		for(RaCPlayer name : muted.keySet()){
			int time = muted.get(name);
			config.set(channelPre + ".muted." + name.getUniqueId(), time);
		}
		
		if(muted.keySet().size() == 0){
			config.set(channelPre + ".muted.empty", true);
		}
	}
	
	/**
	 * Checks if a player is muted.
	 * 
	 * Returns the time the player is still muted.
	 * If -1 is returned, the player is NOT muted.
	 * 
	 * @param player
	 * @return
	 */
	public int isMuted(RaCPlayer player){		
		return muted.containsKey(player) ? muted.get(player) : -1;
	}
	
	/**
	 * Ticks the container to process unmuting after the time muted.
	 */
	public void tick(){
		for(RaCPlayer name : muted.keySet()){
			int duration = muted.get(name);
			if(duration == Integer.MAX_VALUE) continue;
			duration --;
			if(duration < 0)
				muted.remove(name);
			else
				muted.put(name, duration);
		}
	}
}
