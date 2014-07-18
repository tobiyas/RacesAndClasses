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
package de.tobiyas.racesandclasses.persistence.converter;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.statistics.StartupStatisticCategory;

public class ConverterChecker {

	
	/**
	 * This checks all the conversions that a RaC System currently needs.
	 */
	public static void checkAllConvertionsNeeded(){
		long timeBefore = System.currentTimeMillis();
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		//Convert Races / Classes file to smaller ones
		ConvertRaceClassFilesToSmaller.convert();
		
		//if we don't want conversions, we don't do it.
		boolean useConvertion = plugin.getConfigManager().getGeneralConfig().isConfig_convertDatabaseOnStartup();
		
		//DB is disabled.
		//boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		
		
		//first try splitting bit YML data:
		PlayerDataToSmallFileConverter.convertPlayerDataToSmallFiles();
		
		//second convert Name to UUID
		YML_OLD_to_NEW_converter.convert();
		
		//old the conversion below need something
		if(!useConvertion) return;
		
		
		//If we use a DB, convert the YML stuff to DB if not happened.
		/*if(useDB){
			DBConverter.convertYMLToDB();
		}
		
		//If we use a YML File, we need to convert everything to YML first.
		if(!useDB){
			ToYMLConverter.convertDBToYML();
		}*/
		
		long timeNeeded = System.currentTimeMillis() - timeBefore;
		StartupStatisticCategory.Conversion.timeInMiliSeconds = timeNeeded;
	}
}
