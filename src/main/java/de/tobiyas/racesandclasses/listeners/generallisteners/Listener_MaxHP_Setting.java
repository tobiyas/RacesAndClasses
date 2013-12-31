package de.tobiyas.racesandclasses.listeners.generallisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class Listener_MaxHP_Setting implements Listener {
	
	private RacesAndClasses plugin;

	public Listener_MaxHP_Setting(){
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
		String playerName = event.getPlayer().getName();
		plugin.getPlayerManager().checkPlayer(playerName);
	}
}
