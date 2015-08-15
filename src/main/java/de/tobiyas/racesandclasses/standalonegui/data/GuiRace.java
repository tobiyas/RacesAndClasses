package de.tobiyas.racesandclasses.standalonegui.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigStringOption;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.items.ItemUtils.ItemQuality;

public class GuiRace implements Comparable<GuiRace>, NeedsSave {
	
	/**
	 * THe Traits of the Race
	 */
	private final Set<GuiTrait> traits = new HashSet<GuiTrait>();
	
	/**
	 * The config of the Race.
	 */
	private final YAMLConfigExtended ymlConfig;
	
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
	
	/**
	 * if we need to save.
	 */
	private boolean needsSave = false;
	
	
	
	public GuiRace(YAMLConfigExtended raceConfig, String raceName, String raceNodeName, String tag, String manaBonus, String armor, Set<GuiTrait> traits) {
		this.ymlConfig = raceConfig;
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

		this.needsSave = true;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;

		this.needsSave = true;
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
		
		this.needsSave = true;
	}
	
	public void removeTrait(GuiTrait trait){
		this.traits.remove(trait);

		this.needsSave = true;
	}
	
	

	public List<TraitConfigOption> getConfig() {
		return config;
	}
	
	
	/**
	 * Saves to YML file.
	 */
	public void save(){
		if(!needsSave()) return;
		
		//First clear the config.
		ymlConfig.clearConfig();
		
		//second save config!
		for( TraitConfigOption option : config ){
			ymlConfig.set(raceNodeName + ".config." + option.getName(), option.getCurrentSelection());
			option.notifySaved();
		}
		
		//Last save the Traits:
		for( GuiTrait trait : traits ){
			trait.saveTo(ymlConfig, raceNodeName + ".traits.");
		}
		
		ymlConfig.save();
		this.needsSave = false;
	}
	
	
	@Override
	public boolean needsSave() {
		if( needsSave ) return true;
		
		for(GuiTrait trait : traits){
			if(trait.needsSave()) return true;
		}
		
		for( TraitConfigOption option : config ){
			if(option.needsSave()) return true;
		}
		
		return false;
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
