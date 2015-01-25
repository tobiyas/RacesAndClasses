package de.tobiyas.racesandclasses.standalonegui.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigStringOption;
import de.tobiyas.util.items.ItemUtils.ItemQuality;

public class GuiRace implements Comparable<GuiRace> {
	
	
	/**
	 * THe Traits of the Race
	 */
	private final Set<GuiTrait> traits = new HashSet<GuiTrait>();
	
	/**
	 * The Name of the Race
	 */
	private String raceName = "NONE";
	
	/**
	 * The NodeName of the Race
	 */
	private String raceNodeName = "NONE";
	
	/**
	 * The Armor permissions.
	 */
	private final Set<ItemQuality> armor = new HashSet<ItemQuality>();
	
	/**
	 * The Config to use.
	 */
	private final List<TraitConfigOption> config = new LinkedList<TraitConfigOption>();
	
	
	
	public GuiRace(String raceName, String raceNodeName, String tag, String manaBonus, String armor, Set<GuiTrait> traits) {
		this.traits.addAll(traits);
		this.raceNodeName = raceNodeName;
		this.raceName = raceName;
		
		//parse Armor:
		armor = armor.toLowerCase();
		if(armor.contains("leather")) this.armor.add(ItemQuality.Leather);
		if(armor.contains("iron")) this.armor.add(ItemQuality.Iron);
		if(armor.contains("chain")) this.armor.add(ItemQuality.Chain);
		if(armor.contains("gold")) this.armor.add(ItemQuality.Gold);
		if(armor.contains("diamond")) this.armor.add(ItemQuality.Diamond);
		if(armor.contains("all")) for(ItemQuality i : ItemQuality.values()) this.armor.add(i);
		
		for(GuiTrait trait : traits) trait.setBelongingRace(this);
		
		//setting the config.
		this.config.add(new TraitConfigStringOption("name", true, raceName));
		this.config.add(new TraitConfigStringOption("manaBonus", false, manaBonus));
		this.config.add(new TraitConfigStringOption("tag", true, tag));
		
		Collections.sort(config);
	}
	
	
	public String getRaceName() {
		return raceName;
	}
	
	public String getRaceNodeName() {
		return raceNodeName;
	}

	public void setRaceNodeName(String raceNodeName) {
		this.raceNodeName = raceNodeName;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}
	

	public Set<ItemQuality> getArmor() {
		return armor;
	}


	public Set<GuiTrait> getTraits() {
		return traits;
	}
	
	public void addTrait(GuiTrait trait){
		trait.setBelongingRace(this);
		this.traits.add(trait);
	}
	
	public void removeTrait(GuiTrait trait){
		this.traits.remove(trait);
	}
	
	

	public List<TraitConfigOption> getConfig() {
		return config;
	}
	
	@Override
	public String toString() {
		return raceName;
	}
	

	@Override
	public int compareTo(GuiRace o) {
		return raceName.compareTo(o.raceName);
	}
	
}
