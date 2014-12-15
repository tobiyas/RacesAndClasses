package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;

public class TraitConfigDoubleOption extends AbstractTraitConfigOption {


	/**
	 * The Current value.
	 */
	private double value = 0;

	

	public TraitConfigDoubleOption(String name, boolean optional) {
		super(OptionType.Double, name, optional);
	}
	

	public TraitConfigDoubleOption(String name, boolean optional, double value) {
		this(name, optional);
		
		this.value = value;
	}
	
	
	@Override
	public void valueSelected(String value) {
		try{
			this.value = Double.parseDouble(value);
		}catch(Throwable exp){}
	}

	
	@Override
	public String getCurrentSelection() {
		return Double.toString(value);
	}
	
	
	@Override
	public boolean isAcceptable(String value) {
		if(super.isAcceptable(value)) return true;
		
		try{
			Double.parseDouble(value);
			return true;
		}catch(Throwable exp){
			return false;
		}
	}
	
	@Override
	public String toString() {
		return name + ": " + value;
	}
	

}
