package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import org.bukkit.block.Biome;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;

public class TraitConfigBiomeOption extends AbstractTraitConfigOption {


	/**
	 * The current Biome.
	 */
	private Biome biome = Biome.BEACH;
	
	

	public TraitConfigBiomeOption(String name, boolean optional) {
		super(OptionType.Biome, name, optional);
		
		this.options.clear();
		for(Biome biome : Biome.values()){
			this.options.add(biome.name());
		}
	}
	
	
	public TraitConfigBiomeOption(String name, boolean optional, Biome biome) {
		this(name, optional);
		
		this.biome = biome;
	}
	
	
	@Override
	public void valueSelected(String value) {
		for(Biome biome : Biome.values()){
			if(biome.name().equalsIgnoreCase(value)) {
				this.biome = biome;
			}
		}
	}

	
	@Override
	public String getCurrentSelection() {
		return biome.name();
	}
	
	
	@Override
	public boolean isAcceptable(String value) {
		return super.isAcceptable(value.toUpperCase());
	}
	
	
	@Override
	public String toString() {
		return name + ": " + biome.name();
	}
	
}
