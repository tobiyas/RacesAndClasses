package de.tobiyas.racesandclasses.listeners.generallisteners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class PlayerLastDamageListener implements Listener {

	/**
	 * The Map of the last Damages
	 */
	private final Map<String, Long> lastDamageDate = new HashMap<String, Long>();
	
	/**
	 * The plugin to call stuff on
	 */
	private RacesAndClasses plugin;

	/**
	 * The Instance of the listener.
	 */
	private static PlayerLastDamageListener instance;
	
	
	public PlayerLastDamageListener() {
		plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		instance = this;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event){
		if(event.isCancelled()) return;
		
		if(event.getEntityType() == EntityType.PLAYER){
			Player player = (Player) event.getEntity();
			String playerName = player.getName();
			
			long currentTime = System.currentTimeMillis();
			lastDamageDate.put(playerName, currentTime);
		}
	}
	
	/**
	 * Returns the seconds that have passed since the last damage cause.
	 * 
	 * @param playerName to check
	 * @return the ticks passed.
	 */
	public static int getTimePassedSinceLastDamageInSeconds(String playerName){
		if(!instance.lastDamageDate.containsKey(playerName)){
			return Integer.MAX_VALUE;
		}
		
		long lastDamage = instance.lastDamageDate.get(playerName);		
		long currentTime = System.currentTimeMillis();
		
		long passedTime = currentTime - lastDamage;
		return (int) (passedTime / 1000l);
	}
}
