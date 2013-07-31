package de.tobiyas.racesandclasses.configuration.traits;

import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.racesandclasses.util.consts.Consts;

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

	public double getDouble(String string, double defaultValue) {
		Object ret = getValue(string, defaultValue);
		if(ret instanceof Integer){
			return ((Integer) ret).doubleValue();
		}
		
		if(ret instanceof Double)
			return (Double) ret;
		
		return defaultValue;
	}
}
