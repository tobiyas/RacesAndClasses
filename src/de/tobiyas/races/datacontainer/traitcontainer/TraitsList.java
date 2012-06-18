package de.tobiyas.races.datacontainer.traitcontainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.tobiyas.races.datacontainer.traitcontainer.traits.activate.HealOthersTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.activate.SprintTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.activate.TrollbloodTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.arrows.ExplosiveArrowTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.arrows.FireArrowTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.arrows.PoisonArrowTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.arrows.TeleportArrowTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.ArrowDamageIncrease;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.AxeDamageIncrease;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.BerserkerRageTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.DamageIncrease;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.DamageReduce;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.DwarfSkinTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.HungerReplenish;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.RegenerationTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.passive.SwordDamageIncrease;
import de.tobiyas.races.datacontainer.traitcontainer.traits.resistance.DrainResistanceTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.resistance.FallResistanceTrait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.resistance.FireResistanceTrait;

public class TraitsList{
	
	private static HashMap<String, Class<?>> traits;
	private static HashMap<String, HashSet<String>> categorys;
	
	public static void initTraits(){
		traits = new HashMap<String, Class<?>>();
		categorys = new HashMap<String, HashSet<String>>();
		categorys.put("None", new HashSet<String>());
		
		//Passives
		addTraitToList("DamageReduceTrait", DamageReduce.class, "passive");
		addTraitToList("DamageIncreaseTrait", DamageIncrease.class, "passive");
		addTraitToList("HungerReplenishTrait", HungerReplenish.class, "passive");
		//addTraitToList("ArmorTrait", ArmorTrait.class);
		addTraitToList("RegenerationTrait", RegenerationTrait.class, "passive");
		addTraitToList("DwarfSkinTrait", DwarfSkinTrait.class, "passive");
		addTraitToList("BerserkerRageTrait", BerserkerRageTrait.class, "passive");
		
		//Weapons
		addTraitToList("AxeDamageIncreaseTrait", AxeDamageIncrease.class, "weapons");
		addTraitToList("SwordDamageIncreaseTrait", SwordDamageIncrease.class, "weapons");
		addTraitToList("ArrowDamageIncreaseTrait", ArrowDamageIncrease.class, "weapons");
		
		//Resistance
		addTraitToList("FireResistanceTrait", FireResistanceTrait.class, "resistances");
		addTraitToList("DrainResistanceTrait", DrainResistanceTrait.class, "resistances");
		addTraitToList("FallResistanceTrait", FallResistanceTrait.class, "resistances");
		
		//Activate
		addTraitToList("SprintTrait", SprintTrait.class, "activate");
		addTraitToList("TrollbloodTrait", TrollbloodTrait.class, "activate");
		addTraitToList("HealOthersTrait", HealOthersTrait.class, "activate");
		
		//Arrows
		addTraitToList("FireArrowTrait", FireArrowTrait.class, "arrow");
		addTraitToList("TeleportArrowTrait", TeleportArrowTrait.class, "arrow");
		addTraitToList("ExplosiveArrowTrait", ExplosiveArrowTrait.class, "arrow");
		addTraitToList("PoisonArrowTrait", PoisonArrowTrait.class, "arrow");
	}
	
	public static void addTraitToList(String trait, Class<?> traitClass){
		traits.put(trait, traitClass);
	}
	
	public static void addTraitToList(String trait, Class<?> traitClass, String category){
		addTraitToList(trait, traitClass);
		if(!categorys.containsKey(category))
			categorys.put(category, new HashSet<String>());
		
		categorys.get(category).add(trait);
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
	
	public static HashSet<String> getCategory(String category){
		if(categorys.containsKey(category))
			return categorys.get(category);
		
		int page = 0;
		try{
			page = Integer.valueOf(category);
		}catch(NumberFormatException e){
		}
		
		int i = 0;
		if(page >= categorys.size())
			page = 0;
			
		for(String categoryInt : categorys.keySet())
			if(i == page)
				return categorys.get(categoryInt);
		
		return null;
	}

	public static HashSet<String> getAllCategories() {
		HashSet<String> cat = new HashSet<String>();
		for(String name : categorys.keySet())
			cat.add(name);
		
		return cat;
	}
}
