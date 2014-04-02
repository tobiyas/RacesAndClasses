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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
	private final UUID playerUUID;
	
	
	/**
	 * Creates a default MC Level Manager with MC Player Levels.
	 * 
	 * @param playerUUID to create for
	 */
	public MCPlayerLevelManager(UUID playerID) {
		this.playerUUID = playerID;
	}
	
	
	@Override
	public int getCurrentLevel() {
		return getPlayer().getLevel();
	}

	@Override
	public int getCurrentExpOfLevel() {
		return (int) (getPlayer().getExp() * getPlayer().getExpToLevel());
	}

	@Override
	public UUID getPlayerUUID() {
		return playerUUID;
	}

	@Override
	public void setCurrentLevel(int level) {
		getPlayer().setLevel(level);
	}

	@Override
	public void setCurrentExpOfLevel(int currentExpOfLevel) {
		getPlayer().setExp(currentExpOfLevel / getPlayer().getExpToLevel());
	}

	@Override
	public boolean addExp(int exp) {
		PlayerReceiveEXPEvent expEvent = new PlayerReceiveEXPEvent(playerUUID, exp);
		
		Bukkit.getPluginManager().callEvent(expEvent);
		if(expEvent.isCancelled()){
			return false;
		}
		
		exp = expEvent.getExp();
		if(exp < 1){
			return false;
		}
		
		getPlayer().giveExp(exp);
		return true;
	}

	@Override
	public boolean removeExp(int exp) {
		PlayerLostEXPEvent expEvent = new PlayerLostEXPEvent(playerUUID, exp);
		
		Bukkit.getPluginManager().callEvent(expEvent);
		if(expEvent.isCancelled()){
			return false;
		}
		
		exp = expEvent.getExp();
		if(exp < 1){
			return false;
		}		
		
		int totalExp = getPlayer().getTotalExperience();
		int newTotalExp = totalExp - exp;
		
		if(newTotalExp < 0) newTotalExp = 0;
		getPlayer().setTotalExperience(newTotalExp);
		return true;
	}

	@Override
	public void save() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(Bukkit.getPlayer(playerUUID));
		if(!config.getValidLoad()){
			return;
		}
		
		Player player = getPlayer();
	
		config.set(CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_PATH, player.getLevel());
		config.set(CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_EXP_PATH, (int)(player.getExp() * player.getExpToLevel()));
	}

	@Override
	public void saveTo(PlayerSavingContainer container) {
		Player player = getPlayer();
		
		container.setPlayerLevel(player.getLevel());
		container.setPlayerLevelExp((int)(player.getExp() * player.getExpToLevel()));
		
	}

	@Override
	public void reloadFromPlayerSavingContaienr(PlayerSavingContainer container){
		Player player = getPlayer();
		
		player.setLevel(container.getPlayerLevel());
		player.setExp(container.getPlayerLevelExp() / player.getExpToLevel());
	}

	@Override
	public void checkLevelChanged() {
	}

	@Override
	public void reloadFromYaml() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(Bukkit.getPlayer(playerUUID));
		if(!config.getValidLoad()){
			return;
		}
		
		Player player = getPlayer();
		
		player.setLevel(config.getInt("playerdata." + playerUUID + CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_PATH, 1));
		player.setExp(config.getInt("playerdata." + playerUUID + CustomPlayerLevelManager.CURRENT_PLAYER_LEVEL_EXP_PATH, 1)
				/ player.getExpToLevel());
	}

	
	/**
	 * Returns the Player associated with this container.
	 * 
	 * @return
	 */
	private Player getPlayer(){
		return Bukkit.getPlayer(playerUUID);
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
	
}
