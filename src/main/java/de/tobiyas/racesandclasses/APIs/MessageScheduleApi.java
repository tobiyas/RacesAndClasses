package de.tobiyas.racesandclasses.APIs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class MessageScheduleApi {

	
	/**
	 * Sends a Message to a Player in X seconds.
	 * 
	 * @param playerName the player to send to
	 * @param timeInSeconds in which time to send
	 * @param message the message to send
	 */
	public static void scheduleMessageToPlayer(final String playerName, final int timeInSeconds, final String message){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = Bukkit.getPlayer(playerName);
				if(player != null && player.isOnline()){
					player.sendMessage(message);
				}
			}
		}, timeInSeconds * 20);
	}

}
