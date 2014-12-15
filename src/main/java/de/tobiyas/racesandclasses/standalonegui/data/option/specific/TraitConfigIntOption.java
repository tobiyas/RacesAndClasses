package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;


public class TraitConfigIntOption extends AbstractTraitConfigOption {


	/**
	 * The Option for the int.
	 */
	private int intOption = 0;


	
	public TraitConfigIntOption(String name, boolean optional) {
		super(OptionType.Int, name, optional);
	}
	
	
	public TraitConfigIntOption(String name, boolean optional, int option) {
		this(name, optional);
		
		this.intOption = option;
	}	
	
	
	@Override
	public boolean isAcceptable(String value) {
		if(super.isAcceptable(value)) return true;
		
		try{
			int parsed = Integer.parseInt(value);
			return parsed != Integer.MAX_VALUE;
		}catch(Throwable exp){
			return false;
		}
	}
	
	
	@Override
	public void valueSelected(String value) {
		try{
			int parsed = Integer.parseInt(value);
			this.intOption = parsed;
		}catch(Throwable exp){}
	}


	@Override
	public String getCurrentSelection() {
		return Integer.toString(intOption);
	}
	
	@Override
	public String toString() {
		return name + ": " + intOption;
	}
		

}
