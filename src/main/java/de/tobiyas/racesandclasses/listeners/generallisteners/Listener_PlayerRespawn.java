package de.tobiyas.racesandclasses.listeners.generallisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.tobiyas.racesandclasses.healthmanagement.HealthManager;

public class Listener_PlayerRespawn implements Listener{

	public Listener_PlayerRespawn() {
	}

	
	@EventHandler
	public void resetPlayerMaxHealthAfterDeath(PlayerRespawnEvent event){
		HealthManager.getHealthManager().checkPlayer(event.getPlayer().getName());
	}
}
