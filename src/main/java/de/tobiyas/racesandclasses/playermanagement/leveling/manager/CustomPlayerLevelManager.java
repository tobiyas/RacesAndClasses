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
package de.tobiyas.racesandclasses.playermanagement.leveling.manager;

import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelDownEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelUpEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerLostEXPEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerReceiveEXPEvent;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.playermanagement.display.Display;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.DisplayGenerator;
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelCalculator;
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelPackage;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.config.YAMLConfigExtended;

public class CustomPlayerLevelManager extends AbstractPlayerLevelingSystem implements Observer{

	/**
	 * The Path to the current Level of the Player
	 */
	public static final String CURRENT_PLAYER_LEVEL_PATH =".level.currentLevel";
	
	/**
	 * The Path to the current EXP of the Level of the Player
	 */
	public static final String CURRENT_PLAYER_LEVEL_EXP_PATH =".level.currentLevelEXP";

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
	 * @param player
	 */
	public CustomPlayerLevelManager(RaCPlayer player) {
		super(player);
		
		this.currentLevel = 1;
		this.currentExpOfLevel = 0;
		
		rescanDisplay();
	}
	
	/**
	 * This re-registers the display.
	 * <br>Meaning to throw the old one away and generate a new one.
	 */
	private void rescanDisplay(){
		if(expDisplay != null) expDisplay.unregister();
		if(levelDisplay != null) levelDisplay.unregister();
		
		expDisplay = DisplayGenerator.generateDisplay(player, DisplayInfos.LEVEL_EXP);
		levelDisplay = DisplayGenerator.generateDisplay(player, DisplayInfos.LEVEL);
	}
	
	
	@Override
	public void reloadFromYaml(){
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		if(!config.getValidLoad()){
			return;
		}
		
		currentLevel = config.getInt(CURRENT_PLAYER_LEVEL_PATH, 1);
		currentExpOfLevel = config.getInt(CURRENT_PLAYER_LEVEL_EXP_PATH, 1);
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
	public RaCPlayer getPlayer() {
		return player;
	}
	
	@Override
	public int getMaxEXPToNextLevel() {
		return LevelCalculator.calculateLevelPackage(currentLevel).getMaxEXP();
	}
	
	@Override
	public void setCurrentLevel(int level) {
		int maxLvl = LevelAPI.getMaxLevel();
		if(maxLvl > 0) level = Math.min(maxLvl, level);
		
		int oldLevel = currentLevel;
		this.currentLevel = level;
		checkLevelChanged();
		
		if(oldLevel != currentLevel){
			RacesAndClasses.getPlugin().fireEventToBukkit( 
					(oldLevel < currentExpOfLevel) 
					? new LevelUpEvent(player, oldLevel, currentLevel)
					: new LevelDownEvent(player, oldLevel, level) );
		}
	}


	@Override
	public void setCurrentExpOfLevel(int currentExpOfLevel) {
		this.currentExpOfLevel = currentExpOfLevel;
		checkLevelChanged();
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
		int maxLvl = LevelAPI.getMaxLevel();
		if(maxLvl > 0 && getCurrentLevel() >= maxLvl) return false;
		
		PlayerReceiveEXPEvent expEvent = new PlayerReceiveEXPEvent(player, exp);
		
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
		int maxLvl = LevelAPI.getMaxLevel();
		while(currentExpOfLevel >= levelPack.getMaxEXP()){
			if(maxLvl > 0 && getCurrentLevel() >= maxLvl) {
				currentExpOfLevel = 0;
				break;
			}
			
			currentLevel++;
			currentExpOfLevel -= levelPack.getMaxEXP();
			
			Bukkit.getPluginManager().callEvent(new LevelUpEvent(player, currentLevel, currentLevel + 1));
			
			levelPack = LevelCalculator.calculateLevelPackage(currentLevel);
		}
		
		// expDisplay.display(currentExpOfLevel, levelPack.getMaxEXP());
		// levelDisplay.display(currentLevel, currentLevel);
		
		tick();
		return true;
	}
	
	
	
	@Override
	public boolean removeExp(int exp){		
		PlayerLostEXPEvent expEvent = new PlayerLostEXPEvent(player, exp);
		
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
		while(currentExpOfLevel < 0){
			if(currentLevel == 1) {
				currentExpOfLevel = 0;
				return true;
			}
			
			currentLevel--;
			currentExpOfLevel += levelPack.getMaxEXP();
			
			Bukkit.getPluginManager().callEvent(new LevelDownEvent(player, currentLevel + 1, currentLevel));
			
			levelPack = LevelCalculator.calculateLevelPackage(currentLevel - 1);
		}
		
		// expDisplay.display(currentExpOfLevel, levelPack.getMaxEXP());
		// levelDisplay.display(currentLevel, currentLevel);
		
		tick();
		return true;
	}
	


	@Override
	public void save() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		if(!config.getValidLoad()){
			return;
		}
	
		config.set(CURRENT_PLAYER_LEVEL_PATH, currentLevel);
		config.set(CURRENT_PLAYER_LEVEL_EXP_PATH, currentExpOfLevel);
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

	

	@Override
	public boolean canRemove(int toRemove) {
		toRemove -= getCurrentExpOfLevel();
		return toRemove > 0;
	}

	@Override
	public void addLevel(int value) {
		if(value <= 0) return;
		
		int oldLevel = currentLevel;
		currentLevel += value;
		
		int maxLevel = LevelAPI.getMaxLevel();
		if(maxLevel > 0) currentLevel = Math.min(currentLevel, maxLevel);
		
		LevelEvent event = new LevelUpEvent(getPlayer(), oldLevel, currentLevel);
		RacesAndClasses.getPlugin().fireEventToBukkit(event);
	}

	@Override
	public void removeLevel(int value) {
		if(value <= 0) return;
		
		int oldLevel = currentLevel;
		currentLevel -= value;
		if(currentLevel < 1) currentLevel = 1;
		
		LevelEvent event = new LevelDownEvent(getPlayer(), oldLevel, currentLevel);
		RacesAndClasses.getPlugin().fireEventToBukkit(event);
	}

}
