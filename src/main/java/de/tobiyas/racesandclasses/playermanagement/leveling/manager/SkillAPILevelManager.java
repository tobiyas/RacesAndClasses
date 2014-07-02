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

import org.bukkit.Bukkit;

import com.sucy.skill.SkillAPI;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.playermanagement.display.Display;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.DisplayGenerator;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;

public class SkillAPILevelManager implements PlayerLevelManager {
	
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
	 * Sets up the Plugin.
	 */
	public SkillAPILevelManager(RaCPlayer player) {
		this.player = player;
		
		rescanDisplay();
	}
	
	
	@Override
	public int getCurrentLevel() {
		if(!isSkillAPIPresent()) return 1;
		return getSkillAPI().getPlayer(player.getName()).getLevel();
	}

	@Override
	public int getCurrentExpOfLevel() {
		if(!isSkillAPIPresent()) return 1;
		return getSkillAPI().getPlayer(player.getName()).getExp();
	}

	@Override
	public RaCPlayer getPlayer() {
		return player;
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
		if(!isSkillAPIPresent()) return false;
		getSkillAPI().getPlayer(player.getName()).giveExp(exp);
		return true;
	}

	@Override
	public boolean removeExp(int exp) {
		if(!isSkillAPIPresent()) return false;
		getSkillAPI().getPlayer(player.getName()).loseExp(exp);
		return true;
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
		
		expDisplay.display(getCurrentExpOfLevel(), getSkillAPI().getPlayer(player.getName()).getExpToNextLevel());
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
		return true;
	}

	
	/**
	 * Checks if the Skill API is present.
	 * 
	 * @return true if is present, false if not.
	 */
	private boolean isSkillAPIPresent(){
		try{
			SkillAPI skillAPI = getSkillAPI();
			if(skillAPI == null) return false;
			
			return skillAPI.isEnabled();
		}catch(Throwable exp){ return false; }
	}
	
	/**
	 * Returns the Skill API class.
	 * 
	 * @return the Skill API class or Null.
	 */
	private SkillAPI getSkillAPI(){
		try{
			return (SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI");
		}catch(Throwable exp){ return null; }
	}


	@Override
	public void addLevel(int value) {
		getSkillAPI().getPlayer(player.getPlayer()).levelUp(value);
	}


	@Override
	public void removeLevel(int value) {
		//TODO don't know how... :(
	}
}
