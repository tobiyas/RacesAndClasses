package de.tobiyas.racesandclasses.persistence.converter;

import java.io.File;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ConvertRaceClassFilesToSmaller {

	public static void convert(){
		convertRaces();
		convertClasses();
	}

	private static void convertClasses() {
		File classesFile = new File(Consts.classesYML);
		if(!classesFile.exists()) return; //nothing to convert.
		
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.classesYML).load();
		
		File classDir = new File(RacesAndClasses.getPlugin().getDataFolder(), "classes");
		if(!classDir.exists()) classDir.mkdirs();
		
		for(String root : config.getRootChildren()){
			YAMLConfigExtended newConfig = new YAMLConfigExtended(new File(classDir, root + ".yml"));
			newConfig.set(root, config.get(root));
			
			newConfig.save();
		}
		
		new File(Consts.classesYML).delete();
	}

	private static void convertRaces() {
		File raceFile = new File(Consts.racesYML);
		if(!raceFile.exists()) return; //nothing to convert.
		
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.racesYML).load();
		
		File racesDir = new File(RacesAndClasses.getPlugin().getDataFolder(), "races");
		if(!racesDir.exists()) racesDir.mkdirs();
		
		for(String root : config.getRootChildren()){
			YAMLConfigExtended newConfig = new YAMLConfigExtended(new File(racesDir, root + ".yml"));
			newConfig.set(root, config.get(root));
			
			newConfig.save();
		}
		
		new File(Consts.racesYML).delete();
	}
}
