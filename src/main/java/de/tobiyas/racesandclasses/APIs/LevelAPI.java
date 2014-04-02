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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class LevelAPI {


	/**
	 * Returns the Plugin
	 * 
	 * @return the Plugin
	 */
	private static RacesAndClasses getPlugin(){
		return RacesAndClasses.getPlugin();
	}
	
	
	/**
	 * Adds a level to the player
	 * 
	 * @param playerUUID to add to 
	 * @param levelToAdd to add
	 * 
	 * @deprecated use {@link #addLevel(OfflinePlayer, int)} instead
	 */
	@Deprecated
	public static void addLevel(String playerName, int levelToAdd){
		addLevel(Bukkit.getOfflinePlayer(playerName), levelToAdd);
	}
	
	/**
	 * Adds a level to the player
	 * 
	 * @param playerUUID to add to 
	 * @param levelToAdd to add
	 */
	public static void addLevel(OfflinePlayer player, int levelToAdd){
		int currentLevel = getCurrentLevel(player);
		int newLevel = currentLevel + levelToAdd;
		
		getPlugin().getPlayerManager().getPlayerLevelManager(player).setCurrentLevel(newLevel);
	}
	
	
	/**
	 * Removes a level from the Player
	 * 
	 * @param playerUUID to remove from
	 * @param levelToRemove to remove
	 * 
	 * @deprecated use #
	 */
	@Deprecated
	public static void removeLevel(String playerName, int levelToRemove){
		removeLevel(Bukkit.getOfflinePlayer(playerName), levelToRemove);
	}
	
	/**
	 * Removes a level from the Player
	 * 
	 * @param playerUUID to remove from
	 * @param levelToRemove to remve
	 */
	public static void removeLevel(OfflinePlayer player, int levelToRemove){
		int currentLevel = getCurrentLevel(player);
		int newLevel = currentLevel - levelToRemove;
		if(newLevel < 1) newLevel = 1;
		
		getPlugin().getPlayerManager().getPlayerLevelManager(player).setCurrentLevel(newLevel);
	}
	
	
	/**
	 * Adds exp to an player
	 * 
	 * @param playerUUID to add to
	 * @param amount to 
	 * 
	 * @deprecated use {@link #addExp(OfflinePlayer, double)} instead
	 */
	@Deprecated
	public static void addExp(String playerName, double amount){
		addExp(Bukkit.getOfflinePlayer(playerName), amount);
	}
	
	
	/**
	 * Adds exp to an player
	 * 
	 * @param playerUUID to add to
	 * @param amount to 
	 */
	public static void addExp(OfflinePlayer player, double amount){
		getPlugin().getPlayerManager().getPlayerLevelManager(player).addExp((int)amount);
	}
	
	
	/**
	 * Removes the EXP passed from the Player
	 * 
	 * @param playerUUID to remove from
	 * @param amount to remove
	 * 
	 * @deprecated use {@link #removeExp(OfflinePlayer, double)} instead
	 */
	@Deprecated
	public static void removeExp(String playerName, double amount){
		removeExp(Bukkit.getOfflinePlayer(playerName), amount);
	}
	
	/**
	 * Removes the EXP passed from the Player
	 * 
	 * @param playerUUID to remove from
	 * @param amount to remove
	 */
	public static void removeExp(OfflinePlayer player, double amount){
		getPlugin().getPlayerManager().getPlayerLevelManager(player).removeExp((int)amount);
	}
	
	
	/**
	 * Returns the current level of the Player 
	 * 
	 * @param playerUUID to get
	 * @return the level of the player
	 * 
	 * @deprecated use {@link #getCurrentLevel(OfflinePlayer)} instead
	 */
	@Deprecated
	public static int getCurrentLevel(String playerName){
		return getCurrentLevel(Bukkit.getOfflinePlayer(playerName));
	}
	
	
	/**
	 * Returns the current level of the Player 
	 * 
	 * @param playerUUID to get
	 * @return the level of the player
	 */
	public static int getCurrentLevel(OfflinePlayer player){
		return getPlugin().getPlayerManager().getPlayerLevelManager(player).getCurrentLevel();
	}
	
	
	/**
	 * sets the current level of the Player 
	 * 
	 * @param playerUUID to get
	 * @param level the level to set
	 * 
	 * @deprecated use {@link #setCurrentLevel(OfflinePlayer, int)} instead
	 */
	@Deprecated
	public static void setCurrentLevel(String playerName, int level){
		setCurrentLevel(Bukkit.getOfflinePlayer(playerName), level);
	}
	
	
	/**
	 * sets the current level of the Player 
	 * 
	 * @param playerUUID to get
	 * @param level the level to set
	 */
	public static void setCurrentLevel(OfflinePlayer player, int level){
		getPlugin().getPlayerManager().getPlayerLevelManager(player).setCurrentLevel(level);
	}
	
	/**
	 * Gets the current exp of the level.
	 * 
	 * @param playerUUID to get from
	 * 
	 * @return the current exp of the level
	 * 
	 * @deprecated use {@link #getCurrentExpOfLevel(OfflinePlayer)} instead
	 */
	public static double getCurrentExpOfLevel(String playerName){
		return getCurrentExpOfLevel(Bukkit.getOfflinePlayer(playerName));
	}
	
	/**
	 * Gets the current exp of the level.
	 * 
	 * @param playerUUID to get from
	 * 
	 * @return the current exp of the level
	 */
	public static double getCurrentExpOfLevel(OfflinePlayer player){
		return getPlugin().getPlayerManager().getPlayerLevelManager(player).getCurrentExpOfLevel();
	}
}
