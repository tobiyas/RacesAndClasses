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
package de.tobiyas.racesandclasses.configuration.racetoclass;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;

public class RaceToClassConfiguration {

	/**
	 * The ConfigTotal file where the Races / Classes selection matrix is found
	 */
	private YAMLConfigExtended racesClassesSelectionMatrix;
	
	
	/**
	 * The main Plugin Object 
	 */
	private RacesAndClasses plugin;
	
	
	/**
	 * The Path to the Races / Classes selection matrix
	 */
	private String pathToConfigFile;
	
	
	/**
	 * The Actual Matrix of race -> classes it can select
	 */
	private Map<String, List<String>> selectionMatrix;
	
	
	/**
	 * Constructs a new 
	 */
	public RaceToClassConfiguration() {
		plugin = RacesAndClasses.getPlugin();
		selectionMatrix = new HashMap<String, List<String>>();
		
		pathToConfigFile = plugin.getDataFolder().getAbsolutePath() + File.separator + "racesClassesSelectionMatrix.yml";
		racesClassesSelectionMatrix = new YAMLConfigExtended(pathToConfigFile);
	}
	
	
	/**
	 * Reloads the Configuration for the selection matrix
	 */
	public void reload(){
		racesClassesSelectionMatrix.load();
		if(!racesClassesSelectionMatrix.getValidLoad()){
			plugin.log("The File: " + pathToConfigFile + "' could not be loaded correct. The Selection Matrix is empty.");
			return;
		}
		
		
		Set<String> races = racesClassesSelectionMatrix.getRootChildren();
		for(String race: races){
			if(!racesClassesSelectionMatrix.isList(race)){
				plugin.log("The Race " + race + " in the file: '" + pathToConfigFile + "' has no List as argument. Skiping it.");
				continue;
			}
			
			List<String> classList = racesClassesSelectionMatrix.getStringList(race);
			selectionMatrix.put(race, classList);
		}
	}
	
	
	/**
	 * Searches for the raceName in the RaceSelection List.
	 * <br>Returns a list of Strings (class names) when the race is found.
	 * <br>Returns an empty List, when the Race has no Classes to select.
	 * <br>Throws an exception when the Race is not found.
	 * 
	 * @param raceName the race to search for
	 * 
	 * @throws RaceNotFoundException when the race is not found.
	 */
	public List<String> getClassesValidForRace(String raceName) throws RaceNotFoundException{
		if(!selectionMatrix.containsKey(raceName)){
			throw new RaceNotFoundException();
		}
		
		return selectionMatrix.get(raceName);
	}

}
