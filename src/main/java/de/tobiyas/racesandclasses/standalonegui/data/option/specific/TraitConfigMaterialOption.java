package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import org.bukkit.Material;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;

public class TraitConfigMaterialOption extends AbstractTraitConfigOption {

	/**
	 * The Value to set.
	 */
	private Material mat = Material.AIR;
	
	
	public TraitConfigMaterialOption(String name, boolean optional) {
		super(OptionType.Material, name, optional);
		
		this.options.clear();
		for(Material mat : Material.values()){
			this.options.add(mat.name());
		}
	}

	
	public TraitConfigMaterialOption(String name, boolean optional, Material value) {
		this(name, optional);
		
		this.mat = value;
	}
	

	
	@Override
	public void valueSelected(String value) {
		try{
			this.mat = Material.matchMaterial(value.toUpperCase());
		}catch(Throwable exp){}
	}

	@Override
	public String getCurrentSelection() {
		return mat.name();
	}


	@Override
	public String toString() {
		return name + ": " + mat.name();
	}
	
}
