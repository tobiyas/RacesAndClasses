package de.tobiyas.racesandclasses.persistence.converter;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class ConverterChecker {

	
	public static void checkAllConvertionsNeeded(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		
		
		//first try splitting bit YML data:
		PlayerDataToSmallFileConverter.convertPlayerDataToSmallFiles();
		
		//If we use a DB, convert the YML stuff to DB if not happened.
		if(useDB){
			DBConverter.convertYMLToDB();
		}
		
		//If we use a YML File, we need to convert everything to YML first.
		if(!useDB){
			ToYMLConverter.convertDBToYML();
		}
	}
}
