package de.tobiyas.racesandclasses.APIs;

import java.util.Set;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitAPI {

	/**
	 * Checks if the player has the Trait with the following Name.
	 * <br>If false, plugin is not found or the Player does not have the Trait.
	 * 
	 * @param playerName to check
	 * @param traitName to check
	 * 
	 * @return true if has the Trait.
	 */
	public static boolean hasTrait(String playerName, String traitName){
		try{
			Set<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(playerName);
			//no traits -> doesn't have Trait.
			if(traits == null || traits.isEmpty()) return false;
			
			for(Trait trait : traits){
				if(trait.getName().equalsIgnoreCase(traitName)){
					return true;
				}
			}
			
			return false;
		}catch(Throwable exp){ return false; }
	}
}
