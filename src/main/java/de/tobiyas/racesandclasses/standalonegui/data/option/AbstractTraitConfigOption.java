package de.tobiyas.racesandclasses.standalonegui.data.option;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public abstract class AbstractTraitConfigOption implements TraitConfigOption {

	
	/**
	 * The Name of the option.
	 */
	protected final String name;
	
	/**
	 * The Type this is.
	 */
	protected final OptionType optionType;
	
	/**
	 * The options to choose.
	 */
	protected final Set<String> options = new HashSet<String>();
	
	/**
	 * If the field is optional.
	 */
	protected final boolean optional;
	
	
	public AbstractTraitConfigOption(OptionType type, String name, boolean optional) {
		this.optionType = type;
		this.name = name;
		this.optional = optional;
	}
	
	
	@Override
	public boolean isOptional() {
		return optional;
	}
	
	
	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public OptionType getOptionType() {
		return optionType;
	}
	
	
	@Override
	public Set<String> options() {
		return options;
	}
	
	
	@Override
	public TraitConfigOption setOptions(Set<String> options) {
		this.options.clear();
		this.options.addAll(options);
		
		return this;
	}
	
	@Override
	public TraitConfigOption setOptions(String... options) {
		this.options.clear();
		this.options.addAll(Arrays.asList(options));
		
		return this;
	}

	
	@Override
	public boolean isAcceptable(String value) {
		for(String accepted : options){
			if(value.equalsIgnoreCase(accepted)) return true;
		}
		
		return false;
	}
	

}
