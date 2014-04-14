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
package de.tobiyas.racesandclasses.configuration.member;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.database.DBMemberConfig;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;


public class MemberConfigManager {

	/**
	 * The config of members: player -> ConfigTotal 
	 */
	private Map<UUID, MemberConfig> memberConfigs;
	
	
	/**
	 * The plugin to call stuff on
	 */
	private RacesAndClasses plugin;
	
	
	private int bukkitTaskID = -1;
	
	/**
	 * Creates the member config
	 */
	public MemberConfigManager(){
		plugin = RacesAndClasses.getPlugin();
		memberConfigs = new HashMap<UUID, MemberConfig>();
	}
	
	
	public void shutDown(){
		if(bukkitTaskID > 0){
			Bukkit.getScheduler().cancelTask(bukkitTaskID);
			bukkitTaskID = -1;
		}
	}
	
	/**
	 * creates member config file + reload the config
	 */
	@SuppressWarnings("deprecation") //This is async scheduling for performance.
	public void reload(){
		memberConfigs = new HashMap<UUID, MemberConfig>();
		shutDown();
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB()){
			loadConfigs();
		}
		
		int savingTime = 20 * 60 * 10;
		bukkitTaskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				try{
					saveConfigs();
				}catch(Throwable exp){} //Ignore all saving errors.
			}
		}, savingTime, savingTime);
	}
	
	
	/**
	 * Creates a new MemberConfig for a player and saves it.
	 * If the ConfigTotal of the player is already present, it will be returned instead.
	 * 
	 * @param player
	 * @return
	 */
	private MemberConfig getCreateNewConfig(UUID player){
		if(memberConfigs.containsKey(player)){
			return memberConfigs.get(player);
		}
		
		MemberConfig config = null;
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		if(useDB){
			config = DBMemberConfig.createMemberConfig(player);
		}else{
			config = MemberConfig.createMemberConfig(player).save();
		}
		
		memberConfigs.put(player, config);
		return config;
	}
	
	/**
	 * Loads all configs from the playerdata file
	 */
	private void loadConfigs(){
		Set<UUID> players = YAMLPersistenceProvider.getAllPlayersKnown();
		
		for(UUID playerUUID : players){
			Player player = Bukkit.getPlayer(playerUUID);
			if(player != null && player.isOnline())	getCreateNewConfig(playerUUID);
		}
	}
	
	/**
	 * Forces save to all configs
	 */
	public void saveConfigs(){
		for(UUID player : memberConfigs.keySet()){
			MemberConfig config = memberConfigs.get(player);
			if(config != null) {
				if(config instanceof DBMemberConfig){
					((DBMemberConfig) config).save();
				}else{
					config.save();
				}
			}
		}
	}
	
	/**
	 * Returns the personal MemberConfig of a player
	 * If not existent a new one is created.
	 * 
	 * @param player to get the config from
	 * @return
	 */
	public MemberConfig getConfigOfPlayer(UUID player){
		return getCreateNewConfig(player);
	}
	
}
