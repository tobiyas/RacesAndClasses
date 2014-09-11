package de.tobiyas.racesandclasses.listeners.externalchatlistener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;

/**
 * This will only work if the TownyChannel is 'Hooked'.
 * (Whatever this means. O_o)
 * 
 * @author tobiyas
 *
 */
public class TownyChatListener implements Listener {

	
	public TownyChatListener() {
		//do not register when Towny is not present.
		if(Bukkit.getPluginManager().getPlugin("TownyChat") == null) return;
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
	}
	
	
	@EventHandler
	public void townyChatHookEvent(AsyncChatHookEvent event){
		Player player = event.getPlayer();
		
		String format = event.getFormat();
		
		String raceName = RaceAPI.getRaceNameOfPlayer(player);
		String className = ClassAPI.getClassNameOfPlayer(player);
		
		format = format
				.replace("{race}", raceName)
				.replace("{class}", className);
		
		event.setFormat(format);
	}

}
