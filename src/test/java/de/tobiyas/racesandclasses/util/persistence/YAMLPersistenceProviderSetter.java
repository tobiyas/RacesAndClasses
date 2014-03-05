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
package de.tobiyas.racesandclasses.util.persistence;

import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLPersistenceProviderSetter extends YAMLPersistenceProvider {

	
	public static void setPlayerYAML(String playerName, YAMLConfigExtended config){
		playerYamls.put(playerName, config);
	}

	public static void setPlayerYAMLPath(String path){
		Consts.playerDataPath = path;
	}

	
	public static void setRacesYAML(YAMLConfigExtended config){
		YAMLPersistenceProvider.racesYaml = config;
	}
	
	
	public static void setClassesYAML(YAMLConfigExtended config){
		YAMLPersistenceProvider.classesYaml = config;
	}
}
