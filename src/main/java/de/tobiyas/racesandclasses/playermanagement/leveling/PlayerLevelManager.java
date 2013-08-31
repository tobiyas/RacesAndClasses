package de.tobiyas.racesandclasses.playermanagement.leveling;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelDownEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelUpEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerLostEXPEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerReceiveEXPEvent;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.util.persistence.YAMLPersistenceProvider;
import de.tobiyas.util.config.YAMLConfigExtended;

public class PlayerLevelManager {

	/**
	 * The Path to the current Level of the Player
	 */
	public static final String CURRENT_PLAYER_LEVEL_PATH =".level.currentLevel";
	
	/**
	 * The Path to the current EXP of the Level of the Player
	 */
	public static final String CURRENT_PLAYER_LEVEL_EXP_PATH =".level.currentLevelEXP";
	
	
	/**
	 * The player this levelManager belongs to
	 */
	private final String playerName;
	
	
	/**
	 * The current currentLevel of a player
	 */
	private int currentLevel;
	
	
	/**
	 * The current EXP of the Level
	 */
	private int currentExpOfLevel;
	
	
	/**
	 * Creates a LevelManager for the Player.
	 * 
	 * @param playerName
	 */
	public PlayerLevelManager(String playerName) {
		this.playerName = playerName;
		
		this.currentLevel = 1;
		this.currentExpOfLevel = 0;
	}
	
	
	/**
	 * Reloads the Data from the Player Data file or DB.
	 */
	public void reloadFromYaml(){
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(true);
		if(!config.getValidLoad()){
			return;
		}
		
		currentLevel = config.getInt("playerdata." + playerName + CURRENT_PLAYER_LEVEL_PATH, 1);
		currentExpOfLevel = config.getInt("playerdata." + playerName + CURRENT_PLAYER_LEVEL_EXP_PATH, 1);
	}
	
	
	/**
	 * Reloads the Data from the passed container.
	 * 
	 * @param container to load from
	 */
	public void reloadFromPlayerSavingContaienr(PlayerSavingContainer container){
		this.currentLevel = container.getPlayerLevel();
		this.currentExpOfLevel = container.getPlayerLevelExp();
	}
	
	
	/**
	 * Returns the current EXP of the Level
	 * 
	 * @return
	 */
	public int getCurrentExpOfLevel(){
		return currentExpOfLevel;
	}
	
	
	/**
	 * Returns the current Level of the Player
	 * 
	 * @return
	 */
	public int getCurrentLevel(){
		return currentLevel;
	}


	/**
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}
	
	
	/**
	 * @param currentLevel the currentLevel to set
	 */
	public void setCurrentLevel(int level) {
		this.currentLevel = level;
	}


	/**
	 * @param currentExpOfLevel the currentExpOfLevel to set
	 */
	public void setCurrentExpOfLevel(int currentExpOfLevel) {
		this.currentExpOfLevel = currentExpOfLevel;
	}


	/**
	 * Checks if the Player has any Levels to go Up or go Down.
	 * They will be fired as Event.
	 */
	public void checkLevelChanged(){
		addExpIntern(0);
		removeExpIntern(0);
	}
	
	
	/**
	 * Adds EXP for a Player. 
	 * If the maxExp is reached, the player will currentLevel up.
	 * 
	 * WARNING: This will cause an event which can be canceled.
	 * When it is canceled, there will be no EXP added.
	 * 
	 * @param exp to add
	 * 
	 * @return true if the EXP were added, false if someone
	 * Canceled the Event or EXP < 1
	 */
	public boolean addExp(int exp){
		PlayerReceiveEXPEvent expEvent = new PlayerReceiveEXPEvent(playerName, exp);
		
		Bukkit.getPluginManager().callEvent(expEvent);
		if(expEvent.isCancelled()){
			return false;
		}
		
		exp = expEvent.getExp();
		if(exp < 1){
			return false;
		}
		
		return addExpIntern(exp);
	}
	
	
	/**
	 * Adds EXP without the Event check.
	 * 
	 * @param exp to add
	 * @return true if worked.
	 */
	protected boolean addExpIntern(int exp){
		currentExpOfLevel += exp;
		
		LevelPackage levelPack = LevelCalculator.calculateLevelPackage(currentLevel);
		while(currentExpOfLevel >= levelPack.getMaxEXP()){
			currentLevel++;
			currentExpOfLevel -= levelPack.getMaxEXP();
			
			Bukkit.getPluginManager().callEvent(new LevelUpEvent(playerName, currentLevel, currentLevel + 1));
			
			levelPack = LevelCalculator.calculateLevelPackage(currentLevel);
		}
		
		return true;
	}
	
	
	
	/**
	 * Removes EXP for a Player. 
	 * If 0 exp of the currentLevel is reached, the player will currentLevel down.
	 * 
	 * WARNING: This will cause an event which can be canceled.
	 * When it is canceled, there will be no EXP removed or the EXP is < 1.
	 * 
	 * @param exp to remove
	 * 
	 * @return true if the EXP were removed, false if someone
	 * Canceled the Event
	 */
	public boolean removeExp(int exp){		
		PlayerLostEXPEvent expEvent = new PlayerLostEXPEvent(playerName, exp);
		
		Bukkit.getPluginManager().callEvent(expEvent);
		if(expEvent.isCancelled()){
			return false;
		}
		
		exp = expEvent.getExp();
		if(exp < 1){
			return false;
		}
		
		return removeExpIntern(exp);
	}
	
	
	/**
	 * Removes EXP without checking Events before
	 * 
	 * @param exp to remove
	 * @return true if worked, false otherwise
	 */
	protected boolean removeExpIntern(int exp){
		currentExpOfLevel -= exp;
		
		LevelPackage levelPack = LevelCalculator.calculateLevelPackage(currentLevel - 1);
		while(currentExpOfLevel <= 0){
			if(currentLevel == 1) {
				currentExpOfLevel = 0;
				return true;
			}
			
			currentLevel--;
			currentExpOfLevel += levelPack.getMaxEXP();
			
			Bukkit.getPluginManager().callEvent(new LevelDownEvent(playerName, currentLevel + 1, currentLevel));
			
			levelPack = LevelCalculator.calculateLevelPackage(currentLevel - 1);
		}
		
		return true;
	}


	/**
	 * Saves the Container to the playerData File.
	 */
	public void save() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(true);
		if(!config.getValidLoad()){
			return;
		}
	
		config.set("playerdata." + playerName + CURRENT_PLAYER_LEVEL_PATH, currentLevel);
		config.set("playerdata." + playerName + CURRENT_PLAYER_LEVEL_EXP_PATH, currentExpOfLevel);
		
		config.save();
	}


	/**
	 * Saves the current values to the passed container.
	 * 
	 * @param container
	 */
	public void saveTo(PlayerSavingContainer container) {
		container.setPlayerLevel(currentLevel);
		container.setPlayerLevelExp(currentExpOfLevel);
	}

	
}
