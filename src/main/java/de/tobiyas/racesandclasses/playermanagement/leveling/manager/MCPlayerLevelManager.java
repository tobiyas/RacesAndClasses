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

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerLostEXPEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerReceiveEXPEvent;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.util.config.YAMLConfigExtended;

public class MCPlayerLevelManager implements PlayerLevelManager{

	/**
	 * The Player this level Manager is belonging to.
	 */
	private final RaCPlayer player;
	
	
	/**
	 * Creates a default MC Level Manager with MC Player Levels.
	 * 
	 * @param player to create for
	 */
	public MCPlayerLevelManager(RaCPlayer player) {
		this.player = player;
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
	public RaCPlayer getPlayer() {
		return player;
	}
	

	@Override
	public void tick() {
		//not needed
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
	public void save() {
		//we can't save offline players.
		if(!player.isOnline() || getRealPlayer() == null) return;
		
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		if(!config.getValidLoad()){
			return;
		}
		
		config.set(CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_PATH, player.getPlayer().getLevel());
		config.set(CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_EXP_PATH, (int)(player.getPlayer().getExp() 
				* player.getPlayer().getExpToLevel()));
	}

	@Override
	public void saveTo(PlayerSavingContainer container) {
		//we can't save offline players.
		if(!player.isOnline()) return;
		
		Player player = getRealPlayer();
		
		container.setPlayerLevel(player.getLevel());
		container.setPlayerLevelExp((int)(player.getExp() * player.getExpToLevel()));
		
	}

	@Override
	public void reloadFromPlayerSavingContaienr(PlayerSavingContainer container){
		Player player = getRealPlayer();
		
		player.setLevel(container.getPlayerLevel());
		player.setExp(container.getPlayerLevelExp() / player.getExpToLevel());
	}

	@Override
	public void checkLevelChanged() {
	}

	@Override
	public void reloadFromYaml() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		if(!config.getValidLoad()){
			return;
		}
		
		player.getPlayer().setLevel(config.getInt(CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_PATH, 1));
		player.getPlayer().setExp(config.getInt(CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_EXP_PATH, 1)
				/ player.getPlayer().getExpToLevel());
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
	public void forceDisplay() {
		//we have no display to force... The EXP bar is our display.
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
