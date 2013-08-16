package de.tobiyas.racesandclasses.listeners.traitgui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.traitgui.TraitInventory;
import de.tobiyas.racesandclasses.util.inventory.InventoryResync;

public class TraitGuiListener implements Listener {
	
	/**
	 * The Main plugin
	 */
	private RacesAndClasses plugin;
	
	
	/**
	 * Registers to Bukkit EventListener
	 */
	public TraitGuiListener(){
		plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void traitInventoryGuiMoveCancle(InventoryClickEvent event){
		if(event.getView() instanceof TraitInventory){
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void traitInventoryGuiClose(InventoryCloseEvent event){
		if(event.getView() instanceof TraitInventory){
			if(event.getPlayer() instanceof Player){
				Player player = (Player) event.getPlayer();
				InventoryResync.resync(player);
			}
		}
	}
}
