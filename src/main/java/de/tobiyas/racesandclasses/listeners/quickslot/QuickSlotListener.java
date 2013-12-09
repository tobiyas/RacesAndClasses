package de.tobiyas.racesandclasses.listeners.quickslot;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;

public class QuickSlotListener implements Listener {

	/**
	 * The Plugin to register to.
	 */
	private final RacesAndClasses plugin;
	
	
	public QuickSlotListener(){
		this.plugin = RacesAndClasses.getPlugin();
		
		if(!CertainVersionChecker.isAbove1_6()){
			return; //only Bukkit 1.6 above supports quick slot via numbers.
		}
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void playerPressedQuickSlot(PlayerItemHeldEvent event){
		//This is not possible yet. :(
	}
}
