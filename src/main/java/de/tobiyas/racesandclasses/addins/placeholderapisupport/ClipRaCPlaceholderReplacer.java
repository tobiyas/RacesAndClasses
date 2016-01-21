package de.tobiyas.racesandclasses.addins.placeholderapisupport;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.APIs.ManaAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;

public class ClipRaCPlaceholderReplacer extends PlaceholderHook {
	
	/**
	 * the plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * A small formatter.
	 */
	private final DecimalFormat format = new DecimalFormat("0.0");
	
	
	public ClipRaCPlaceholderReplacer(RacesAndClasses plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * Registers the Placeholder.
	 */
	public void register(){
		PlaceholderAPI.registerPlaceholderHook(plugin, this);
	}
	
	
	/**
	 * Unregisters the Placeholder.
	 */
	public void unregister(){
		PlaceholderAPI.unregisterPlaceholderHook(plugin);
	}


	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if(identifier.equalsIgnoreCase("race")) return RaceAPI.getRaceNameOfPlayer(player);
		if(identifier.equalsIgnoreCase("class")) return ClassAPI.getClassNameOfPlayer(player);
		
		if(identifier.equalsIgnoreCase("mana")) return format.format(ManaAPI.getCurrentMana(player));
		if(identifier.equalsIgnoreCase("maxmana")) return format.format(ManaAPI.getCurrentMana(player));

		if(identifier.equalsIgnoreCase("level")) return String.valueOf(LevelAPI.getCurrentLevel(player));
		if(identifier.equalsIgnoreCase("exp")) return format.format(LevelAPI.getCurrentExpOfLevel(player));
		if(identifier.equalsIgnoreCase("maxexp")) return format.format(LevelAPI.getMaxEXPToNextLevel(player));
		
		return null;
	}

	
	/**
	 * Replaces the Holders in the Key.
	 * @param toReplace the text to replace.
	 * @return the text that is replaced.
	 */
	public static String replace(Player player, String toReplace){
		return PlaceholderAPI.setPlaceholders(player, toReplace);
	}
	
}
