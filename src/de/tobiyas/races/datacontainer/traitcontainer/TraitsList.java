package de.tobiyas.races.datacontainer.traitcontainer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.tobiyas.races.datacontainer.traitcontainer.traits.activate.SprintTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.activate.TrollbloodTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.arrows.FireArrowTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.DamageIncrease;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.DamageReduce;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.HungerReplenish;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.ArmorTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.RegenerationTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.resistance.DrainResistanceTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.resistance.FallResistanceTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.resistance.FireResistanceTrait;

public class TraitsList{
	
	public static HashMap<String, Class<?>> traits;
	
	public static void initTraits(){
		traits = new HashMap<String, Class<?>>();
		
		//Passives
		addTraitToList("DamageReduceTrait", DamageReduce.class);
		addTraitToList("DamageIncreaseTrait", DamageIncrease.class);
		addTraitToList("HungerReplenishTrait", HungerReplenish.class);
		addTraitToList("ArmorTrait", ArmorTrait.class);
		addTraitToList("RegenerationTrait", RegenerationTrait.class);
		
		//Resistance
		addTraitToList("FireResistanceTrait", FireResistanceTrait.class);
		addTraitToList("DrainResistanceTrait", DrainResistanceTrait.class);
		addTraitToList("FallResistanceTrait", FallResistanceTrait.class);
		
		//Activate
		addTraitToList("SprintTrait", SprintTrait.class);
		addTraitToList("TrollbloodTrait", TrollbloodTrait.class);
		
		
		//Arrows
		addTraitToList("FireArrowTrait", FireArrowTrait.class);
	}
	
	public static void addTraitToList(String trait, Class<?> traitClass){
		traits.put(trait, traitClass);
	}
	
	public static Class<?> getClassOfTrait(String name){
		return TraitsList.traits.get(name);
	}
	
	public static List<String> getAllTraits(){
		LinkedList<String> list = new LinkedList<String>();
		for(String trait : traits.keySet())
			list.add(trait);
		
		return list;
	}
}
