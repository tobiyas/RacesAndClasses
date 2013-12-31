package de.tobiyas.racesandclasses.playermanagement.leveling;

import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelDownEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelUpEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerLostEXPEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerReceiveEXPEvent;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.playermanagement.display.Display;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.DisplayGenerator;
import de.tobiyas.util.config.YAMLConfigExtended;

public class CustomPlayerLevelManager implements PlayerLevelManager, Observer{

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
	 * The Display to show.
	 */
	private Display expDisplay;

	/**
	 * The Display to show.
	 */
	private Display levelDisplay;
	
	
	/**
	 * Creates a LevelManager for the Player.
	 * 
	 * @param playerName
	 */
	public CustomPlayerLevelManager(String playerName) {
		this.playerName = playerName;
		
		this.currentLevel = 1;
		this.currentExpOfLevel = 0;
		
		rescanDisplay();
	}
	
	/**
	 * This re-registers the display.
	 * <br>Meaning to throw the old one away and generate a new one.
	 */
	private void rescanDisplay(){
		if(expDisplay != null){
			expDisplay.unregister();
		}
		
		expDisplay = DisplayGenerator.generateDisplay(playerName, DisplayInfos.LEVEL_EXP);
		levelDisplay = DisplayGenerator.generateDisplay(playerName, DisplayInfos.LEVEL);
	}
	
	
	@Override
	public void reloadFromYaml(){
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
		if(!config.getValidLoad()){
			return;
		}
		
		currentLevel = config.getInt("playerdata." + playerName + CURRENT_PLAYER_LEVEL_PATH, 1);
		currentExpOfLevel = config.getInt("playerdata." + playerName + CURRENT_PLAYER_LEVEL_EXP_PATH, 1);
	}
	
	
	@Override
	public void reloadFromPlayerSavingContaienr(PlayerSavingContainer container){
		this.currentLevel = container.getPlayerLevel();
		this.currentExpOfLevel = container.getPlayerLevelExp();
	}
	
	
	@Override
	public int getCurrentExpOfLevel(){
		return currentExpOfLevel;
	}
	
	
	@Override
	public int getCurrentLevel(){
		return currentLevel;
	}


	@Override
	public String getPlayerName() {
		return playerName;
	}
	
	
	@Override
	public void setCurrentLevel(int level) {
		this.currentLevel = level;
	}


	@Override
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
	
	
	@Override
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
		
		expDisplay.display(currentExpOfLevel, levelPack.getMaxEXP());
		levelDisplay.display(currentLevel, currentLevel);
		return true;
	}
	
	
	
	@Override
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
		
		expDisplay.display(currentExpOfLevel, levelPack.getMaxEXP());
		levelDisplay.display(currentLevel, currentLevel);
		return true;
	}


	@Override
	public void save() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
		if(!config.getValidLoad()){
			return;
		}
	
		config.set("playerdata." + playerName + CURRENT_PLAYER_LEVEL_PATH, currentLevel);
		config.set("playerdata." + playerName + CURRENT_PLAYER_LEVEL_EXP_PATH, currentExpOfLevel);
	}


	@Override
	public void saveTo(PlayerSavingContainer container) {
		container.setPlayerLevel(currentLevel);
		container.setPlayerLevelExp(currentExpOfLevel);
	}

	@Override
	public void update(Observable o, Object arg) {
		String changedValue = (String) arg;
		
		if(changedValue.equalsIgnoreCase("displayType")){
			rescanDisplay();
		}
	}

	@Override
	public void forceDisplay() {
		LevelPackage levelPack = LevelCalculator.calculateLevelPackage(currentLevel);
		expDisplay.display(currentExpOfLevel, levelPack.getMaxEXP());
		levelDisplay.display(currentLevel, currentLevel);
	}

	
}
