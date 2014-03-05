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

import java.io.File;
import java.util.HashMap;

import de.tobiyas.racesandclasses.util.consts.Consts;

public class TraitConfigManager {

	private HashMap<String, TraitConfig> configs;
	
	
	public TraitConfigManager(){
		configs = new HashMap<String, TraitConfig>();
		
		checkDirs();
	}
	
	private void checkDirs(){
		File file = new File(Consts.traitConfigDir);
		if(!file.exists())
			file.mkdirs();
		DefaultTraitConfig.createDefaultTraitConfig();
	}
	
	
	public void reload(){
		configs.clear();
		File configDir = new File(Consts.traitConfigDir);
		
		for(File file : configDir.listFiles()){
			if(file.isDirectory())
				continue;
			
			int index = file.getName().lastIndexOf('.');
			String fileName = file.getName().substring(0, index);
			configs.put(fileName, new TraitConfig(fileName));
		}
	}
	
	
	public TraitConfig getConfigOfTrait(String traitName){
		TraitConfig config = configs.get(traitName);
		return config;
	}
	
}
