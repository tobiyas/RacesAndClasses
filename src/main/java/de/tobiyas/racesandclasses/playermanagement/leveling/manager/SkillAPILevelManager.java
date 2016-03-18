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
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;

public class SkillAPILevelManager extends AbstractPlayerLevelingSystem {

	
	/**
	 * Sets up the Plugin.
	 */
	public SkillAPILevelManager(RaCPlayer player, PlayerSavingData data) {
		super(player, data);
	}
	
	
	@Override
	public int getCurrentLevel() {
		if(!isSkillAPIPresent()) return 1;
		PlayerData data = SkillAPI.getPlayerData(player.getPlayer());
		int level = 0;
		for(PlayerClass clazz : data.getClasses()) level += clazz.getLevel();
		
		return level;
	}

	@Override
	public int getCurrentExpOfLevel() {
		if(!isSkillAPIPresent()) return 1;
		
		return 0;
		//return getSkillAPI().getPlayer(player.getName()).getExp();
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
		//getSkillAPI().getPlayer(player.getName()).giveExp(exp);
		return false;
	}

	@Override
	public boolean removeExp(int exp) {
		if(!isSkillAPIPresent()) return false;
		
		//getSkillAPI().getPlayer(player.getName()).loseExp(exp);
		return false;
	}

	@Override
	public void checkLevelChanged() {
		//nothing to do.
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
		//getSkillAPI().getPlayer(player.getPlayer()).levelUp(value);
	}


	@Override
	public void removeLevel(int value) {
		//TODO don't know how... :(
		//getSkillAPI().getPlayer(player.getPlayer()).levelUp(-1);
	}
	
	@Override
	public int getMaxEXPToNextLevel() {
		//PlayerSkills skills = getSkillAPI().getPlayer(player.getPlayer());
		//return skills.getExp() + skills.getExpToNextLevel();
		
		return 1;
	}
}
