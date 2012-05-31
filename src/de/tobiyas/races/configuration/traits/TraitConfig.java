package de.tobiyas.races.configuration.traits;

import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.util.consts.Consts;

public class TraitConfig {

	private YAMLConfigExtended config;
	
	public TraitConfig(String traitName){
		config = new YAMLConfigExtended(Consts.traitConfigDir + traitName + ".yml").load();
	}
	
	public Object getValue(String path, Object defaultValue){
		config.load();
		Object returnValue = config.get(path, defaultValue);
		config.save();
		
		return returnValue;
	}
	
	public void setValue(String path, Object value){
		config.load();
		config.set(path, value);
		config.save();
	}
}
