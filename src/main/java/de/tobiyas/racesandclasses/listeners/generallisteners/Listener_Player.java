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

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.util.consts.Consts;


public class Listener_Player implements Listener {
	private RacesAndClasses plugin;

	public Listener_Player(){
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			Player player = event.getPlayer();
			plugin.getChannelManager().playerQuit(player);
		}
	}
	 

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		boolean racesActive = plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces();
		
		RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player.getName());
		if((container == null || container == plugin.getRaceManager().getDefaultHolder()) 
				&& racesActive){
			LanguageAPI.sendTranslatedMessage(player, login_no_race_selected);
			if(container == null){
				plugin.getRaceManager().addPlayerToHolder(player.getName(), Consts.defaultRace, true);
			}
		}
		
		plugin.getPlayerManager().checkPlayer(player.getName());
		plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			plugin.getChannelManager().playerLogin(player);
		}
		
		if(racesActive){
			container.editTABListEntry(player.getName());
		}
		
		boolean forceSelectOfRace = plugin.getConfigManager().getGeneralConfig().isConfig_openRaceSelectionOnJoinWhenNoRace();
		boolean playerHasNoRace = plugin.getRaceManager().getHolderOfPlayer(player.getName()) == plugin.getRaceManager().getDefaultHolder();
		int scheduledTimeToOpen = plugin.getConfigManager().getGeneralConfig().getConfig_debugTimeAfterLoginOpening();
		
		if(playerHasNoRace && forceSelectOfRace && racesActive){
			final HolderInventory raceInv = new HolderInventory(player, plugin.getRaceManager());
			if(raceInv.getNumberOfHolder() > 0){
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						player.openInventory(raceInv);
					}
				}, scheduledTimeToOpen * 20);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			World oldWorld = event.getFrom();
			Player player = event.getPlayer();
			plugin.getChannelManager().playerChangedWorld(oldWorld, player);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatEarly(AsyncPlayerChatEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()) return;
		String orgMsg = event.getMessage();
		Player player = event.getPlayer();
		
		if(orgMsg.charAt(0) == '/') return;
		
		MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
		String channel = "Global";
		
		if(config != null){
			channel = config.getCurrentChannel();
			if(!plugin.getChannelManager().isMember(player.getName(), channel)){
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
		if(event.getMessage().charAt(0) == '/') return;
		
		String format = event.getFormat();
		format = format.replace("{msg}", event.getMessage());
		
		event.setFormat(format);
	}
}
