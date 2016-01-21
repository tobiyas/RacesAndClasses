package de.tobiyas.racesandclasses.addins.placeholderapisupport;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.APIs.ManaAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;

public class MVdWRaCPlaceholderReplacer implements PlaceholderReplacer {
	
	/**
	 * the plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	private final DecimalFormat format = new DecimalFormat("0.0");
	
	
	public MVdWRaCPlaceholderReplacer(RacesAndClasses plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
		String placeholder = event.getPlaceholder();
		Player player = event.getPlayer();
		
		if(placeholder.equalsIgnoreCase("race")) return RaceAPI.getRaceNameOfPlayer(player);
		if(placeholder.equalsIgnoreCase("class")) return ClassAPI.getClassNameOfPlayer(player);
		
		if(placeholder.equalsIgnoreCase("mana")) return format.format(ManaAPI.getCurrentMana(player));
		if(placeholder.equalsIgnoreCase("maxmana")) return format.format(ManaAPI.getCurrentMana(player));

		if(placeholder.equalsIgnoreCase("level")) return String.valueOf(LevelAPI.getCurrentLevel(player));
		if(placeholder.equalsIgnoreCase("exp")) return format.format(LevelAPI.getCurrentExpOfLevel(player));
		if(placeholder.equalsIgnoreCase("maxexp")) return format.format(LevelAPI.getMaxEXPToNextLevel(player));
		
		return null;
	}
	
	
	/**
	 * Registers the Placeholder.
	 */
	public void register(){
		PlaceholderAPI.registerPlaceholder(plugin, "race", this);
		PlaceholderAPI.registerPlaceholder(plugin, "class", this);
		
		PlaceholderAPI.registerPlaceholder(plugin, "mana", this);
		PlaceholderAPI.registerPlaceholder(plugin, "maxmana", this);
		
		PlaceholderAPI.registerPlaceholder(plugin, "level", this);
		PlaceholderAPI.registerPlaceholder(plugin, "exp", this);
		PlaceholderAPI.registerPlaceholder(plugin, "maxexp", this);
	}
	
	
	/**
	 * Unregisters the Placeholder.
	 */
	public void unregister(){
		//TODO unregister somehow?!? seems no API for that.
	}


	/**
	 * Does a simple replace on the Placeholder API.
	 */
	public static String replace(Player player, String toReplace) {
		return PlaceholderAPI.replacePlaceholders(player, toReplace);
	}

}
