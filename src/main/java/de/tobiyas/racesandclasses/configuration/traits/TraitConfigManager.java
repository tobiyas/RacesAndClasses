package de.tobiyas.racesandclasses.configuration.traits;

import java.io.File;
import java.util.HashMap;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class TraitConfigManager {

	private HashMap<String, TraitConfig> configs;
	
	private static TraitConfigManager traitConfigManager;
	
	public TraitConfigManager(){
		traitConfigManager = this;
		RacesAndClasses.getPlugin();
		configs = new HashMap<String, TraitConfig>();
		checkDirs();
	}
	
	private void checkDirs(){
		File file = new File(Consts.traitConfigDir);
		if(!file.exists())
			file.mkdirs();
		DefaultTraitConfig.createDefaultTraitConfig();
	}
	
	public void init(){
		File configDir = new File(Consts.traitConfigDir);
		
		for(File file : configDir.listFiles()){
			if(file.isDirectory())
				continue;
			
			int index = file.getName().lastIndexOf('.');
			String fileName = file.getName().substring(0, index);
			configs.put(fileName, new TraitConfig(fileName));
		}
	}
	
	public TraitConfig getConfigOfTrait(String traitName){
		TraitConfig config = configs.get(traitName);
		return config;
	}
	
	public static TraitConfigManager getInstance(){
		return traitConfigManager;
	}
	
	
}
