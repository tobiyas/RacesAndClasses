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
package de.tobiyas.racesandclasses.playermanagement.leveling;


public enum LevelingSystem {
	/**
	 * The Inbuild Level System
	 */
	RacesAndClasses,
	
	/**
	 * The MC level
	 */
	VanillaMC,
	
	/**
	 * The Level of McMMO.
	 */
	mcMMO,
	
	/**
	 * Supporting Heroes Levels.
	 */
	Heroes,
	
	/**
	 * SkillAPI is used.
	 */
	SkillAPI;

	
	/**
	 * Parses an String to the corresponding LevelingSystem.
	 * 
	 * @param toParse the String to parse.
	 * 
	 * @return the correct leveling system.
	 */
	public static LevelingSystem parse(String toParse) {
		if(toParse == null) return RacesAndClasses;
		
		toParse = toParse.toLowerCase();
		if(toParse.startsWith("r"))	return RacesAndClasses;
		if(toParse.startsWith("v") || toParse.equalsIgnoreCase("mc")) return VanillaMC;
		if(toParse.startsWith("mcm")) return mcMMO;
		if(toParse.startsWith("s"))	return SkillAPI;
		if(toParse.startsWith("h")) return Heroes;
		
		//at least try to parse from names.
		try{ return LevelingSystem.valueOf(toParse.toUpperCase()); }catch(Throwable exp){}
		return RacesAndClasses;
	}
}
