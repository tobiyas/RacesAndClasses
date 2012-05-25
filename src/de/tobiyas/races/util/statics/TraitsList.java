package de.tobiyas.races.util.statics;

import java.util.HashMap;

import de.tobiyas.races.datacontainer.traitcontainer.traits.DamageIncrease;
import de.tobiyas.races.datacontainer.traitcontainer.traits.DamageReduce;

public class TraitsList{
	
	public static HashMap<String, Class<?>> traits;
	
	public static void initTraits(){
		traits = new HashMap<String, Class<?>>();
		
		addTraitToList("DamageReduceTrait", DamageReduce.class);
		addTraitToList("DamageIncreaseTrait", DamageIncrease.class);
	}
	
	private static void addTraitToList(String trait, Class<?> traitClass){
		traits.put(trait, traitClass);
	}
	
	public static Class<?> getClassOfTrait(String name){
		return TraitsList.traits.get(name);
	}
}
