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
	private List<TraitConfigOption> traitConfiguration = new LinkedList<TraitConfigOption>();

	
	
	public GuiTrait(String traitType, List<TraitConfigOption> options) {
		this.traitType = traitType;
		this.traitConfiguration.addAll(options);
	}
	
	
	public String getTraitType() {
		return traitType;
	}

	public void setTraitType(String traitType) {
		this.traitType = traitType;
	}

	public List<TraitConfigOption> getTraitConfiguration() {
		return traitConfiguration;
	}


	@Override
	public int compareTo(GuiTrait o) {
		return traitType.compareTo(o.getTraitType());
	}


	
}
