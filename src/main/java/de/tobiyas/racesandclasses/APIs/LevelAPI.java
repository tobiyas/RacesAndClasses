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
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;

public class LevelAPI {
	
	
	/**
	 * Adds a level to the player
	 * 
	 * @param player to add to 
	 * @param levelToAdd to add
	 * 
	 * @deprecated use {@link #addLevel(Player, int)} instead
	 */
	@Deprecated
	public static void addLevel(String playerName, int levelToAdd){
		addLevel(Bukkit.getPlayer(playerName), levelToAdd);
	}
	
	/**
	 * Adds a level to the player
	 * 
	 * @param orgPlayer to add to 
	 * @param levelToAdd to add
	 */
	public static void addLevel(Player orgPlayer, int levelToAdd){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(orgPlayer);
		player.getLevelManager().addLevel(levelToAdd);
	}
	
	
	/**
	 * Removes a level from the Player
	 * 
	 * @param player to remove from
	 * @param levelToRemove to remove
	 * 
	 * @deprecated use #
	 */
	@Deprecated
	public static void removeLevel(String playerName, int levelToRemove){
		removeLevel(Bukkit.getPlayer(playerName), levelToRemove);
	}
	
	/**
	 * Removes a level from the Player
	 * 
	 * @param player to remove from
	 * @param levelToRemove to remve
	 */
	public static void removeLevel(Player player, int levelToRemove){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		racPlayer.getLevelManager().removeLevel(levelToRemove);
	}
	
	
	/**
	 * Adds exp to an player
	 * 
	 * @param player to add to
	 * @param amount to 
	 * 
	 * @deprecated use {@link #addExp(Player, double)} instead
	 */
	@Deprecated
	public static void addExp(String playerName, int amount){
		addExp(Bukkit.getPlayer(playerName), amount);
	}
	
	
	/**
	 * Adds exp to an player
	 * 
	 * @param player to add to
	 * @param amount to 
	 */
	public static void addExp(Player player, int amount){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		racPlayer.getLevelManager().addExp(amount);
	}
	
	
	/**
	 * Adds exp to an player
	 * 
	 * @param player to add to
	 * @param amount to 
	 */
	public static void addExp(RaCPlayer racPlayer, int amount){
		racPlayer.getLevelManager().addExp(amount);
	}
	
	
	/**
	 * Removes the EXP passed from the Player
	 * 
	 * @param player to remove from
	 * @param amount to remove
	 * 
	 * @deprecated use {@link #removeExp(Player, double)} instead
	 */
	@Deprecated
	public static void removeExp(String playerName, double amount){
		removeExp(Bukkit.getPlayer(playerName), amount);
	}
	
	/**
	 * Removes the EXP passed from the Player
	 * 
	 * @param player to remove from
	 * @param amount to remove
	 */
	public static void removeExp(Player player, double amount){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		racPlayer.getLevelManager().removeExp((int)amount);
	}
	
	
	/**
	 * Removes the EXP passed from the Player
	 * 
	 * @param player to remove from
	 * @param amount to remove
	 */
	public static void removeExp(RaCPlayer racPlayer, double amount){
		racPlayer.getLevelManager().removeExp((int)amount);
	}
	
	
	/**
	 * Returns the current level of the Player 
	 * 
	 * @param player to get
	 * @return the level of the player
	 * 
	 * @deprecated use {@link #getCurrentLevel(Player)} instead
	 */
	@Deprecated
	public static int getCurrentLevel(String playerName){
		return getCurrentLevel(Bukkit.getPlayer(playerName));
	}
	
	
	/**
	 * Returns the current level of the Player 
	 * 
	 * @param player to get
	 * @return the level of the player
	 */
	public static int getCurrentLevel(Player player){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		return racPlayer.getLevelManager().getCurrentLevel();
	}
	
	
	/**
	 * Returns the current level of the Player 
	 * 
	 * @param player to get
	 * @return the level of the player
	 */
	public static int getCurrentLevel(RaCPlayer racPlayer){
		return racPlayer.getLevelManager().getCurrentLevel();
	}
	
	
	/**
	 * sets the current level of the Player 
	 * 
	 * @param player to get
	 * @param level the level to set
	 * 
	 * @deprecated use {@link #setCurrentLevel(Player, int)} instead
	 */
	@Deprecated
	public static void setCurrentLevel(String playerName, int level){
		setCurrentLevel(Bukkit.getPlayer(playerName), level);
	}

	
	
	/**
	 * sets the current level of the Player 
	 * 
	 * @param player to get
	 * @param level the level to set
	 */
	public static void setCurrentLevel(Player player, int level){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		setCurrentLevel(racPlayer, level);
	}
	
	/**
	 * sets the current level of the Player 
	 * 
	 * @param player to get
	 * @param level the level to set
	 */
	public static void setCurrentLevel(RaCPlayer racPlayer, int level){
		racPlayer.getLevelManager().setCurrentLevel(level);
	}
	
	/**
	 * Gets the current exp of the level.
	 * 
	 * @param player to get from
	 * 
	 * @return the current exp of the level
	 * 
	 * @deprecated use {@link #getCurrentExpOfLevel(Player)} instead
	 */
	public static int getCurrentExpOfLevel(String playerName){
		return getCurrentExpOfLevel(Bukkit.getPlayer(playerName));
	}
	
	/**
	 * Gets the current exp of the level.
	 * 
	 * @param player to get from
	 * 
	 * @return the current exp of the level
	 */
	public static int getCurrentExpOfLevel(Player player){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		return racPlayer.getLevelManager().getCurrentExpOfLevel();
	}
	
	/**
	 * Sets the current exp of the level.
	 * 
	 * @param player to get from
	 * @param exp the current exp of the level
	 * 
	 * @deprecated use {@link #getCurrentExpOfLevel(Player)} instead
	 */
	public static void setCurrentExpOfLevel(String playerName, int exp){
		setCurrentExpOfLevel(Bukkit.getPlayer(playerName), exp);
	}
	
	/**
	 * Gets the current exp of the level.
	 * 
	 * @param player to get from
	 * @param exp the current exp of the level
	 */
	public static void setCurrentExpOfLevel(Player player, int exp){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		racPlayer.getLevelManager().setCurrentExpOfLevel(exp);
	}
}
