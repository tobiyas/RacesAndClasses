package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;

public class TraitConfigBooleanOption extends AbstractTraitConfigOption {

	/**
	 * The Value to set.
	 */
	private boolean value = false;
	
	
	public TraitConfigBooleanOption(String name, boolean optional) {
		super(OptionType.Boolean, name, optional);
		
		this.setOptions(new String[]{"true", "false"});
	}

	
	public TraitConfigBooleanOption(String name, boolean optional, boolean value) {
		this(name, optional);
		
		this.value = value;
	}
	

	@Override
	public void valueSelected(String value) {
		try{
			this.value = Boolean.parseBoolean(value);
		}catch(Throwable exp){}
	}

	
	@Override
	public String getCurrentSelection() {
		return Boolean.toString(value);
	}
	
	
	@Override
	public boolean isAcceptable(String value) {
		if(super.isAcceptable(value)) return true;
		
		return value.equals("true") || value.equals("false");
	}
	
	@Override
	public String toString() {
		return name + ": " + value;
	}
	

}
