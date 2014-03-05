/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.configuration.traits;

import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

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
