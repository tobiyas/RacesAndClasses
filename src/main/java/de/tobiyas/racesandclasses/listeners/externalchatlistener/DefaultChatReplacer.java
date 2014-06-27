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
package de.tobiyas.racesandclasses.listeners.externalchatlistener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public class DefaultChatReplacer implements Listener {

	/**
	 * The Plugin to call stuff on.
	 */
	private final RacesAndClasses plugin;
	
	
	public DefaultChatReplacer() {
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void asyncChatHigh(AsyncPlayerChatEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			//plugin is already handling chat.
			return;
		}
		
		replace(event);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void asyncChat(AsyncPlayerChatEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			//plugin is already handling chat.
			return;
		}
		
		replace(event);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void asyncChatLow(AsyncPlayerChatEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			//plugin is already handling chat.
			return;
		}
		
		replace(event);
	}
	
	
	private void replace(AsyncPlayerChatEvent event){
		String format = event.getFormat();
		if(format == null) return;
		
		String raceTag = "";
		String classTag = "";

		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		AbstractTraitHolder holder = player.getRace();
		if(holder != null){
			raceTag = holder.getTag();
			if("".equals(raceTag)){
				raceTag = "[" + holder.getDisplayName() + "]";
			}
		}
		
		holder = player.getclass();
		if(holder != null){
			classTag = holder.getTag();
			if("".equals(classTag)){
				classTag = "[" + holder.getDisplayName() + "]";
			}
		}
		
		String raceReplacement = "{race}";
		String classReplacement = "{class}";
		
	    if (format.contains(raceReplacement)) {
	      format = format.replace(raceReplacement, raceTag);
	    }

	    if (format.contains(classReplacement)) {
			format = format.replace(classReplacement, classTag);
	    }
	    
	    event.setFormat(format);
	}
}
