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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.playermanagement.display.Display;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.DisplayGenerator;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class McMMOLevelManager implements PlayerLevelManager {
	
	/**
	 * The Playername to this levelsystem.
	 */
	private final RaCPlayer player;
	
	
	/**
	 * The Display to show.
	 */
	private Display expDisplay;

	/**
	 * The Display to show.
	 */
	private Display levelDisplay;
	
	/**
	 * The String to calc the McMMO Level.
	 */
	private final String calcString;
	
	/**
	 * Sets up the Plugin.
	 */
	public McMMOLevelManager(RaCPlayer player) {
		this.player = player;
		
		rescanDisplay();
		calcString = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString();
	}
	
	
	@Override
	public int getCurrentLevel() {
		if(!isMcMMOPresent()) return 1;
		
		return calcCurrentLevel();
	}

	@Override
	public int getCurrentExpOfLevel() {
		if(!isMcMMOPresent()) return 1;

		return 0;
	}

	@Override
	public RaCPlayer getPlayer() {
		return player;
	}
	

	@Override
	public void tick() {
		//not needed
	}

	@Override
	public void setCurrentLevel(int level) {
		//not supported by Skill API
	}

	@Override
	public void setCurrentExpOfLevel(int currentExpOfLevel) {
		//not supported by Skill API
	}

	@Override
	public boolean addExp(int exp) {
		return false;
	}

	@Override
	public boolean removeExp(int exp) {
		return false;
	}

	@Override
	public void save() {
		//this is done via Skill-API
	}

	@Override
	public void saveTo(PlayerSavingContainer container) {
		container.setPlayerLevel(getCurrentLevel());
		container.setPlayerLevelExp(getCurrentExpOfLevel());
	}

	@Override
	public void reloadFromPlayerSavingContaienr(PlayerSavingContainer container) {
		//not supported by Skill-API
	}

	@Override
	public void checkLevelChanged() {
		//nothing to do.
	}

	@Override
	public void reloadFromYaml() {
		//nothing to do.
	}

	@Override
	public void forceDisplay() {
		if(player == null || !player.isOnline()) return;
		
		expDisplay.display(0, 1);
		levelDisplay.display(getCurrentLevel(), getCurrentLevel());
	}
	
	/**
	 * This re-registers the display.
	 * <br>Meaning to throw the old one away and generate a new one.
	 */
	private void rescanDisplay(){
		if(expDisplay != null){
			expDisplay.unregister();
		}
		
		expDisplay = DisplayGenerator.generateDisplay(player, DisplayInfos.LEVEL_EXP);
		levelDisplay = DisplayGenerator.generateDisplay(player, DisplayInfos.LEVEL);
	}

	@Override
	public boolean canRemove(int toRemove) {
		return false;
	}

	
	/**
	 * Checks if the mcMMO Plugin is present.
	 * 
	 * @return true if is present, false if not.
	 */
	private boolean isMcMMOPresent(){
		try{
			mcMMO mcMMO = getMCMMO();
			if(mcMMO == null) return false;
			
			return mcMMO.isEnabled();
		}catch(Throwable exp){ return false; }
	}
	
	/**
	 * Returns the Skill API class.
	 * 
	 * @return the Skill API class or Null.
	 */
	private mcMMO getMCMMO(){
		try{
			return (mcMMO) Bukkit.getPluginManager().getPlugin("mcMMO");
		}catch(Throwable exp){ return null; }
	}
	
	
	/**
	 * Checks if the McMMO String works.
	 * 
	 * @param generatorString
	 * @return
	 */
	public static boolean verifyGeneratorStringWorks(String generatorString){
		try{
			generatorString = generatorString.toLowerCase();
			ScriptEngineManager mgr = null;
			ScriptEngine engine = null;
			try{
				mgr = new ScriptEngineManager();
				engine = mgr.getEngineByName("JavaScript");
			}catch(Throwable exp){
				RacesAndClasses.getPlugin().logStackTrace("Could no load JavaScript Engine! You can't use mcmmo skills.", exp);
				return false;
			}
			
			generatorString = generatorString.replace("{powerlevel}", String.valueOf(42));
		    for(SkillType type : SkillType.values()){
		    	generatorString = generatorString.replace("{" + type.name().toLowerCase() + "}", String.valueOf(42));
		    	generatorString = generatorString.replace("{" + type.getName().toLowerCase() + "}", String.valueOf(42));

		    	generatorString = generatorString.replace(type.name().toLowerCase(), String.valueOf(42));
		    	generatorString = generatorString.replace(type.getName().toLowerCase(), String.valueOf(42));
		    }
		
	    	String parsedValue = (String) engine.eval(generatorString).toString();	    	
	    	double doubleValue = Double.parseDouble(parsedValue);
	    	Integer intValue = (int) doubleValue;
	    	
	    	return intValue != null;
	    }catch(Throwable exp){
	    	RacesAndClasses.getPlugin().logStackTrace("Could not compile your MCMMO Level String. Please fix it!", exp);
	    	return false;
	    }
	}
	
	
	/**
	 * This calcs the current level of the Player passed.
	 * 
	 * @param playerName to parse
	 * 
	 * @return the level of the Player or 1 if something gone wrong.
	 */
	private int calcCurrentLevel(){
		String generatorString = this.calcString.toLowerCase();
		try{
			ScriptEngineManager mgr = null;
			ScriptEngine engine = null;
			try{
				mgr = new ScriptEngineManager();
				engine = mgr.getEngineByName("JavaScript");
			}catch(Throwable exp){
				return 1;
			}
			
		    
			Player pl = getPlayer().getPlayer();
			int powerLevel = ExperienceAPI.getPowerLevel(player);
			generatorString = generatorString.replace("{powerlevel}", String.valueOf(powerLevel));
			
			
		    for(SkillType type : SkillType.values()){
		    	int skillLevel = ExperienceAPI.getLevel(pl, type.name());
		    	
		    	generatorString = generatorString.replace("{" + type.getName().toLowerCase() + "}", String.valueOf(skillLevel));
		    	generatorString = generatorString.replace("{" + type.name().toLowerCase() + "}", String.valueOf(skillLevel));

		    	generatorString = generatorString.replace(type.getName().toLowerCase(), String.valueOf(skillLevel));
		    	generatorString = generatorString.replace(type.name().toLowerCase(), String.valueOf(skillLevel));
		    }
		
	    	String parsedValue = (String) engine.eval(generatorString).toString();	    	
	    	double doubleValue = Double.parseDouble(parsedValue);
	    	Integer intValue = (int) doubleValue;
	    	
	    	return intValue;
	    }catch(Throwable exp){
	    	return 1;
	    }
	}


	@Override
	public void addLevel(int value) {
	}


	@Override
	public void removeLevel(int value) {
	}


	@Override
	public int getMaxEXPToNextLevel() {
		return 1;
	}
}
