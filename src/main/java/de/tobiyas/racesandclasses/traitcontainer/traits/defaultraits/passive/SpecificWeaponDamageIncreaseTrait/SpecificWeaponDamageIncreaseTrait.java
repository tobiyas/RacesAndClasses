/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.SpecificWeaponDamageIncreaseTrait;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractWeaponDamageIncreaseTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class SpecificWeaponDamageIncreaseTrait extends AbstractWeaponDamageIncreaseTrait {
	
	

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "weapons", classToExpect = String.class, optional = false)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("weapons")){
			String weaponsString = configMap.getAsString("weapons");
			for(String weapon : weaponsString.split(Pattern.quote(","))){
				try{
					Material ma = Material.matchMaterial(weapon);
					if(ma != null) this.weapons.add(ma);
				}catch(Throwable exp){}
			}
		}
		
	}


	@Override
	public String getName() {
		return "SpecificWeaponDamageIncreaseTrait";
	}
	
		
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Your Damage will be increased by a value or times an value.");
		return helpList;
	}
	
	
	@TraitInfos(category="passive", traitName="SpecificWeaponDamageIncreaseTrait", visible=true)
	@Override
	public void importTrait() {}

}
