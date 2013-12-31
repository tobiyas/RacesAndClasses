package de.tobiyas.racesandclasses.util.persistence;

import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLPersistenceProviderSetter extends YAMLPersistenceProvider {

	
	public static void setPlayerYAML(String playerName, YAMLConfigExtended config){
		playerYamls.put(playerName, config);
	}

	public static void setPlayerYAMLPath(String path){
		Consts.playerDataPath = path;
	}

	
	public static void setRacesYAML(YAMLConfigExtended config){
		YAMLPersistenceProvider.racesYaml = config;
	}
	
	
	public static void setClassesYAML(YAMLConfigExtended config){
		YAMLPersistenceProvider.classesYaml = config;
	}
}
