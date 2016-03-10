/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.skilltree.PlayerSkillTreeManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitHolderCombinder {

	
	/**
	 * If the SkillSystem is used.
	 */
	private static boolean useSkillSystem(){
		return RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_useSkillSystem();
	}
	
	
	/**
	 * Checks if the player passed has access to the trait passed.
	 * 
	 * @param playerRaCPlayer the player to check
	 * @param trait to check against
	 * 
	 * @return true if the player has access to the trait passed, false otherwise
	 */
	public static boolean checkContainer(RaCPlayer player, Trait trait){
		return checkContainer(player, trait, false);
	}
	
	/**
	 * Checks if the player passed has access to the trait passed.
	 * 
	 * @param playerRaCPlayer the player to check
	 * @param trait to check against
	 * 
	 * @return true if the player has access to the trait passed, false otherwise
	 */
	public static boolean checkContainer(RaCPlayer player, Trait trait, boolean ignoreSkilling){
		//Check if player has skill first + check if not permanent:
		if(!ignoreSkilling && useSkillSystem()) {
			if(player.getSkillTreeManager().getLevel(trait)>0) return true;
			if(!trait.isPermanentSkill()) return false;
		}
		
		//Rest, check if holder == player holder.
		Set<AbstractTraitHolder> holder = trait.getTraitHolders();
		if(holder == null || holder.isEmpty()) return true;
		
		AbstractTraitHolder raceHolder = player.getRace();
		if(holder.contains(raceHolder)){
			return true;
		}
		
		AbstractTraitHolder classHolder = player.getclass();
		if(holder.contains(classHolder)) return true;
		
		return false;
	}

	/**
	 * Returns a Set of all Traits that a player has.
	 * This combines Race- + Class-Traits
	 * 
	 * @param offlinePlayer to check
	 
	 * @return set of all Traits of player
	 */
	public static Set<Trait> getAllTraitsOfPlayer(RaCPlayer player){
		Set<Trait> traits = new HashSet<Trait>();
		
		AbstractTraitHolder raceContainer = player.getRace();
		if(raceContainer != null){
			traits.addAll(raceContainer.getTraits());
		}
			
		AbstractTraitHolder classContainer = player.getclass();
		if(classContainer != null){
			traits.addAll(classContainer.getTraits());
		}
			
		return traits;
	}
	
	/**
	 * Returns a Set of all Traits that a player has.
	 * This combines Race- + Class-Traits.
	 * <br>This gets all Permanent + learned spells!
	 * 
	 * @param offlinePlayer to check
	 
	 * @return set of all Traits of player
	 */
	public static Set<Trait> getSkillTreeReducedTraitsOfPlayer(RaCPlayer player){
		Set<Trait> traits = getReducedTraitsOfPlayer(player);
		
		
		//Filter for SkillSystem.
		if(useSkillSystem()){
			PlayerSkillTreeManager skillTreeManager = player.getSkillTreeManager();
			Iterator<Trait> it = traits.iterator();
			while(it.hasNext()) {
				Trait check = it.next();
				if(check.isPermanentSkill()) continue;
				if(skillTreeManager.getLevel(check) > 0) continue;
				
				//Does not have and is not permanent!
				it.remove();
			}
		}
		
		filterForReplacementTraits(traits);
		return traits;
	}
	
	
	/**
	 * Filters out replacement Traits.
	 * @param traits to filter.
	 */
	private static void filterForReplacementTraits(Collection<Trait> traits){
		if(traits.isEmpty()) return;
		
		Set<String> toRemove = new HashSet<>();
		for(Trait trait : traits) toRemove.addAll(trait.getReplacesOtherTraits());
		
		if(toRemove.isEmpty()) return;
		Iterator<Trait> traitIt = traits.iterator();
		while(traitIt.hasNext()){
			Trait trait = traitIt.next();
			if(trait != null && toRemove.contains(trait.getDisplayName())){
				traitIt.remove();
			}
		}
	}

	
	/**
	 * Gets all Traits of Player.
	 * This includes Races- and Classes-Trait.
	 * 
	 * It is filtered for doubled Traits and only the strong ones survive. ;)
	 * 
	 * @param offlinePlayer to check
	 *
	 * @return a set of Traits
	 */
	public static Set<Trait> getReducedTraitsOfPlayer(RaCPlayer offlinePlayer){
		Set<Trait> traits = getAllTraitsOfPlayer(offlinePlayer);
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
			if(trait.isStackable()){
				filtered.add(trait);
				continue;
			}
			
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
