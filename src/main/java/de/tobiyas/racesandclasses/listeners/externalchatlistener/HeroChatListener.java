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
import org.bukkit.event.Listener;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Herochat;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class HeroChatListener implements Listener  {

	/**
	 * The Plugin to call stuff on.
	 */
	private final RacesAndClasses plugin;
	
	
	public HeroChatListener() {
		this.plugin = RacesAndClasses.getPlugin();
		
		if(!isHeroChatThere()) return;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Checks if the HeroChat Plugin is there.
	 * 
	 * @return true if it is present, false otherwise.
	 */
	public static boolean isHeroChatThere(){
		return Bukkit.getPluginManager().getPlugin("Herochat") != null;
	}
	
	
	@EventHandler
	public void heroChatMessage(ChannelChatEvent event){		
		String format = event.getFormat();
		if (format.equals("{default}")) {
			format = Herochat.getChannelManager().getStandardFormat();
		}
		
		String raceTag = "";
		String classTag = "";
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getSender().getPlayer());
		AbstractTraitHolder holder = player.getRace();
		if(holder != null){
			raceTag = holder.getTag();
		}
		
		holder = player.getclass();
		if(holder != null){
			classTag = holder.getTag();
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
