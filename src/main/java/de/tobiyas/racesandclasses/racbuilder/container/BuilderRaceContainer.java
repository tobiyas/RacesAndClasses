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
package de.tobiyas.racesandclasses.racbuilder.container;

import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

public class BuilderRaceContainer extends RaceContainer {

	
	/**
	 * Creates a new RaceContainer for the passed values.
	 * 
	 * @param name the name to generate to.
	 */
	public BuilderRaceContainer(String name, String raceTag, Set<ItemQuality> armorPermissions, Set<Trait> traits, 
			List<String> permissionList, double raceMaxHealth) {
		super(name);
		
		this.holderTag = raceTag;
		//this.armorUsage = armorPermissions; //TODO fixme
		
		//this.raceMaxHealth = raceMaxHealth;
		this.traits = traits;
		
		this.raceChatColor = RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_color();
		this.raceChatFormat = RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_format();
	}
	
	
	@Override
	public AbstractTraitHolder load(){
		return this;
	}

}
