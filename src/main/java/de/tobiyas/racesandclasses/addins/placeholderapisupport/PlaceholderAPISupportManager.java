package de.tobiyas.racesandclasses.addins.placeholderapisupport;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import de.tobiyas.racesandclasses.RacesAndClasses;

public class PlaceholderAPISupportManager implements Listener {

	private static final String PLUGIN_NAME = "MVdWPlaceholderAPI";
	
	/**
	 * the plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	public PlaceholderAPISupportManager(RacesAndClasses plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * reloads the Manager.
	 */
	public void reload(){
		HandlerList.unregisterAll(this);
		plugin.registerEvents(this);
		
		if(Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)) registerReplacer();;
	}
	
	@EventHandler
	public void onPluginLoad(PluginEnableEvent event){
		if(event.getPlugin().getName().equalsIgnoreCase(PLUGIN_NAME)){
			registerReplacer();
		}
	}
	
	/**
	 * Registers the replacer.
	 */
	private void registerReplacer(){
		RaCPlaceholderReplacer replacer = new RaCPlaceholderReplacer();
		PlaceholderAPI.registerPlaceholder(plugin, "race", replacer);
		PlaceholderAPI.registerPlaceholder(plugin, "class", replacer);
	}
	
}
