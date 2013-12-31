package de.tobiyas.racesandclasses.persistence.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLPersistenceProvider {
	
	static{
		rescanKnownPlayers();
	}
	
	/**
	 * The Cached Player YAML files
	 */
	protected static Map<String, YAMLConfigExtended> playerYamls = new HashMap<String, YAMLConfigExtended>();
	
	
	/**
	 * The Cached Races YAML file
	 */
	protected static YAMLConfigExtended racesYaml;
	
	
	/**
	 * The Cached Classes YAML file
	 */
	protected static YAMLConfigExtended classesYaml;
	
	/**
	 * The YAMLs for the Channels
	 */
	protected static YAMLConfigExtended channelsYaml;
	
	/**
	 * The knownPlayers.
	 */
	protected static Set<String> knownPlayers = new HashSet<String>();
	
	

	/**
	 * Returns the already loaded Player YAML File.
	 * This is a lazy load.
	 * 
	 * @param playerName to load
	 * 
	 * @return the loaded file. NEVER!!! SAVE IT!!! This will be done Async to NOT stop the Bukkit thread!
	 */
	public static YAMLConfigExtended getLoadedPlayerFile(String playerName) {
		if(playerYamls.containsKey(playerName)){
			cacheHit++;
			return playerYamls.get(playerName);
		}
		
		if(knownPlayers.contains(playerName)){
			YAMLConfigExtended playerConfig = new YAMLConfigExtended(new File(Consts.playerDataPath + playerName + ".yml")).load();
			playerYamls.put(playerName, playerConfig);
			
			cacheMiss++;
			return playerConfig;
		}
		
		//Whether in cache, nor known.
		YAMLConfigExtended playerConfig = new YAMLConfigExtended(new File(Consts.playerDataPath + playerName + ".yml")).load();
		playerYamls.put(playerName, playerConfig);
		
		rescanKnownPlayers();
		cacheMiss++;
		return playerConfig;
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
	 * Returns the already loaded loaded Races YAML File.
	 * This is a lazy load.
	 * 
	 * @return
	 */
	public static YAMLConfigExtended getLoadedChannelsFile(boolean loaded){
		if(channelsYaml == null){
			channelsYaml = new YAMLConfigExtended(new File(Consts.channelsYML)).load();
		}
		
		return loaded ? channelsYaml.load() : channelsYaml;
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
	
	
	/**
	 * Returns a List of all Players known.
	 * 
	 * @return the known Players.
	 */
	public static Set<String> getAllPlayersKnown(){
		if(knownPlayers == null){
			rescanKnownPlayers();
		}
		
		return knownPlayers;
	}
	
	/**
	 * Rescans the Known Players.
	 */
	protected static void rescanKnownPlayers(){
		File playerFolder = new File(Consts.playerDataPath);
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(name.startsWith("playerData")){
					return false;
				}
				
				return name.endsWith(".yml");
			}
		};
		
		if(knownPlayers == null){
			knownPlayers = new HashSet<String>();
		}
		
		knownPlayers.clear();
		for(String playerFile : playerFolder.list(filter)){
			knownPlayers.add(playerFile.replace(".yml", ""));
		}
	}
	
	
	
	private static int cacheHit = 0;
	private static int cacheMiss = 0;
	
	/**
	 * Returns the Hitrate of the YML loading
	 * 
	 * @return hitrate.
	 */
	public static double getCacheHitRate(){
		double total = cacheHit + cacheMiss;
		return total / cacheHit;
	}
	
	
	/**
	 * Returns the total number of accesses.
	 * 
	 * @return total number of accesses.
	 */
	public static int getTotalTries(){
		return cacheHit + cacheMiss;
	}
}
