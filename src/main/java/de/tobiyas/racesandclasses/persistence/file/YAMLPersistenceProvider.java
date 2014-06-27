/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.persistence.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLPersistenceProvider {
	
	/**
	 * The Cached Player YAML files
	 */
	protected static Map<UUID, YAMLConfigExtended> playerYamls = new HashMap<UUID, YAMLConfigExtended>();
	
	
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
	 * The knownPlayerIDs.
	 */
	protected static Set<RaCPlayer> knownPlayerIDs;
	
	

	/**
	 * Returns the already loaded Player YAML File.
	 * This is a lazy load.
	 * 
	 * @param player to load
	 * 
	 * @return the loaded file. NEVER!!! SAVE IT!!! This will be done Async to NOT stop the Bukkit thread!
	 */
	public static YAMLConfigExtended getLoadedPlayerFile(Player player) {
		return getLoadedPlayerFile(player.getUniqueId());
	}
	
	
	
	/**
	 * Returns the already loaded Player YAML File.
	 * This is a lazy load.
	 * 
	 * @param player to load
	 * 
	 * @return the loaded file. NEVER!!! SAVE IT!!! This will be done Async to NOT stop the Bukkit thread!
	 */
	public static YAMLConfigExtended getLoadedPlayerFile(RaCPlayer player) {
		return getLoadedPlayerFile(player.getUniqueId());
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
	public static Set<RaCPlayer> getAllPlayersKnown(){
		if(knownPlayerIDs == null){
			rescanKnownPlayers();
		}
		
		return new HashSet<RaCPlayer>(knownPlayerIDs);
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
		
		if(knownPlayerIDs == null){
			knownPlayerIDs = new HashSet<RaCPlayer>();
		}
		
		knownPlayerIDs.clear();
		String[] playerFileNames = playerFolder.list(filter);
		if(playerFileNames == null) return;
		
		for(String playerFile : playerFileNames){
			try{
				UUID id = UUID.fromString(playerFile.replace(".yml", ""));
				RaCPlayer player = RaCPlayerManager.get().getPlayer(id);
				
				if(player != null) knownPlayerIDs.add(player);
			}catch(IllegalArgumentException exp){ continue; }
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


	/**
	 * Creates an offline player and returns the File.
	 * 
	 * @param player to load from.
	 * 
	 * @return the loaded file.
	 */
	public static YAMLConfigExtended getLoadedPlayerFile(UUID playerUUID) {
		if(knownPlayerIDs == null){
			rescanKnownPlayers();
		}
		
		if(playerYamls.containsKey(playerUUID)){
			YAMLConfigExtended playerConfig = playerYamls.get(playerUUID);
			if(playerConfig.getValidLoad()){
				cacheHit++;
				return playerConfig;
			}
			
			cacheMiss++;
			return playerConfig.load();
		}
		
		if(knownPlayerIDs.contains(playerUUID)){
			YAMLConfigExtended playerConfig = new YAMLConfigExtended(new File(Consts.playerDataPath + playerUUID.toString() + ".yml")).load();
			playerYamls.put(playerUUID, playerConfig);
			
			cacheMiss++;
			return playerConfig;
		}
		
		//Whether in cache, nor known.
		YAMLConfigExtended playerConfig = new YAMLConfigExtended(new File(Consts.playerDataPath + playerUUID.toString() + ".yml")).load();
		playerYamls.put(playerUUID, playerConfig);
		
		rescanKnownPlayers();
		cacheMiss++;
		return playerConfig;
	}
	
	
	/**
	 * Gets all unloaded Players.
	 * @return
	 */
	public static Set<RaCPlayer> getNotLoadedPlayers(){
		if(knownPlayerIDs == null) rescanKnownPlayers();
		
		Set<RaCPlayer> notLoaded = new HashSet<RaCPlayer>();
		for(RaCPlayer known : knownPlayerIDs){
			if(!playerYamls.containsKey(known.getUniqueId())) notLoaded.add(known);
		}
		
		return notLoaded;
	}
}
