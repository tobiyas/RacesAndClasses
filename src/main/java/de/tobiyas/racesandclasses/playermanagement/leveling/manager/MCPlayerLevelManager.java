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
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerLostEXPEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerReceiveEXPEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;

public class MCPlayerLevelManager extends AbstractPlayerLevelingSystem {
	
	
	/**
	 * Creates a default MC Level Manager with MC Player Levels.
	 * 
	 * @param player to create for
	 * @param savingContainer 
	 */
	public MCPlayerLevelManager(RaCPlayer player, PlayerSavingData data) {
		super(player, data);
	}
	
	
	@Override
	public int getCurrentLevel() {
		return getRealPlayer().getLevel();
	}

	@Override
	public int getCurrentExpOfLevel() {
		return (int) (getRealPlayer().getExp() * getRealPlayer().getExpToLevel());
	}


	@Override
	public void setCurrentLevel(int level) {
		getRealPlayer().setLevel(level);
	}

	@Override
	public void setCurrentExpOfLevel(int currentExpOfLevel) {
		getRealPlayer().setExp(currentExpOfLevel / getRealPlayer().getExpToLevel());
	}

	@Override
	public boolean addExp(int exp) {
		PlayerReceiveEXPEvent expEvent = new PlayerReceiveEXPEvent(player, exp);
		
		Bukkit.getPluginManager().callEvent(expEvent);
		if(expEvent.isCancelled()){
			return false;
		}
		
		exp = expEvent.getExp();
		if(exp < 1){
			return false;
		}
		
		getRealPlayer().giveExp(exp);
		return true;
	}

	@Override
	public boolean removeExp(int exp) {
		PlayerLostEXPEvent expEvent = new PlayerLostEXPEvent(player, exp);
		
		Bukkit.getPluginManager().callEvent(expEvent);
		if(expEvent.isCancelled()){
			return false;
		}
		
		exp = expEvent.getExp();
		if(exp < 1){
			return false;
		}		
		
		int totalExp = getRealPlayer().getTotalExperience();
		int newTotalExp = totalExp - exp;
		
		if(newTotalExp < 0) newTotalExp = 0;
		getRealPlayer().setTotalExperience(newTotalExp);
		return true;
	}


	@Override
	public void checkLevelChanged() {
	}

	/**
	 * Returns the Player associated with this container.
	 * 
	 * @return
	 */
	private Player getRealPlayer(){
		return player.getPlayer();
	}


	@Override
	public boolean canRemove(int toRemove) {
		toRemove -= getCurrentExpOfLevel();
		return toRemove > 0;
	}


	@Override
	public void addLevel(int value) {
		getRealPlayer().giveExpLevels(value);
	}


	@Override
	public void removeLevel(int value) {
		int newLevel = Math.max(0, getCurrentLevel() - value);
		setCurrentLevel(newLevel);
	}
	
	
	@Override
	public int getMaxEXPToNextLevel() {
		Player pl = player.getPlayer();
		return (int) (pl.getExpToLevel() * (1d/(1d - pl.getExp())));
	}
}
