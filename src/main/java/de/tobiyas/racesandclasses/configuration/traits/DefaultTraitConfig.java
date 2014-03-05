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
import java.io.IOException;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.items.CreateDropContainer;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DefaultTraitConfig {

	public static void createDefaultTraitConfig(){
		createTraitConfig("TrollbloodTrait", "trait.uplink", 60, false);
		createTraitConfig("TrollbloodTrait", "trait.iteminhand", 260, true);
		
		createTraitConfig("SprintTrait", "trait.uplink", 40, false);
		createTraitConfig("SprintTrait", "trait.iteminhand", 260, true);
		
		createTraitConfig("TeleportArrowTrait", "trait.uplink", 60, false);
		
		createTraitConfig("STDAxeDamageTrait", "trait.damage.wood", 4, true);
		createTraitConfig("STDAxeDamageTrait", "trait.damage.stone", 5, true);
		createTraitConfig("STDAxeDamageTrait", "trait.damage.gold", 4, true);
		createTraitConfig("STDAxeDamageTrait", "trait.damage.iron", 6, true);
		createTraitConfig("STDAxeDamageTrait", "trait.damage.diamond", 7, true);
		
		createTraitConfig("HealOthersTrait", "trait.uplink", 60, true);
		createTraitConfig("HealOthersTrait", "trait.iteminhand", 287, true);
		
		createTraitConfig("BerserkerRageTrait", "trait.uplink", 60, true);
		createTraitConfig("BerserkerRageTrait", "trait.duration", 10, true);
		createTraitConfig("BerserkerRageTrait", "trait.activationLimit", 30, true);
		
		createTraitConfig("DwarfSkinTrait", "trait.uplink", 60, true);
		createTraitConfig("DwarfSkinTrait", "trait.duration", 10, true);
		createTraitConfig("DwarfSkinTrait", "trait.activationLimit", 30, true);
		
		createTraitConfig("LastStandTrait", "trait.uplink", 60, true);
		createTraitConfig("LastStandTrait", "trait.activationLimit", 30, true);
		
		String dropContainerPath = Consts.traitConfigDir + "DropRates.yml";
		CreateDropContainer.createAllContainers(dropContainerPath);
	}
	
	private static void createTraitConfig(String traitName, String config, Object value, boolean force){
		YAMLConfigExtended yamlConfig = createYAMLIfNotExist(traitName, force);
		if(yamlConfig == null)
			return;
		
		if(!yamlConfig.isInt(config))
			yamlConfig.set(config, value);
		
		yamlConfig.save();
	}
	
	private static YAMLConfigExtended createYAMLIfNotExist(String traitName, boolean force){
		File file = new File(Consts.traitConfigDir, traitName + ".yml");
		if(file.exists()){
			if(!force)
				return null;
		}else
			try {
				file.createNewFile();
			} catch (IOException e) {
				RacesAndClasses.getPlugin().log("Could not create file: " + file.toString());
				return null;
			}
		
		YAMLConfigExtended config = new YAMLConfigExtended(file.toString()).load();
		if(config.getValidLoad())
			return config;
		
		return null;
	}
}
