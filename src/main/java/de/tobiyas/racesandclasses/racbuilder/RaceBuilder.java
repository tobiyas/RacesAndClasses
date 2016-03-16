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
package de.tobiyas.racesandclasses.racbuilder;

import java.io.File;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.racbuilder.container.BuilderRaceContainer;
import de.tobiyas.util.config.YAMLConfigExtended;


public class RaceBuilder extends AbstractHolderBuilder{

	/**
	 * Generates a Builder for a Race
	 * 
	 * @param name to build
	 */
	public RaceBuilder(String name) {
		super(name);
		
		health = 20;
	}
	
	
	/**
	 * Generates a builder from a existing race
	 * 
	 * @param raceContainer
	 */
	public RaceBuilder(RaceContainer raceContainer){
		super(raceContainer);
		
		//health = raceContainer.getRaceMaxHealth();
	}
	
	
	/**
	 * Builds the {@link RaceContainer} from the given values.
	 * 
	 * @return the build RaceContainer.
	 */
	public RaceContainer build(){
		return new BuilderRaceContainer(this.name, this.holderTag, this.armorPermission, this.traitSet, this.permissionList, this.health);
	}
	
	@Override
	protected YAMLConfigExtended getHolderYAMLFile() {
		return new YAMLConfigExtended(new File(new File(plugin.getDataFolder(),"races"), name + ".yml"));
	}


	@Override
	protected void saveFurtherToFile(YAMLConfigExtended config) {
		config.set(name + ".config.raceMaxHealth", health);
		config.set(name + ".config.raceTag", holderTag);
	}
}
