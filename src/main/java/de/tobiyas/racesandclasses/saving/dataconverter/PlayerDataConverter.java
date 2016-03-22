package de.tobiyas.racesandclasses.saving.dataconverter;

import de.tobiyas.racesandclasses.saving.dataconverter.v1_1_11.V1_1_10ToV1_1_11Converter;
import de.tobiyas.racesandclasses.saving.dataconverter.v1_1_11.YMLToDatabaseConverter;

public class PlayerDataConverter {

	/**
	 * The Converters:
	 */
	private static final Converter[] converters = new Converter[]{
			new V1_1_10ToV1_1_11Converter(), new YMLToDatabaseConverter(),
			};
	
	
	/**
	 * Starts convertion of Data if present.
	 */
	public static void checkForConvertAndConvert(){
		for(int i = 0; i < converters.length; i++){
			Converter converter = converters[i];
			if(converter == null) continue;
			
			//Start the convertion if possible:
			if(converter.isApplyable()) converter.convert();
		}
	}
	
}
