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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.persistence.PersistenceStorage;
import de.tobiyas.racesandclasses.persistence.db.EBeanPersistenceStorage;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceStorage;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ToYMLConverter {

	
	/**
	 * Converts the DB stuff to YML.
	 */
	public static void convertDBToYML(){
		EBeanPersistenceStorage ebeanStorage = new EBeanPersistenceStorage();
		
		convertHolderAssociations(ebeanStorage);
		convertGeneralData(ebeanStorage);
		convertMemberConfig(ebeanStorage);
	}

	/**
	 * Converts the Holder associations.
	 * @param ebeanStorage 
	 */
	private static void convertHolderAssociations(PersistenceStorage ebeanStorage) {
		YAMLConfigExtended raceFile = YAMLPersistenceProvider.getLoadedRacesFile(true);
		YAMLConfigExtended classFile = YAMLPersistenceProvider.getLoadedRacesFile(true);
		
		Set<String> holderSet = new HashSet<String>();
		holderSet.addAll(raceFile.getRootChildren());
		holderSet.addAll(classFile.getRootChildren());
		
		if(holderSet.isEmpty()) return; //early out if no races or classes (shouldn't happen)
		
		Set<PlayerHolderAssociation> holderAssociations = new HashSet<PlayerHolderAssociation>();
		
		for(String holder : holderSet){
			try{
				List<PlayerHolderAssociation> newHolders = ebeanStorage.getAllPlayerHolderAssociationsForHolder(holder);
				if(newHolders != null){
					holderAssociations.addAll(newHolders);
				}
			}catch(Exception exp){}
		}
		
		if(holderAssociations.isEmpty()) return; //early out if none found.
		
		for(PlayerHolderAssociation holder : holderAssociations){
			String playerName = holder.getPlayerName();
			String race = holder.getRaceName();
			String clazz = holder.getClassName();
			
			YAMLConfigExtended playerConfig = YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
			playerConfig.set("playerdata." + playerName + ".class", clazz);
			playerConfig.set("playerdata." + playerName + ".race", race);
		}
	}
	
	
	/**
	 * Converts the EBean storage to YML
	 * 
	 * @param storage to convert from
	 */
	private static void convertGeneralData(PersistenceStorage storage){
		Set<PlayerSavingContainer> playerGeneral = new HashSet<PlayerSavingContainer>();
		List<PlayerSavingContainer> newHolders = storage.getAllPlayerSavingContainers();
		if(newHolders != null){
			playerGeneral.addAll(newHolders);
		}
		
		if(playerGeneral.isEmpty()) return; //early out if none found.
		
		for(PlayerSavingContainer container : playerGeneral){
			String playerName = container.getPlayerName();
			boolean hasGod = container.isHasGod();
			int level = container.getPlayerLevel();
			int expOfLevel = container.getPlayerLevelExp();
			
			YAMLConfigExtended playerConfig = YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
			playerConfig.set("playerdata." + playerName + ".level.currentLevel", level);
			playerConfig.set("playerdata." + playerName + ".level.currentLevelEXP", expOfLevel);
			playerConfig.set("playerdata." + playerName + ".hasGod", hasGod);
		}
	}
	
	/**
	 * Converty the EBean Storage to YML.
	 * 
	 * @param storage to convert from
	 */
	private static void convertMemberConfig(PersistenceStorage storage){
		Set<ConfigOption> playerConfig = new HashSet<ConfigOption>();
		
		for(String playerName : YAMLPersistenceProvider.getAllPlayersKnown()){
			List<ConfigOption> newHolders = storage.getAllConfigOptionsOfPlayer(playerName);
			if(newHolders != null){
				playerConfig.addAll(newHolders);
			}
		}
		
		if(playerConfig.isEmpty()) return; //early out if none found.

		YAMLPersistenceStorage newStorage = new YAMLPersistenceStorage();
		for(ConfigOption container : playerConfig){
			newStorage.savePlayerMemberConfigEntry(container, true);
		}
	}

}
