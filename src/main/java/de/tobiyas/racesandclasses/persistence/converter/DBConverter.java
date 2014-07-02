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
package de.tobiyas.racesandclasses.persistence.converter;

import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfig;
import de.tobiyas.racesandclasses.configuration.member.database.DBMemberConfig;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.CustomPlayerLevelManager;
import de.tobiyas.util.config.YAMLConfigExtended;

public class DBConverter {

	/**
	 * The Plugin to call ConfigTotal stuff on.
	 */
	private static final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Converts everything in the PlayerData YML file.
	 */
	public static void convertYMLToDB(){		
		deleteOldPlayerData(false);
		convertHolderAssociated();
		convertGeneralData();
		convertMemberConfig();
		deleteOldPlayerData(true);
	}
	
	
	/**
	 * Removes all Player Entries that are empty.
	 */
	private static void deleteOldPlayerData(boolean broadcastSomething) {
		Set<RaCPlayer> playerList = YAMLPersistenceProvider.getAllPlayersKnown();
		
		if(playerList.size() <= 0){
			return;
		}
		
		int removed = 0;
		if(broadcastSomething){
			plugin.log("Performing some cleanup...");
		}
			
		for(RaCPlayer player : playerList){
			YAMLConfigExtended playerData = YAMLPersistenceProvider.getLoadedPlayerFile(player);
			if(playerData.getChildren("playerdata." + player).size() == 0){
				playerData.set("playerdata." + player, null);
				removed ++;
			}
		}
		
		if(broadcastSomething){
			plugin.log("Cleanup done. Removed " + removed + " old entries.");
		}
	}


	/**
	 * Converts the Player to classes / races Association to the DB.
	 */
	public static void convertHolderAssociated(){
		Set<RaCPlayer> playerList = YAMLPersistenceProvider.getAllPlayersKnown();
		
		if(playerList.size() <= 0){
			return;
		}
		
		plugin.log("Starting to Transfer Player Holder Data to DB: " + playerList.size() + " entries.");
		int i = 0;
		int times = 1;
		
		int fourthPercentValue = playerList.size() / 4;
		
		String defaultRaceName = getGeneralConfig().getConfig_defaultRaceName();
		String defaultClassName = null;
		
		for(RaCPlayer player : playerList){
			YAMLConfigExtended playerData = YAMLPersistenceProvider.getLoadedPlayerFile(player);
			if(!playerData.contains("race") && !playerData.contains("class")) continue;
			
			String raceName = playerData.getString("race", defaultRaceName);
			String className = playerData.getString("class", defaultClassName);
			
			PlayerHolderAssociation container = new PlayerHolderAssociation();
			container.setClassName(className);
			container.setRaceName(raceName);
			container.setPlayerUUID(player.getUniqueId());
			
			try{
				//look if already present.
				PlayerHolderAssociation presentHolder = plugin.getDatabase().find(PlayerHolderAssociation.class)
						.where().ieq("player", player.toString()).findUnique();
				
				if(presentHolder == null){
					plugin.getDatabase().save(container);
				}

				playerData.set("race", null);
				playerData.set("class", null);
				
				playerData.save();
			}catch(Exception exp){
				plugin.getDebugLogger().logStackTrace(exp);
			}
			
			i++;			
			if(fourthPercentValue == i){
				plugin.log("Still copying... Done: " + (times * 25) + "% that's: " + (times * fourthPercentValue) + " / " + playerList.size());
				times ++;
				i = 0;
			}
			
		}
		
		plugin.log("Copying done. Have fun with the DB. :)");
	}
	
	/**
	 * Converts the General Data of Players.
	 */
	public static void convertGeneralData(){
		Set<RaCPlayer> playerList = YAMLPersistenceProvider.getAllPlayersKnown();
		
		if(playerList.size() <= 0){
			return;
		}
		
		plugin.log("Starting to Transfer Player General Data to DB: " + playerList.size() + " entries.");
		int i = 0;
		int times = 1;
		
		int fourthPercentValue = playerList.size() / 4;
		
		for(RaCPlayer player : playerList){
			YAMLConfigExtended playerData = YAMLPersistenceProvider.getLoadedPlayerFile(player);
			String pre = "playerdata." + player;
			
			if(!playerData.contains(pre + ".hasGod") 
					&& !playerData.contains(pre + CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_EXP_PATH)
					&& !playerData.contains(pre + CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_PATH)) continue;
			
			boolean hasGod = playerData.getBoolean(pre + ".hasGod", false);			
			int level = playerData.getInt(pre + CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_PATH, 1);
			int levelExp = playerData.getInt(pre + CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_EXP_PATH, 0);
			
			PlayerSavingContainer container = PlayerSavingContainer.generateNewContainer(player);
			container.setHasGod(hasGod);
			container.setPlayerLevel(level);
			container.setPlayerLevelExp(levelExp);
			
			try{
				PlayerSavingContainer alreadyPlayerContainer = plugin.getDatabase().find(PlayerSavingContainer.class)
						.where().ieq("player", player.toString()).findUnique();
				
				if(alreadyPlayerContainer == null){
					plugin.getDatabase().save(container);
				}
				
				playerData.set(pre + ".hasGod", null);
				playerData.set(pre + ".currentHealth", null);
			}catch(Exception exp){
				plugin.getDebugLogger().logStackTrace(exp);
			}
			
			i++;			
			if(fourthPercentValue == i){
				plugin.log("Still copying... Done: " + (times * 25) + "% that's: " + (times * fourthPercentValue) + " / " + playerList.size());
				times ++;
				i = 0;
			}
		}
		
		plugin.log("Copying done. Have fun with the DB. :)");
	}
	
	
	/**
	 * tries to convert the MemberConfig of the Plugin.
	 */
	public static void convertMemberConfig(){
		Set<RaCPlayer> playerList = YAMLPersistenceProvider.getAllPlayersKnown();
		if(playerList.size() <= 0){
			return;
		}
		
		plugin.log("Starting to Transfer PlayerData to DB. This may take some time. Entries: " + playerList.size());
		int i = 0;
		int times = 1;
		
		int fourthPercentValue = playerList.size() / 4;
		
		for(RaCPlayer player : playerList){
			YAMLConfigExtended playerData = YAMLPersistenceProvider.getLoadedPlayerFile(player);
			if(playerData.getChildren("playerdata." + player + ".config").size() <= 0) continue;
			
			MemberConfig config = MemberConfig.createMemberConfig(player);
			
			DBMemberConfig dbConfig = DBMemberConfig.copyFrom(config);
			dbConfig.save();
			
			i++;
			
			if(fourthPercentValue == i){
				plugin.log("Still copying... Done: " + (times * 25) + "% that's: " + (times * fourthPercentValue) + " / " + playerList.size());
				times ++;
				i = 0;
			}
			
			playerData.set("playerdata." + player + ".config", null);
		}
		
		
		
		plugin.log("Copying done. Have fun with the Database. :)");
	}
	
	
	/**
	 * Returns the General ConfigTotal.
	 * If not load yet, it builds it temporarly.
	 * 
	 * @return
	 */
	public static GeneralConfig getGeneralConfig(){
		if(plugin.getConfigManager() == null){
			return new GeneralConfig().reload();
		}else{
			return plugin.getConfigManager().getGeneralConfig();
		}
	}
}