package de.tobiyas.racesandclasses.standalonegui.data;

import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;

public class Trait {

	
	/**
	 * The Type of trait.
	 */
	private String traitType = null;
	
	/**
	 * The Configuration of the Trait.
	 */
	private TraitConfiguration traitConfiguration = new TraitConfiguration();

	
	
	public String getTraitType() {
		return traitType;
	}

	public void setTraitType(String traitType) {
		this.traitType = traitType;
	}

	public TraitConfiguration getTraitConfiguration() {
		return traitConfiguration;
	}
	
	
	/**
	 * adds stuff to the config.
	 * 
	 * @param key to add
	 * @param value to add
	 */
	public void addToConfig(String key, Object value){
		this.traitConfiguration.put(key, value);
	}
	
}
