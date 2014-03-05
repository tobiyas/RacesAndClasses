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

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class PlayerDataToSmallFileConverter {

	
	/**
	 * Converts Big PlayerYML Data if present.
	 */
	public static void convertPlayerDataToSmallFiles(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		File file = new File(Consts.playerDataPath, "playerdata.yml");
		if(!file.exists()) return; //no file -> no transformation
		
		YAMLConfigExtended bigDataYML = new YAMLConfigExtended(file).load();
		if(!bigDataYML.getValidLoad()) return; //no valid data
		
		Set<String> playerNames = bigDataYML.getChildren("playerdata");
		if(playerNames == null || playerNames.isEmpty()) return;
		
		int totalSize = playerNames.size();
		int notifyAt = totalSize / 4;
		if(notifyAt == 0) notifyAt = 1;
		
		int i = 0;
		Iterator<String> nameIt = playerNames.iterator();
		
		while(nameIt.hasNext()){
			String playerName = nameIt.next();
			
			YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
			Object subTree = bigDataYML.get("playerdata." + playerName);
			config.set("playerdata." + playerName, subTree);
			i++;
			
			if((i % notifyAt) == 0){
				plugin.log(ChatColor.GREEN + "Converting Big PlayerYML to small ones: " 
						+ ChatColor.LIGHT_PURPLE + i + ChatColor.GRAY + "/" 
						+ ChatColor.LIGHT_PURPLE + totalSize + ChatColor.GREEN + ".");
			}
		}
		
		try{
			file.delete();
		}catch(Exception exp){
			plugin.log(ChatColor.RED + "Could not delete 'playerData.yml'. Please delete it per Hand!");
		}
	}
}
