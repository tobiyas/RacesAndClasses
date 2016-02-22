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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class ClassAPI {

	/**
	 * The plugin to get the ClassManager from
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Returns the Class of a player.
	 * If the player has no Class, the Default Class is returned.
	 * 
	 * @param player to search
	 * 
	 * @return the {@link RaceContainer} of the player
	 * 
	 * @deprecated use {@link #getClassOfPlayer(OfflinePlayer)} instead.
	 */
	@Deprecated
	public static ClassContainer getClassOfPlayer(String playerName){
		if(!isClassSystemActive()) return null;
		return getClassOfPlayer(Bukkit.getPlayer(playerName));
	}
	
	/**
	 * Returns the Class of a player.
	 * If the player has no Class, the Default Class is returned.
	 * 
	 * @param player to search
	 * 
	 * @return the {@link RaceContainer} of the player
	 */
	public static ClassContainer getClassOfPlayer(Player player){
		if(!isClassSystemActive()) return null;
		
		RaCPlayer raCPlayer = RaCPlayerManager.get().getPlayer(player);
		ClassManager classManager = plugin.getClassManager();
		ClassContainer clazz = raCPlayer.getclass();
		if(clazz != null){
			return clazz;
		}else{
			return (ClassContainer) classManager.getDefaultHolder();
		}
	}
	
	/**
	* Returns the Class Name of a player.
	* If the player has no Class, the Default Class is returned.
	* 
	* @param player to search
	* 
	* @return the {@link RaceContainer} of the player
	*/
	public static String getClassNameOfPlayer(Player player){
		if(!isClassSystemActive()) return null;
		
		ClassContainer container = getClassOfPlayer(player);
		return (container == null) ? "" : container.getDisplayName();
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
		if(!isClassSystemActive()) return null;
		
		ClassManager classManager = plugin.getClassManager();
		return (ClassContainer) classManager.getHolderByName(className);
	}

	
	/**
	 * Returns a List of all Class names available
	 * 
	 * @return list of Class names
	 */
	public static List<String> getAllClassNames(){
		if(!isClassSystemActive()) return new ArrayList<>();
		return plugin.getClassManager().getAllHolderNames();
	}
	
	
	/**
	 * Gives the passed Player a Class.
	 * If he already has one, the Class is changed to the new one.
	 * 
	 * Returns true on success, false if:
	 *  - player can not be found on Bukkit.getPlayer(player).
	 *  - the new className is not found.
	 *  - any internal error occurs.
	 * 
	 * @param player the player that the Class should be changed.
	 * @param className to change to
	 * 
	 * @return true if worked, false otherwise
	 * 
	 * @deprecated use {@link #addPlayerToClass(OfflinePlayer, String)} instead.
	 */
	@Deprecated
	public static boolean addPlayerToClass(String playerName, String className){
		if(!isClassSystemActive()) return false;
		return addPlayerToClass(Bukkit.getPlayer(playerName), className);
	}
	
	/**
	 * Gives the passed Player a Class.
	 * If he already has one, the Class is changed to the new one.
	 * 
	 * Returns true on success, false if:
	 *  - player can not be found on Bukkit.getPlayer(player).
	 *  - the new className is not found.
	 *  - any internal error occurs.
	 * 
	 * @param Offlineplayer the player that the Class should be changed.
	 * @param className to change to
	 * 
	 * @return true if worked, false otherwise
	 */
	public static boolean addPlayerToClass(Player player, String className){
		if(player == null || className == null) return false;
		if(!isClassSystemActive()) return false;
		
		ClassManager manager = plugin.getClassManager();
		ClassContainer wantedClass = (ClassContainer) manager.getHolderByName(className);
		if(wantedClass == null){
			return false;
		}
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		return manager.changePlayerHolder(racPlayer, className, true);
	}
	
	
	public static String getDefaultClassName(){
		if(!isClassSystemActive()) return null;
		AbstractTraitHolder holder = plugin.getClassManager().getDefaultHolder();
		if(holder == null) return null;
		return holder.getDisplayName();
	}
	
	/**
	 * Returns if the Class system is used or if it is deactivated.
	 * 
	 * @return true if used.
	 */
	public static boolean isClassSystemActive(){
		return plugin.getConfigManager().getGeneralConfig().isConfig_classes_enable();
	}
}
