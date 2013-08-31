package de.tobiyas.racesandclasses.util.persistence;

import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLPersistenceProviderSetter extends YAMLPersistenceProvider {

	
	public static void setPlayerYAML(YAMLConfigExtended config){
		YAMLPersistenceProvider.playerYaml = config;
	}

	
	public static void setRacesYAML(YAMLConfigExtended config){
		YAMLPersistenceProvider.racesYaml = config;
	}
	
	
	public static void setClassesYAML(YAMLConfigExtended config){
		YAMLPersistenceProvider.classesYaml = config;
	}
}
