package de.tobiyas.racesandclasses.saving.dataconverter;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.config.YMLConfigFilter;
import de.tobiyas.util.file.FileUtils;

public class V1_1_10ToV1_1_11Converter implements Converter {

	
	private static final String OLD_NAME = "PlayerData";
	private static final String NEW_NAME = "PlayerDataYML";
	
	private static final long NOTIFY_TIME = 5*1000;
	
	
	@Override
	public void convert(){
		File toConvert = new File(RacesAndClasses.getPlugin().getDataFolder(), OLD_NAME);
		File newDir = new File(RacesAndClasses.getPlugin().getDataFolder(), NEW_NAME);
		Set<File> oldFiles = FileUtils.getAllFiles(toConvert, new YMLConfigFilter());
		int size = oldFiles.size();
		if(size <= 0) return;
		
		int index = 0;
		long lastNotify = System.currentTimeMillis();
		System.out.println("Convertig Player-Data to V1_1_11: " + size + " Entries");
		for(File file : oldFiles){
			index++;
			YAMLConfigExtended oldConfig = new YAMLConfigExtended(file).load();
			if(!oldConfig.getValidLoad()) continue;
			
			YAMLConfigExtended newConfig = new YAMLConfigExtended(new File(newDir, file.getName()));
			//Now do the Convertion:
			newConfig.set("id", oldConfig.get("uuid", ""));
			newConfig.set("lastplayed", oldConfig.getLong("lastOnline", 0));
			newConfig.set("lastname", oldConfig.getString("lastKnownName", ""));
			
			newConfig.set("race", oldConfig.get("race", ""));
			newConfig.set("class", oldConfig.get("class", ""));
			
			newConfig.set("level", oldConfig.getInt("level.currentLevel", 1));
			newConfig.set("exp", oldConfig.getInt("level.currentLevelEXP", 0));
			
			//Bindings:
			List<String> oldBindings = oldConfig.getStringList("bindings");
			for(String line : oldBindings){
				String[] lineSplit = line.split(Pattern.quote("|"));
				if(lineSplit.length != 2) continue;
				newConfig.set(lineSplit[0], lineSplit[1]);
			}
			
			//SkillTree:
			List<String> skillTree = oldConfig.getStringList("learnedTraits");
			for(String line : skillTree){
				String[] lineSplit = line.split(Pattern.quote("@"));
				if(lineSplit.length != 2) continue;
				newConfig.set(lineSplit[0], lineSplit[1]);
			}
			
			
			newConfig.save();
			file.delete();
			
			if(lastNotify + NOTIFY_TIME < System.currentTimeMillis()){
				double percent = ((double)index / (double) size) * 100d;
				System.out.println("Converted " + ((int)percent) + " for V1_1_11 data. " +index+"/"+size);
				lastNotify = System.currentTimeMillis();
			}
		}
		
		toConvert.delete();
	}

	
	@Override
	public boolean isApplyable() {
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		return new File(plugin.getDataFolder(), OLD_NAME).exists();
	}

	
	@Override
	public int getVersion() {
		return 0x01010a;
	}
	
}
