package de.tobiyas.racesandclasses.traitcontainer.container;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.ArmorTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.DeathCheckerTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.NormalArrow;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.STDAxeDamageTrait;

public class TraitsList{
	
	private static HashSet<TraitInfoContainer> traits;
	private static HashSet<String> categorys;
	
	public static void initTraits(){
		traits = new HashSet<TraitInfoContainer>();
		categorys = new HashSet<String>();
		categorys.add("None");
		
		//Passives
		//addTraitToList("DamageReduceTrait", DamageReduce.class, "passive");
		//addTraitToList("DamageIncreaseTrait", DamageIncrease.class, "passive");
		//addTraitToList("HungerReplenishTrait", HungerReplenish.class, "passive");
		//addTraitToList("RegenerationTrait", RegenerationTrait.class, "passive");
		//addTraitToList("DwarfSkinTrait", DwarfSkinTrait.class, "passive");
		//addTraitToList("BerserkerRageTrait", BerserkerRageTrait.class, "passive");
		//addTraitToList("LastStandTrait", LastStandTrait.class, "passive");
		
		//Weapons
		//addTraitToList("AxeDamageIncreaseTrait", AxeDamageIncrease.class, "weapons");
		//addTraitToList("SwordDamageIncreaseTrait", SwordDamageIncrease.class, "weapons");
		//addTraitToList("ArrowDamageIncreaseTrait", ArrowDamageIncrease.class, "weapons");
		
		//AbstractResistance
		//addTraitToList("FireResistanceTrait", FireResistanceTrait.class, "resistances");
		//addTraitToList("DrainResistanceTrait", DrainResistanceTrait.class, "resistances");
		//addTraitToList("FallResistanceTrait", FallResistanceTrait.class, "resistances");
		
		//Activate
		//addTraitToList("SprintTrait", SprintTrait.class, "activate");
		//addTraitToList("TrollbloodTrait", TrollbloodTrait.class, "activate");
		//addTraitToList("HealOthersTrait", HealOthersTrait.class, "activate");
		
		//Arrows
		//addTraitToList("FireArrowTrait", FireArrowTrait.class, "arrow");
		//addTraitToList("TeleportArrowTrait", TeleportArrowTrait.class, "arrow");
		//addTraitToList("ExplosiveArrowTrait", ExplosiveArrowTrait.class, "arrow");
		//addTraitToList("PoisonArrowTrait", PoisonArrowTrait.class, "arrow");
		
		//STD
		addTraitToList("DeathCheckerTrait", DeathCheckerTrait.class, "STD", false);
		addTraitToList("STDAxeDamageTrait", STDAxeDamageTrait.class, "STD", false);
		addTraitToList("NormalArrow", NormalArrow.class, "STD", false);
		addTraitToList("ArmorTrait", ArmorTrait.class, "STD", false);
		
		TraitStore.importFromFileSystem();
	}
	
	public static void addTraitToList(String trait, Class<?> traitClass, String category){
		addTraitToList(trait, traitClass, category, true);
	}
	
	public static void addTraitToList(String trait, Class<?> traitClass, String category, boolean visible){
		traits.add(new TraitInfoContainer(trait, traitClass, category, visible));
		if(!categorys.contains(category))
			categorys.add(category);
	}
	
	public static Class<?> getClassOfTrait(String name){
		for(TraitInfoContainer container : traits)
			if(container.getName().equalsIgnoreCase(name))
				return container.getClazz();
		
		return null;
	}
	
	public static List<String> getAllVisibleTraits(){
		LinkedList<String> list = new LinkedList<String>();
		for(TraitInfoContainer trait : traits)
			if(trait.isVisible())
				list.add(trait.getName());
		
		return list;
	}
	
	public static List<String> getAllTraits(){
		LinkedList<String> list = new LinkedList<String>();
		for(TraitInfoContainer trait : traits)
			list.add(trait.getName());
		
		return list;
	}
	
	public static HashSet<String> getCategory(String category){
		if(categorys.contains(category))
			return getAllOfCategory(category);
		
		int page = 0;
		try{
			page = Integer.valueOf(category);
		}catch(NumberFormatException e){
		}
		
		int i = 0;
		if(page >= categorys.size())
			page = 0;
			
		for(String categoryName : categorys){
			if(i == page)
				return getAllOfCategory(categoryName);
			i++;
		}
		
		return null;
	}
	
	private static HashSet<String> getAllOfCategory(String category){
		HashSet<String> set = new HashSet<String>();
		for(TraitInfoContainer container : traits)
			if(container.getCategory().equalsIgnoreCase(category))
				set.add(container.getName());
		
		return set;
	}

	public static HashSet<String> getAllCategories() {		
		return categorys;
	}
}
