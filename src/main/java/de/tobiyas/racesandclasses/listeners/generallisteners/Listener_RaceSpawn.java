package de.tobiyas.racesandclasses.listeners.generallisteners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public class Listener_RaceSpawn implements Listener {

	private final RacesAndClasses plugin;
	
	public Listener_RaceSpawn() {
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void playerDeath(PlayerRespawnEvent event) {
		boolean disabled = !plugin.getConfigManager().getGeneralConfig().isConfig_enableRaceSpawn()
				|| !plugin.getConfigManager().getGeneralConfig().isConfig_enableRaceSpawnOnDeath();
		
		if(disabled) return;
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(event.getPlayer());
		
		AbstractTraitHolder holder = racPlayer.getRace();
		if(holder == null) return;
		
		Location loc = plugin.getAddinManager().getRaceSpawnManager().getSpawnForRace(holder.getDisplayName());
		if(loc != null) event.setRespawnLocation(loc);
	}
}
