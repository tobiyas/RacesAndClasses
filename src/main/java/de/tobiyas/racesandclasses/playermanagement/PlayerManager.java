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
package de.tobiyas.racesandclasses.playermanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.pets.PlayerPetManager;
import de.tobiyas.racesandclasses.playermanagement.display.scoreboard.PlayerRaCScoreboardManager;
import de.tobiyas.racesandclasses.playermanagement.health.HealthManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.util.player.PlayerUtils;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class PlayerManager{
	
	/**
	 * This is the map of each player's health container
	 */
	private HashMap<RaCPlayer, PlayerContainer> playerData;
	
	/**
	 * The plugin to access other parts of the Plugin.
	 */
	private RacesAndClasses plugin;
	
	/**
	 * Creates a new PlayerManager and resets all values
	 */
	public PlayerManager(){
		playerData = new HashMap<RaCPlayer, PlayerContainer>();
		plugin = RacesAndClasses.getPlugin();
		
		new DebugBukkitRunnable("RaCPlayerDataTicker") {
			@Override
			protected void runIntern() {
				for(PlayerContainer container : playerData.values()){
					container.tick();
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	/**
	 * Loads the health manager from the playerdata.yml
	 */
	public void init(){
		playerData.clear();
		loadPlayerContainer();
	}
	
	/**
	 * Stores the data in the PlayerManager in the player data ymls
	 */
	public void savePlayerContainer(){
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		for(PlayerContainer container: playerData.values()){
			container.save(useDB);
		}
	}
	
	/**
	 * Shuts down the Player Manager.
	 */
	public void shutdown(){
		savePlayerContainer();
		for(PlayerContainer container: playerData.values()){
			container.shutdown();
		}
		
		playerData.clear();
	}
	
	/**
	 * loads the health manager internally
	 */
	private void loadPlayerContainer(){
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();

		Set<RaCPlayer> players = new HashSet<RaCPlayer>();
		if(useDB){
			for(Player online : PlayerUtils.getOnlinePlayers()){
				RaCPlayer player = RaCPlayerManager.get().getPlayer(online);
				players.add(player);
			}
		}else{
			for(RaCPlayer player : YAMLPersistenceProvider.getAllPlayersKnown()){
				players.add(player);
			}
		}
		
		for(RaCPlayer player : players){
			if(player == null || !player.isOnline()) continue;
			
			PlayerContainer container = PlayerContainer.loadPlayerContainer(player, useDB);
			if(container != null){
				playerData.put(player, container);
			}
		}
	}
	
	/**
	 * Gives the player a new PlayerContainer.
	 * It scans the player's data from the Race and Class container and
	 * calculates the needed values.
	 * 
	 * @param player to create
	 */
	public void addPlayer(RaCPlayer player){
		if(playerData.containsKey(player)) return;

		playerData.put(player, new PlayerContainer(player).checkStats());
	}
	
	
	/**
	 * Rescans the player and creates a new {@link PlayerContainer} if he has none.
	 *  
	 * @param player to check
	 */
	public void checkPlayer(RaCPlayer player){
		PlayerContainer hContainer = getCreate(player);
		hContainer.checkStats();
	}
	
	
	/**
	 * Checks if the ArrowManager of a Player exists and returns it.
	 * If the ArrowManager does not exist, it is created.
	 * 
	 * @param player to check
	 * @return the {@link ArrowManager} of the Player
	 */
	public ArrowManager getArrowManagerOfPlayer(RaCPlayer player) {
		return getCreate(player, true).getArrowManager();
	}
	
	/**
	 * Checks if the {@link ArmorToolManager} of a Player exists and returns it.
	 * If the {@link ArmorToolManager} does not exist, it is created.
	 * 
	 * @param player to check
	 * @return the {@link ArmorToolManager} of the Player
	 */
	public ArmorToolManager getArmorToolManagerOfPlayer(RaCPlayer player){
		return getCreate(player, true).getArmorToolManager();
	}
	
	/**
	 * Checks if the {@link PlayerRaCScoreboardManager} of a Player exists and returns it.
	 * If the {@link PlayerRaCScoreboardManager} does not exist, it is created.
	 * 
	 * @param player to check
	 * @return the {@link PlayerRaCScoreboardManager} of the Player
	 */
	public PlayerRaCScoreboardManager getScoreboardManager(RaCPlayer player){
		return getCreate(player, true).getPlayerScoreboardManager();
	}

	/**
	 * Forces an HP display output for the player.
	 * 
	 * @param player to force the output
	 * @return true, if it worked.
	 */
	public boolean displayHealth(RaCPlayer player) {
		getHealthManager(player).forceHPOut();
		return true;
	}

	/**
	 * Switches the God Mode of the player.
	 * 
	 * @param player to switch
	 * @return true if worked, false if player not found.
	 */
	public boolean switchGod(RaCPlayer playerUUID) {
		PlayerContainer container = playerData.get(playerUUID);
		if(container != null){
			container.switchGod();
			return true;
		}
		return false;
	}

	/**
	 * Returns the HealthManger of the Player
	 * 
	 * @return health stuff
	 */
	public HealthManager getHealthManager(RaCPlayer player){
		return getCreate(player).getHealthManager();
	}
	
	
	/**
	 * Gets the current PlayerContainer of the Player.
	 * If not found, a new one is created. 
	 * 
	 * @param player to check
	 * @return the found or new created container.
	 */
	private PlayerContainer getCreate(RaCPlayer player){
		return getCreate(player, true);
	}
	
	/**
	 * Gets the PlayerContainer of a Player.
	 * If the container is not found, a new one is created 
	 * if the flag (create) is set to true.
	 * 
	 * @param player
	 * @param create
	 * @return
	 */
	private PlayerContainer getCreate(RaCPlayer player, boolean create){
		PlayerContainer container = playerData.get(player);
		if(container == null && create){
			plugin.getRaceManager().loadIfNotExists(player);
			plugin.getClassManager().loadIfNotExists(player);
			
			container = new PlayerContainer(player);
			playerData.put(player, container);
			
			checkPlayer(player);
		}
		
		return container;
	}

	/**
	 * Returns if the Player has God mode on.
	 * 
	 * @param player to check
	 * @return true if has god on
	 */
	public boolean isGod(RaCPlayer player) {
		PlayerContainer containerOfPlayer = getCreate(player);
		return containerOfPlayer.isGod();
	}
	
	
	/**
	 * Gets the SpellManager of a player.
	 * @param player to get
	 * @return the SpellManager of the Player.
	 */
	public PlayerSpellManager getSpellManagerOfPlayer(RaCPlayer player){
		PlayerContainer containerOfPlayer = getCreate(player);
		return containerOfPlayer.getSpellManager();
	}
	
	
	/**
	 * Returns the PlayerLevelManager of the Player.
	 * 
	 * @param player
	 * @return
	 */
	public PlayerLevelManager getPlayerLevelManager(RaCPlayer player){
		PlayerContainer containerOfPlayer = getCreate(player);
		return containerOfPlayer.getPlayerLevelManager();
	}

	/**
	 * Returns the current size of the PlayerManager.
	 * 
	 * @return the size of the player manager.
	 */
	public String getPlayerNumber() {
		return playerData.size() + "";
	}

	/**
	 * Returns the PlayerPetManager.
	 * 
	 * @param raCPlayer to get for.
	 * @return the Player Pet manager.
	 */
	public PlayerPetManager getPlayerPetManager(RaCPlayer player) {
		PlayerContainer containerOfPlayer = getCreate(player);
		return containerOfPlayer.getPlayerPetManager();
	}

}
