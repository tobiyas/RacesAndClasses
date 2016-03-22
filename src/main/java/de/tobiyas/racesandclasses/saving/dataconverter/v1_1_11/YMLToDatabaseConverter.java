package de.tobiyas.racesandclasses.saving.dataconverter.v1_1_11;

import java.io.File;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.saving.dataconverter.Converter;

public class YMLToDatabaseConverter implements Converter {

	@Override
	public void convert() {
		//TODO implement converter!
	}
	

	@Override
	public boolean isApplyable() {
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		if(!plugin.getDescription().getVersion().equals("1.1.11")) return false;
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB()) return false;
		
		File toConvertDir = new File(plugin.getDataFolder(), "PlayerData");
		if(!toConvertDir.exists()) return false;
		
		return true;
	}
	

	@Override
	public int getVersion() {
		return 0x1010a;
	}

}
