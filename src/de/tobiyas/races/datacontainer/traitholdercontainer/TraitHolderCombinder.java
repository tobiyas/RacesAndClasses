package de.tobiyas.races.datacontainer.traitholdercontainer;

import java.util.HashSet;

import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;

public class TraitHolderCombinder {

	public static HashSet<Trait> getAllTraitsOfPlayer(String player){
		HashSet<Trait> traits = new HashSet<Trait>();
		
		RaceContainer raceContainer = RaceManager.getManager().getRaceOfPlayer(player);
		if(raceContainer != null)
			traits.addAll(raceContainer.getTraits());
		
		ClassContainer classContainer = ClassManager.getInstance().getClassOfPlayer(player);
		if(classContainer != null)
			traits.addAll(classContainer.getTraits());
		
		return traits;
	}
	
	public static HashSet<Trait> getVisibleTraitsOfPlayer(String player){
		HashSet<Trait> traits = new HashSet<Trait>();
		
		RaceContainer raceContainer = RaceManager.getManager().getRaceOfPlayer(player);
		if(raceContainer != null)
			traits.addAll(raceContainer.getVisibleTraits());
		
		ClassContainer classContainer = ClassManager.getInstance().getClassOfPlayer(player);
		if(classContainer != null)
			traits.addAll(classContainer.getVisibleTraits());
		
		return traits;
	}
	
	public static HashSet<Trait> getReducedTraitsOfPlayer(String player){
		HashSet<Trait> traits = getAllTraitsOfPlayer(player);
		traits = filterForDoubles(traits);
		return traits;
	}
	
	public static HashSet<Trait> getReducedVisibleTraitsOfPlayer(String player){
		HashSet<Trait> traits = getVisibleTraitsOfPlayer(player);
		traits = filterForDoubles(traits);
		return traits;
	}
	
	private static HashSet<Trait> filterForDoubles(HashSet<Trait> traits){
		HashSet<Trait> filtered = new HashSet<Trait>();
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
	
	private static Trait selectBetter(Trait trait1, Trait trait2){
		Object value1 = trait1.getValue();
		Object value2 = trait2.getValue();
		
		if(value1 instanceof Integer){
			int intValue1 = (int) value1;
			int intValue2 = (int) value2;
			
			return (intValue1 > intValue2 ? trait1 : trait2);
		}
		
		if(value1 instanceof Double){
			double intValue1 = (double) value1;
			double intValue2 = (double) value2;
			
			return (intValue1 > intValue2 ? trait1 : trait2);
		}
		
		return trait1;
	}
	
	private static Trait containsTrait(HashSet<Trait> traits, Trait newTrait){
		for(Trait trait : traits)
			if(sameTrait(trait, newTrait)) return trait;
		return null;
	}
	
	private static boolean sameTrait(Trait trait1, Trait trait2){
		return trait1.getName().equalsIgnoreCase(trait2.getName());
	}
}
