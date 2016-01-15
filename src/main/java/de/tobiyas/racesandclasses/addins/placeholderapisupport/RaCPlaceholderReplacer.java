package de.tobiyas.racesandclasses.addins.placeholderapisupport;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;

public class RaCPlaceholderReplacer implements PlaceholderReplacer {
	
	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
		String placeholder = event.getPlaceholder();
		Player player = event.getPlayer();
		
		if(placeholder.equalsIgnoreCase("race")) return RaceAPI.getRaceNameOfPlayer(player);
		if(placeholder.equalsIgnoreCase("class")) return ClassAPI.getClassNameOfPlayer(player);
		
		return null;
	}

}
