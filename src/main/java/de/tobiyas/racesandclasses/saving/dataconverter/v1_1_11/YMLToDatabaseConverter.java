package de.tobiyas.racesandclasses.saving.dataconverter.v1_1_11;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.racesandclasses.saving.dataconverter.Converter;
import de.tobiyas.racesandclasses.saving.serializer.DatabasePlayerDataSerializer;
import de.tobiyas.racesandclasses.saving.serializer.YAMLPlayerDataSerializer;
import de.tobiyas.util.file.FileUtils;

public class YMLToDatabaseConverter implements Converter {

	
	@Override
	public void convert() {
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		DatabasePlayerDataSerializer dbSerializer = new DatabasePlayerDataSerializer(plugin);
		YAMLPlayerDataSerializer ymlSerializer = new YAMLPlayerDataSerializer(plugin);
		
		File toConvertDir = new File(plugin.getDataFolder(), "PlayerDataYML");
		Collection<File> files = FileUtils.getAllFiles(toConvertDir);

		//Convert:
		System.out.println("Converting " + files.size() + " files to DB.");
		for(File file : files){
			if(!file.getName().endsWith(".yml")){
				file.delete();
				continue;
			}
			
			try{
				UUID id = UUID.fromString(file.getName().replace(".yml", ""));
				PlayerSavingData data = ymlSerializer.loadDataNow(id);
				dbSerializer.saveData(data, true);
				file.delete();
			}catch(Throwable exp){}
		}
		
		toConvertDir.delete();
	}
	

	@Override
	public boolean isApplyable() {
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB()) return false;
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_convert_toDB_orFile()) return false;
		
		File toConvertDir = new File(plugin.getDataFolder(), "PlayerDataYML");
		if(!toConvertDir.exists()) return false;
		
		DatabasePlayerDataSerializer dbSerializer = new DatabasePlayerDataSerializer(plugin);
		if(!dbSerializer.isFunctional()) return false;
		
		return true;
	}
	

	@Override
	public int getVersion() {
		return 0x1010a;
	}

}
