package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;


public class TraitConfigStringOption extends AbstractTraitConfigOption {

	/**
	 * The Current value.
	 */
	private String value = "";
	
	
	public TraitConfigStringOption(String name, boolean optional) {
		super(OptionType.String, name, optional);
	}
	
	public TraitConfigStringOption(String name, boolean optional, String value) {
		this(name, optional);
		
		this.value = value;
	}


	@Override
	public boolean isAcceptable(String value) {
		if(super.isAcceptable(value)) return true;
		
		return true;
	}

	@Override
	public void valueSelected(String value) {
		this.value = value;
	}

	@Override
	public String getCurrentSelection() {
		return value;
	}
	

	@Override
	public String toString() {
		return name + ": " + value;
	}

}
