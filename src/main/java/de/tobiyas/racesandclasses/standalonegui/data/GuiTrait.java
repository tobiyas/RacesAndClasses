package de.tobiyas.racesandclasses.standalonegui.data;

import java.util.LinkedList;
import java.util.List;

import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;
import de.tobiyas.util.config.YAMLConfigExtended;

public class GuiTrait implements Comparable<GuiTrait>, NeedsSave {
	
	
	/**
	 * The Type of trait.
	 */
	private String traitType = null;
	
	/**
	 * The Configuration of the Trait.
	 */
	private List<TraitConfigOption> traitConfigurationNeeded = new LinkedList<TraitConfigOption>();
	
	/**
	 * The Configuration of the Trait.
	 */
	private List<TraitConfigOption> traitConfigurationOptional = new LinkedList<TraitConfigOption>();
	
	/**
	 * The belonging race.
	 */
	private GuiRace belongingRace = null;
	
	/**
	 * The belonging class.
	 */
	private GuiClass belongingClass = null;
	
	
	
	public GuiTrait(String traitType, List<TraitConfigOption> needed, List<TraitConfigOption> optional) {
		this.traitType = traitType;
		this.traitConfigurationNeeded.addAll(needed);
		this.traitConfigurationOptional.addAll(optional);
	}
	
	
	
	public GuiRace getBelongingRace() {
		return belongingRace;
	}

	public void setBelongingRace(GuiRace belongingRace) {
		this.belongingRace = belongingRace;
	}

	public GuiClass getBelongingClass() {
		return belongingClass;
	}

	public void setBelongingClass(GuiClass belongingClass) {
		this.belongingClass = belongingClass;
	}







	public String getTraitType() {
		return traitType;
	}

	public void setTraitType(String traitType) {
		this.traitType = traitType;
	}

	public List<TraitConfigOption> getTraitConfigurationNeeded() {
		return traitConfigurationNeeded;
	}
	

	public List<TraitConfigOption> getTraitConfigurationOptional() {
		return traitConfigurationOptional;
	}

	
	/**
	 * Removes this race from the Parent.
	 */
	public void removeFromParent(){
		if(belongingClass != null) belongingClass.removeTrait(this);
		if(belongingRace != null) belongingRace.removeTrait(this);
	}

	@Override
	public int compareTo(GuiTrait o) {
		return traitType.compareTo(o.getTraitType());
	}


	@Override
	public boolean needsSave() {
		for(TraitConfigOption config : traitConfigurationNeeded){
			if(config.needsSave()) return true;
		}
		
		return false;
	}



	/**
	 * Saves self to the Traits section of the Config.
	 * 
	 * @param ymlConfig to use
	 * @param string the pre, including the traits section and a Period.
	 */
	public void saveTo(YAMLConfigExtended ymlConfig, String pre) {
		pre += traitType;
		String addon = "";
		
		int i = 0;
		while(ymlConfig.contains(pre+addon)){
			i++;
			addon = "#"+i;
		}
		
		//adapt to trait type.
		pre += addon;
		ymlConfig.createSection(pre);
		pre += ".";
		
		for(TraitConfigOption option : traitConfigurationNeeded){
			ymlConfig.set(pre + option.getName(), option.getCurrentSelection());
			option.notifySaved();
		}

		for(TraitConfigOption option : traitConfigurationOptional){
			if(!option.isCreated()) continue;

			ymlConfig.set(pre + option.getName(), option.getCurrentSelection());
			option.notifySaved();
		}
	}
	
}
