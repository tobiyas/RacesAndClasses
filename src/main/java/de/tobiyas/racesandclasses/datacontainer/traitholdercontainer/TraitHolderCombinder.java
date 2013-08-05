package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import java.util.HashSet;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;

public class TraitHolderCombinder {
	
	/**
	 * The Plugin to call stuff on
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Checks if the player passed has access to the trait passed.
	 * 
	 * @param playerName the player to check
	 * @param trait to check against
	 * 
	 * @return true if the player has access to the trait passed, false otherwise
	 */
	public static boolean checkContainer(String playerName, Trait trait){
		AbstractTraitHolder holder = trait.getTraitHolder();
		
		AbstractTraitHolder raceHolder = plugin.getRaceManager().getHolderOfPlayer(playerName);
		if(raceHolder == null) return false;
		if(holder == raceHolder){
			return true;
		}
		
		AbstractTraitHolder classHolder = plugin.getClassManager().getHolderOfPlayer(playerName);
		if(classHolder == null) return false;
		if(holder == classHolder){
			return true;
		}
		
		return false;
	}

	/**
	 * Returns a Set of all Traits that a player has.
	 * This combines Race- + Class-Traits
	 * 
	 * @param player to check
	 
	 * @return set of all Traits of player
	 */
	public static Set<Trait> getAllTraitsOfPlayer(String player){
		Set<Trait> traits = new HashSet<Trait>();
		
		AbstractTraitHolder raceContainer = plugin.getRaceManager().getHolderOfPlayer(player);
		if(raceContainer != null){
			traits.addAll(raceContainer.getTraits());
		}
			
		AbstractTraitHolder classContainer = plugin.getClassManager().getHolderOfPlayer(player);
		if(classContainer != null){
			traits.addAll(classContainer.getTraits());
		}
			
		return traits;
	}
	
	/**
	 * Returns all visible Traits of a Player.
	 * This combines Race- and Class-Traits
	 * 
	 * @param player to check
	 * 
	 * @return a set of Traits
	 */
	public static Set<Trait> getVisibleTraitsOfPlayer(String player){
		Set<Trait> traits = new HashSet<Trait>();
		
		AbstractTraitHolder raceContainer = plugin.getRaceManager().getHolderOfPlayer(player);
		if(raceContainer != null){
			traits.addAll(raceContainer.getVisibleTraits());
		}
		
		AbstractTraitHolder classContainer = plugin.getClassManager().getHolderOfPlayer(player);
		if(classContainer != null){
			traits.addAll(classContainer.getVisibleTraits());
		}
		
		return traits;
	}
	
	/**
	 * Gets all Traits of Player.
	 * This includes Races- and Classes-Trait.
	 * 
	 * It is filtered for doubled Traits and only the strong ones survive. ;)
	 * 
	 * @param player to check
	 *
	 * @return a set of Traits
	 */
	public static Set<Trait> getReducedTraitsOfPlayer(String player){
		Set<Trait> traits = getAllTraitsOfPlayer(player);
		traits = filterForDoubles(traits);
		return traits;
	}
	
	/**
	 * Gets all Traits of Player.
	 * This includes Races- and Classes-Trait.
	 * Only visible Traits will be returned.
	 * 
	 * It is filtered for doubled Traits and only the strong ones survive. ;)
	 * 
	 * @param player to check
	 *
	 * @return a set of Traits
	 */
	public static Set<Trait> getReducedVisibleTraitsOfPlayer(String player){
		Set<Trait> traits = getVisibleTraitsOfPlayer(player);
		traits = filterForDoubles(traits);
		return traits;
	}
	
	/**
	 * Filters a Set of Traits to only contain max. 1 of each Trait.
	 * It removes the weaker trait/s if a trait is contained more than once.
	 * 
	 * @param traits to check
	 *
	 * @return a cleaned Set of Traits
	 */
	private static Set<Trait> filterForDoubles(Set<Trait> traits){
		Set<Trait> filtered = new HashSet<Trait>();
		for(Trait trait : traits){
			Trait doubled = containsTrait(filtered, trait);
			if(doubled == null)
				filtered.add(trait);
			else{
				filtered.remove(doubled);
				filtered.add(selectBetter(doubled, trait));
			}
				
		}
		return filtered;
	}
	
	/**
	 * Compares two Traits and returns the better one
	 * 
	 * @param trait1 to test against
	 * @param trait2 to test against
	 * 
	 * @return the better Trait of boath 
	 */
	private static Trait selectBetter(Trait trait1, Trait trait2){
		if(trait1.isBetterThan(trait2))
			return trait1;
		return trait2;
	}
	
	
	/**
	 * Checks if the Set of traits already contains the Trait passed.
	 * If it is contained, the trait that is identical is returned.
	 * If it is not contained, null is returned.
	 * 
	 * Null arguments are supported for Trait and Set, Null is returned.
	 * 
	 * @param traits to search in
	 * @param newTrait to search for
	 * 
	 * @return see description
	 */
	private static Trait containsTrait(Set<Trait> traits, Trait newTrait){
		if(newTrait == null || traits == null){
			return null;
		}
			
		for(Trait trait : traits){
			if(trait != null && sameTrait(trait, newTrait)) return trait;
		}
		
		return null;
	}
	
	/**
	 * Checks if the Traits have the same Name. Then it is assumed it is the same Trait.
	 * Returns true if they have the same Name, False otherwise.
	 * 
	 * @param trait1 to check against
	 * @param trait2 to check against
	 * 
	 * @return if the Traits have the save Name
	 */
	private static boolean sameTrait(Trait trait1, Trait trait2){
		return trait1.getName().equalsIgnoreCase(trait2.getName());
	}
}
