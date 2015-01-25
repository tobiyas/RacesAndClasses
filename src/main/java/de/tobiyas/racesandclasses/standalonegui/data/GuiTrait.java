package de.tobiyas.racesandclasses.standalonegui.data;

import java.util.LinkedList;
import java.util.List;

import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;

public class GuiTrait implements Comparable<GuiTrait> {

	
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


	
}
