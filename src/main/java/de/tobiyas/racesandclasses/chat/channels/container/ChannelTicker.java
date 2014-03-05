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

import java.util.HashSet;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class ChannelTicker implements Runnable {

	private RacesAndClasses plugin;
	
	protected static ChannelTicker ticker = null;
	protected int currentTask;
	protected HashSet<ChannelContainer> channels;
	
	
	public ChannelTicker(){
		plugin = RacesAndClasses.getPlugin();
		
		channels = new HashSet<ChannelContainer>();
		currentTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
	}
	
	private void disableIntern(){
		channels.clear();
		Bukkit.getScheduler().cancelTask(currentTask);
		ticker = null;
	}
	
	public static void init(){
		if(ticker == null){
			ticker = new ChannelTicker();
		}
	}
	
	public static void disable(){
		if(ticker != null){
			ticker.disableIntern();
		}
	}
	
	public static void registerChannel(ChannelContainer container){
		init();
		ticker.channels.add(container);
	}
	
	public static void unregisterChannel(ChannelContainer container){
		init();
		ticker.channels.remove(container);
	}
	
	@Override
	public void run() {
		for(ChannelContainer container : channels){
			if(container == null) {
				channels.remove(container);
			}else{
				container.tick();
			}
		}
	}

}
