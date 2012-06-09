/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races.listeners;


import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import de.tobiyas.races.Races;
import de.tobiyas.races.chat.channels.ChannelManager;
import de.tobiyas.races.configuration.member.MemberConfig;
import de.tobiyas.races.configuration.member.MemberConfigManager;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;


public class Listener_Player implements Listener {
	private Races plugin;

	public Listener_Player(){
		plugin = Races.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent event){
		Player player = event.getPlayer();
		ChannelManager.GetInstance().playerQuit(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		if(container == null){
			player.sendMessage(ChatColor.RED + "You have not selected a Race. Please select a race using /race select <racename>");
		}
		
		HealthManager.getHealthManager().checkPlayer(player.getName());
		MemberConfigManager.getInstance().getConfigOfPlayer(player.getName());
		ChannelManager.GetInstance().playerLogin(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
		World oldWorld = event.getFrom();
		Player player = event.getPlayer();
		ChannelManager.GetInstance().playerChangedWorld(oldWorld, player);
	}
	
	@EventHandler
	public void onPlayerSpritToggle(PlayerToggleSprintEvent event){
		TraitEventManager.fireEvent(event);
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event){
		if(!plugin.interactConfig().getconfig_channels_enable()) return;
		String orgMsg = event.getMessage();
		Player player = event.getPlayer();
		
		if(orgMsg.charAt(0) == '/') return;
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		
		if(container == null) return;
		event.setCancelled(true);
		
		MemberConfig config = MemberConfigManager.getInstance().getConfigOfPlayer(player.getName());
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
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		TraitEventManager.fireEvent(event);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		HealthManager.getHealthManager().resetHealth(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerInterace(PlayerInteractEvent event){
		TraitEventManager.fireEvent(event);
	}
}
