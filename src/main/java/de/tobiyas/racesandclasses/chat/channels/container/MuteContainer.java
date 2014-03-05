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

import de.tobiyas.util.config.YAMLConfigExtended;

public class MuteContainer {

	private HashMap<String, Integer> muted = new HashMap<String, Integer>();
	
	public MuteContainer(){
	}
	
	public MuteContainer(YAMLConfigExtended config, String channelPre){
		Set<String> mutedPlayers = config.getChildren(channelPre + ".muted");
		for(String playerName : mutedPlayers){
			int time = config.getInt(channelPre + ".muted." + playerName);
			muted.put(playerName, time);
		}
	}
	
	/**
	 * Mutes a player for the passed Time.
	 * 
	 * Returns true if the player got muted.
	 * Returns false if the player is already muted.
	 * 
	 * @param playerName
	 * @param time
	 * @return
	 */
	public boolean mutePlayer(String playerName, int time){
		if(!muted.containsKey(playerName)){
			muted.put(playerName, time);
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
	 * @param playerName
	 */
	public boolean unmutePlayer(String playerName){
		for(String name : muted.keySet()){
			if(playerName.equalsIgnoreCase(name)){
				muted.remove(name);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Saves the current {@link MuteContainer} to the passed config
	 * under the channelPrefix passed.
	 * 
	 * @param config
	 * @param channelPre
	 */
	public void saveContainer(YAMLConfigExtended config, String channelPre){
		for(String name : muted.keySet()){
			int time = muted.get(name);
			config.set(channelPre + ".muted." + name, time);
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
	 * @param playerName
	 * @return
	 */
	public int isMuted(String playerName){
		for(String name : muted.keySet()){
			if(playerName.equalsIgnoreCase(name))
				return muted.get(name);
		}
		
		return -1;
	}
	
	/**
	 * Ticks the container to process unmuting after the time muted.
	 */
	public void tick(){
		for(String name : muted.keySet()){
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
