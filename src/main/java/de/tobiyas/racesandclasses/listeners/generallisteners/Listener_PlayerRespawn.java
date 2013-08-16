package de.tobiyas.racesandclasses.listeners.generallisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class Listener_PlayerRespawn implements Listener{

	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	public Listener_PlayerRespawn() {
	}

	
	@EventHandler
	public void resetPlayerMaxHealthAfterDeath(PlayerRespawnEvent event){
		plugin.getPlayerManager().checkPlayer(event.getPlayer().getName());
	}
}
