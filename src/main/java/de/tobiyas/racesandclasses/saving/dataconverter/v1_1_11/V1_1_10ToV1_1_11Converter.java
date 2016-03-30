package de.tobiyas.racesandclasses.saving.dataconverter.v1_1_11;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.saving.dataconverter.Converter;
import de.tobiyas.racesandclasses.saving.serializer.PlayerDataSerializer;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.file.FileUtils;

public class V1_1_10ToV1_1_11Converter implements Converter {

	
	private static final String OLD_NAME = "PlayerData";
	private static final String NEW_NAME = "PlayerDataYML";
	
	private static final long NOTIFY_TIME = 5*1000;
	
	
	@Override
	public void convert(){
		File toConvert = new File(RacesAndClasses.getPlugin().getDataFolder(), OLD_NAME);
		File newDir = new File(RacesAndClasses.getPlugin().getDataFolder(), NEW_NAME);
		Set<File> oldFiles = FileUtils.getAllFiles(toConvert);
		int size = oldFiles.size();
		if(size <= 0) return;
		
		int index = 0;
		long lastNotify = System.currentTimeMillis();
		System.out.println("Convertig Player-Data to V1_1_11: " + size + " Entries");
		for(File file : oldFiles){
			index++;
			if(!file.getName().endsWith(".yml")){
				file.delete();
				continue;
			}
			
			YAMLConfigExtended oldConfig = new YAMLConfigExtended(file).load();
			if(!oldConfig.getValidLoad()) continue;
			
			YAMLConfigExtended newConfig = new YAMLConfigExtended(new File(newDir, file.getName()));
			//Now do the Convertion:
			newConfig.set(PlayerDataSerializer.UUID_PATH, oldConfig.getString("uuid", ""));
			newConfig.set(PlayerDataSerializer.LAST_PLAYED_PATH, oldConfig.getLong("lastOnline", 0));
			newConfig.set(PlayerDataSerializer.LAST_NAME_PATH, oldConfig.getString("lastKnownName", ""));
			
			newConfig.set(PlayerDataSerializer.RACE_PATH, oldConfig.get("race", ""));
			newConfig.set(PlayerDataSerializer.CLASS_PATH, oldConfig.get("class", ""));
			
			newConfig.set(PlayerDataSerializer.LEVEL_PATH, oldConfig.getInt("level.currentLevel", 1));
			newConfig.set(PlayerDataSerializer.EXP_PATH, oldConfig.getInt("level.currentLevelEXP", 0));
			
			//Bindings:
			List<String> oldBindings = oldConfig.getStringList("bindings");
			for(String line : oldBindings){
				String[] lineSplit = line.split(Pattern.quote("|"));
				if(lineSplit.length != 2) continue;
				newConfig.set(PlayerDataSerializer.HOTKEY_PATH + "." + lineSplit[0], lineSplit[1]);
			}
			
			//SkillTree:
			List<String> skillTree = oldConfig.getStringList("learnedTraits");
			for(String line : skillTree){
				String[] lineSplit = line.split(Pattern.quote("@"));
				if(lineSplit.length != 2) continue;
				newConfig.set(PlayerDataSerializer.SKILL_TREE_PATH + "." + lineSplit[0], lineSplit[1]);
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
		File folder = new File(plugin.getDataFolder(), OLD_NAME);
		if(!folder.exists()) return false;
		
		File[] files = folder.listFiles();
		if(files == null || files.length == 0) return false;
		return true;
	}

	
	@Override
	public int getVersion() {
		return 0x01010a;
	}
	
}
