package de.tobiyas.racesandclasses.APIs;

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
	 * @param playerName to add to 
	 * @param levelToAdd to add
	 */
	public static void addLevel(String playerName, int levelToAdd){
		int currentLevel = getCurrentLevel(playerName);
		int newLevel = currentLevel + levelToAdd;
		
		getPlugin().getPlayerManager().getPlayerLevelManager(playerName).setCurrentLevel(newLevel);
	}
	
	
	/**
	 * Removes a level from the Player
	 * 
	 * @param playerName to remove from
	 * @param levelToRemove to remve
	 */
	public static void removeLevel(String playerName, int levelToRemove){
		int currentLevel = getCurrentLevel(playerName);
		int newLevel = currentLevel - levelToRemove;
		if(newLevel < 1) newLevel = 1;
		
		getPlugin().getPlayerManager().getPlayerLevelManager(playerName).setCurrentLevel(newLevel);
	}
	
	
	/**
	 * Adds exp to an player
	 * 
	 * @param playerName to add to
	 * @param amount to 
	 */
	public static void addExp(String playerName, double amount){
		getPlugin().getPlayerManager().getPlayerLevelManager(playerName).addExp((int)amount);
	}
	
	
	/**
	 * Removes the EXP passed from the Player
	 * 
	 * @param playerName to remove from
	 * @param amount to remove
	 */
	public static void removeExp(String playerName, double amount){
		getPlugin().getPlayerManager().getPlayerLevelManager(playerName).removeExp((int)amount);
	}
	
	
	/**
	 * Returns the current level of the Player 
	 * 
	 * @param playerName to get
	 * @return the level of the player
	 */
	public static int getCurrentLevel(String playerName){
		return getPlugin().getPlayerManager().getPlayerLevelManager(playerName).getCurrentLevel();
	}
	
	/**
	 * sets the current level of the Player 
	 * 
	 * @param playerName to get
	 * @param level the level to set
	 */
	public static void setCurrentLevel(String playerName, int level){
		getPlugin().getPlayerManager().getPlayerLevelManager(playerName).setCurrentLevel(level);
	}
	
	/**
	 * Gets the current exp of the level.
	 * 
	 * @param playerName to get from
	 * 
	 * @return the current exp of the level
	 */
	public static double getCurrentExpOfLevel(String playerName){
		return getPlugin().getPlayerManager().getPlayerLevelManager(playerName).getCurrentExpOfLevel();
	}
}
