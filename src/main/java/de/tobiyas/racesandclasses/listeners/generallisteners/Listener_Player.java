/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.listeners.generallisteners;


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
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.configuration.member.MemberConfig;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.healthmanagement.HealthManager;
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
			ChannelManager.GetInstance().playerQuit(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		RaceContainer container = (RaceContainer) RaceManager.getInstance().getHolderOfPlayer(player.getName());
		if(container == null || container == RaceManager.getInstance().getDefaultHolder()){
			player.sendMessage(ChatColor.RED + "You have not selected a Race. Please select a race using /race select <racename>");
			if(container == null){
				RaceManager.getInstance().addPlayerToHolder(player.getName(), Consts.defaultRace);
			}
		}
		
		HealthManager.getHealthManager().checkPlayer(player.getName());
		plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			ChannelManager.GetInstance().playerLogin(player);
		}
		
		container.editTABListEntry(player.getName());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			World oldWorld = event.getFrom();
			Player player = event.getPlayer();
			ChannelManager.GetInstance().playerChangedWorld(oldWorld, player);
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()) return;
		String orgMsg = event.getMessage();
		Player player = event.getPlayer();
		
		if(orgMsg.charAt(0) == '/') return;
		event.setCancelled(true);
		
		MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
		String channel = "Global";
		
		if(config != null){
			channel = config.getCurrentChannel();
			if(!ChannelManager.GetInstance().isMember(player.getName(), channel)){
				player.sendMessage(ChatColor.RED + "You are writing in a channel, you don't have access to. Please change your channel with " + 
									ChatColor.LIGHT_PURPLE + "/channel change" + ChatColor.YELLOW + " [channelname]");
				return;
			}
		}

		ChannelManager.GetInstance().broadcastMessageToChannel(channel, player, orgMsg);
	}
	
}
