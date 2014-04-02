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
package de.tobiyas.racesandclasses.APIs;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;

public class ClassAPI {

	/**
	 * The plugin to get the ClassManager from
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Returns the Class of a player.
	 * If the player has no Class, the Default Class is returned.
	 * 
	 * @param playerUUID to search
	 * 
	 * @return the {@link RaceContainer} of the player
	 * 
	 * @deprecated use {@link #getClassOfPlayer(OfflinePlayer)} instead.
	 */
	@Deprecated
	public static ClassContainer getClassOfPlayer(String playerName){
		return getClassOfPlayer(Bukkit.getPlayer(playerName));
	}
	
	/**
	 * Returns the Class of a player.
	 * If the player has no Class, the Default Class is returned.
	 * 
	 * @param playerUUID to search
	 * 
	 * @return the {@link RaceContainer} of the player
	 */
	public static ClassContainer getClassOfPlayer(OfflinePlayer player){
		ClassManager classManager = plugin.getClassManager();
		ClassContainer clazz = (ClassContainer) classManager.getHolderOfPlayer(player.getUniqueId());
		if(clazz != null){
			return clazz;
		}else{
			return (ClassContainer) classManager.getDefaultHolder();
		}
	}
	
	
	/**
	 * Returns the {@link ClassContainer} by the name.
	 * If the Class is not found, Null is returned.
	 * 
	 * @param className to search
	 * 
	 * @return the Class corresponding to the name
	 */
	public static ClassContainer getClassByName(String className){
		ClassManager classManager = plugin.getClassManager();
		return (ClassContainer) classManager.getHolderByName(className);
	}

	
	/**
	 * Returns a List of all Class names available
	 * 
	 * @return list of Class names
	 */
	public static List<String> getAllClassNames(){
		return plugin.getClassManager().getAllHolderNames();
	}
	
	
	/**
	 * Gives the passed Player a Class.
	 * If he already has one, the Class is changed to the new one.
	 * 
	 * Returns true on success, false if:
	 *  - playerUUID can not be found on Bukkit.getPlayer(playerUUID).
	 *  - the new className is not found.
	 *  - any internal error occurs.
	 * 
	 * @param playerUUID the player that the Class should be changed.
	 * @param className to change to
	 * 
	 * @return true if worked, false otherwise
	 * 
	 * @deprecated use {@link #addPlayerToClass(OfflinePlayer, String)} instead.
	 */
	@Deprecated
	public static boolean addPlayerToClass(String playerName, String className){
		return addPlayerToClass(Bukkit.getPlayer(playerName), className);
	}
	
	/**
	 * Gives the passed Player a Class.
	 * If he already has one, the Class is changed to the new one.
	 * 
	 * Returns true on success, false if:
	 *  - playerUUID can not be found on Bukkit.getPlayer(playerUUID).
	 *  - the new className is not found.
	 *  - any internal error occurs.
	 * 
	 * @param playerUUID the player that the Class should be changed.
	 * @param className to change to
	 * 
	 * @return true if worked, false otherwise
	 */
	public static boolean addPlayerToClass(OfflinePlayer player, String className){
		if(player == null) return false;
		
		ClassManager manager = plugin.getClassManager();
		ClassContainer wantedClass = (ClassContainer) manager.getHolderByName(className);
		if(wantedClass == null){
			return false;
		}
		
		return manager.changePlayerHolder(player.getUniqueId(), className, true);
	}
}
