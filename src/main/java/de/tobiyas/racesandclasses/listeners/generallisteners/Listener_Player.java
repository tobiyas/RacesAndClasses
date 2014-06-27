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
/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.listeners.generallisteners;


import static de.tobiyas.racesandclasses.translation.languages.Keys.login_no_race_selected;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;


public class Listener_Player implements Listener {
	private RacesAndClasses plugin;

	public Listener_Player(){
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			plugin.getChannelManager().playerQuit(RaCPlayerManager.get().getPlayer(event.getPlayer()));
		}
	}
	 

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event){
		final RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		boolean racesActive = plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces();
		
		plugin.getPlayerManager().checkPlayer(player);
		RaceContainer container = player.getRace();
		if((container == null || container == plugin.getRaceManager().getDefaultHolder()) 
				&& racesActive){
			LanguageAPI.sendTranslatedMessage(player, login_no_race_selected);
			if(container == null){
				plugin.getRaceManager().addPlayerToHolder(player, Consts.defaultRace, true);
			}
		}
		
		//just to load them.
		player.getConfig();
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			plugin.getChannelManager().playerLogin(player);
		}
		
		if(racesActive){
			container.editTABListEntry(player);
		}
		
		boolean forceSelectOfRace = plugin.getConfigManager().getGeneralConfig().isConfig_openRaceSelectionOnJoinWhenNoRace();
		boolean playerHasNoRace = player.getRace() == plugin.getRaceManager().getDefaultHolder();
		int scheduledTimeToOpen = plugin.getConfigManager().getGeneralConfig().getConfig_debugTimeAfterLoginOpening();
		
		if(playerHasNoRace && forceSelectOfRace && racesActive){
			final HolderInventory raceInv = new HolderInventory(player.getPlayer(), plugin.getRaceManager());
			if(raceInv.getNumberOfHolder() > 0){
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						player.getPlayer().openInventory(raceInv);
					}
				}, scheduledTimeToOpen * 20);
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoinCheck(PlayerJoinEvent event){
		final RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		if(!config.contains("uuid")){
			YAMLPersistenceProvider.getLoadedPlayerFile(player).set("uuid", event.getPlayer().getUniqueId().toString());			
		}

		
		String newName = player.getName();
		String oldName = config.getString("lastKnownName", "");
		if(!newName.equals(oldName)){
			//Name has changed
			RacesAndClasses.getPlugin().log(oldName + " has changed his name to: " + newName);
			YAMLPersistenceProvider.getLoadedPlayerFile(player).set("lastKnownName", newName);
		}
		
		//set the last online time.
		YAMLPersistenceProvider.getLoadedPlayerFile(player).set("lastOnline", new Date().getTime());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			World oldWorld = event.getFrom();
			plugin.getChannelManager().playerChangedWorld(oldWorld, RaCPlayerManager.get().getPlayer(event.getPlayer()));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatEarly(AsyncPlayerChatEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()) return;
		String orgMsg = event.getMessage();
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		
		//check if we have a fake command.
		if(orgMsg != null && orgMsg.length() > 0 && orgMsg.charAt(0) == '/') return;
		
		MemberConfig config = player.getConfig();
		String channel = "Global";
		
		if(config != null){
			channel = config.getCurrentChannel();
			if(!plugin.getChannelManager().isMember(player, channel)){
				player.sendMessage(ChatColor.RED + "You are writing in a channel, you don't have access to. Please change your channel with " + 
									ChatColor.LIGHT_PURPLE + "/channel change" + ChatColor.YELLOW + " [channelname]");
				
				event.setCancelled(true);
				return;
			}
		}
		
		plugin.getChannelManager().editToChannel(channel, event);
	}
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChatLate(AsyncPlayerChatEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()) return;
		
		String message = event.getMessage();
		if(message != null && message.length() > 0 && message.charAt(0) == '/') return;
		
		String format = event.getFormat();
		format = format.replace("{msg}", "%2$s");
		
		event.setFormat(format);
	}
}
