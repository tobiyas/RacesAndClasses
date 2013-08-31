package de.tobiyas.racesandclasses.util.persistence;

import java.io.File;

import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLPersistenceProvider {

	/**
	 * The Cached Player YAML file
	 */
	protected static YAMLConfigExtended playerYaml;
	
	
	/**
	 * The Cached Races YAML file
	 */
	protected static YAMLConfigExtended racesYaml;
	
	
	/**
	 * The Cached Classes YAML file
	 */
	protected static YAMLConfigExtended classesYaml;
	
	
	
	
	
	/**
	 * Returns the already loaded Player YAML File.
	 * This is a lazy load.
	 * 
	 * @return
	 */
	public static YAMLConfigExtended getLoadedPlayerFile(boolean loaded) {
		if(playerYaml == null){
			playerYaml = new YAMLConfigExtended(new File(Consts.playerDataYML)).load();
			return playerYaml;
		}
		
		
		return loaded ? playerYaml.load() : playerYaml;
	}

	
	/**
	 * Returns the already loaded loaded Races YAML File.
	 * This is a lazy load.
	 * 
	 * @return
	 */
	public static YAMLConfigExtended getLoadedRacesFile(boolean loaded){
		if(racesYaml == null){
			racesYaml = new YAMLConfigExtended(new File(Consts.racesYML)).load();
		}
		
		return loaded ? racesYaml.load() : racesYaml;
	}
	
	
	/**
	 * Returns the already loaded loaded Classes YAML File.
	 * This is a lazy load.
	 * 
	 * @return
	 */
	public static YAMLConfigExtended getLoadedClassesFile(boolean loaded){
		if(classesYaml == null){
			classesYaml = new YAMLConfigExtended(new File(Consts.classesYML)).load();
		}
		
		return loaded ? classesYaml.load() : classesYaml;
	}
	
}
