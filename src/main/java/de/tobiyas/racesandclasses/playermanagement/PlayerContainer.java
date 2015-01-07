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

import org.bukkit.ChatColor;
import org.bukkit.metadata.FixedMetadataValue;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.playermanagement.health.HealthManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.CustomPlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.HeroesLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.MCPlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.McMMOLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.SkillAPILevelManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.util.config.YAMLConfigExtended;

public class PlayerContainer {
	
	/**
	 * The plugin to get the managers from.
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();

	
	/**
	 * The ArrowManager of the Player.
	 */
	private ArrowManager arrowManager;
	
	/**
	 * The Armor and Tool Manager of the Player
	 */
	private ArmorToolManager armorToolManager;
	
	/**
	 * The Name of the Player associated with this Container
	 */
	private RaCPlayer player;
	
	/**
	 * The health manager to use.
	 */
	protected HealthManager healthManager;
	
	/**
	 * Shows if the Player has God mode.
	 */
	private boolean hasGod;
	
	
	/**
	 * The Spell Manager managing the Spells of the Player.
	 */
	private final PlayerSpellManager spellManager;
	
	
	/**
	 * The Level Manager of the Player.
	 */
	private final PlayerLevelManager levelManager;
	
	
	/**
	 * This constructor only sets the most important stuff.
	 * It is thought for converting and restoring from DB.
	 * 
	 * THIS DOES NO RESCANNING!!! All is set to default! No extra stuff.
	 * 
	 * @param player
	 */
	public PlayerContainer(RaCPlayer player){
		this.player = player;
		this.armorToolManager = new ArmorToolManager(player);
		this.arrowManager = new ArrowManager(player);
		this.healthManager = new HealthManager(player);
		
		this.hasGod = false;
		
		
		//choose level manager.
		switch(plugin.getConfigManager().getGeneralConfig().getConfig_useLevelSystem()){
			case RacesAndClasses : this.levelManager = new CustomPlayerLevelManager(player); break;
			case VanillaMC : this.levelManager = new MCPlayerLevelManager(player); break;
			case SkillAPI : this.levelManager = new SkillAPILevelManager(player); break;
			case mcMMO : this.levelManager = new McMMOLevelManager(player); break;
			case Heroes : this.levelManager = new HeroesLevelManager(player); break;
			
			//if none found (should not happen) the RaC level manager is used.
			default: this.levelManager = new CustomPlayerLevelManager(player);
		}
		
		//make sure we reload the file.
		this.levelManager.reloadFromYaml();
		
		this.spellManager = new PlayerSpellManager(player);		
	}
	
	
	/**
	 * This ticks the Containe once per second.
	 */
	public void tick(){
		levelManager.tick();
	}

	
	
	/**
	 * Returns the calculated Max Health of  the player.
	 * @return
	 */
	public HealthManager getHealthManager(){
		return healthManager;
	}

	
	/**
	 * Saves the current state of the Container to the player data yml file
	 * 
	 * @param saveToDB if true it is saved to the Database. false to PlayerData yml file.
	 * @return true if worked, false if not
	 */
	public boolean save(boolean saveToDB){
		if(saveToDB){
//			PlayerSavingContainer container = PlayerSavingContainer.generateNewContainer(player);
//			
//			container.setHasGod(hasGod);
//			levelManager.saveTo(container);
//			
//			try{
//				plugin.getDatabase().save(container);
//				return true;
//			}catch(PersistenceException exp){
//				return false;
//			}catch(Exception exp){
//				plugin.getDebugLogger().logStackTrace(exp);
//				return false;
//			}
			return false;
		}else{
			YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
			config.set("hasGod", hasGod);
			levelManager.save();
			
			levelManager.save();
			return true;			
		}
		
		
	}
	
	
	/**
	 * Constructs a {@link PlayerContainer} from the DB.
	 * If no entry in the DB is found, a new one is created.
	 * 
	 * @param player
	 * @return
	 */
//	public static PlayerContainer constructFromDB(RaCPlayer player){
//		try{
//			PlayerSavingContainer container = plugin.getDatabase().find(PlayerSavingContainer.class).where().ieq("player", player.toString()).findUnique();
//			if(container == null) throw new PersistenceException("Not found.");
//			
//			PlayerContainer playerContainer = new PlayerContainer(player).checkStats();
//			
//			playerContainer.levelManager.setCurrentLevel(container.getPlayerLevel());
//			playerContainer.levelManager.setCurrentExpOfLevel(container.getPlayerLevelExp());
//			
//			if(container.isHasGod()){
//				playerContainer.hasGod = true;
//			}
//			
//			return playerContainer.checkStats();
//		}catch(PersistenceException exp){
//			PlayerContainer playerContainer = new PlayerContainer(player);
//			playerContainer.checkStats();
//			return playerContainer;
//		}
//	}
	
	
	/**
	 * Loads the PlayerData from the passed source (db or yml file).
	 * 
	 * @param player to load
	 * @param fromDB true if to load from DB.
	 * @return the loaded PlayerContainer.
	 */
	public static PlayerContainer loadPlayerContainer(RaCPlayer player ,boolean fromDB){
//		if(fromDB){
//			return constructFromDB(player);
//		}else{
			return constructContainerFromYML(player);
//		}
	}
	
	
	/**
	 * Generates a Health Container from the Player Data YAML file.
	 * The player passed will be unfold and load.
	 * 
	 * @param player to load
	 * @return the container corresponding to the player.
	 */
	public static PlayerContainer constructContainerFromYML(RaCPlayer player){
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		config.load();
		
		
		//set race when not set yet.
		String savedRace = config.getString("race", null);
		boolean raceEnabled = plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces();
		if(raceEnabled && savedRace != null && !savedRace.equals("") 
				&& plugin.getRaceManager().getHolderOfPlayer(player) == plugin.getRaceManager().getDefaultHolder()){
			plugin.getRaceManager().changePlayerHolder(player, savedRace, false);
		}

		
		//set class when not set
		String savedClass = config.getString("class", null);
		boolean classEnabled = plugin.getConfigManager().getGeneralConfig().isConfig_classes_enable();
		if(classEnabled && savedClass != null && !savedClass.equals("") 
				&& plugin.getClassManager().getHolderOfPlayer(player) == plugin.getClassManager().getDefaultHolder()){
			plugin.getClassManager().changePlayerHolder(player, savedClass, false);
		}
			
		PlayerContainer container = new PlayerContainer(player).checkStats();
		boolean hasGod = config.getBoolean("hasGod");
		if(hasGod){
			container.switchGod();
		}
		return container;
	}

	/**
	 * Checks the Player if he has any Wrong set values and resets the MaxHealth if needed.
	 */
	public PlayerContainer checkStats() {
		if(player == null || !player.isOnline()) return null;
		
		arrowManager.rescanClass();
		armorToolManager.rescanPermission();
		armorToolManager.checkArmorNotValidEquiped();
		healthManager.rescanPlayer();
		
		spellManager.rescan();
		levelManager.checkLevelChanged();
		
		player.getPlayer().setMetadata("LEVEL", new FixedMetadataValue(plugin, levelManager.getCurrentLevel()));
		
		return this;
	}
	
	/**
	 * Switches the God state of a player.
	 */
	public void switchGod(){
		hasGod = !hasGod;
		
		if(player != null && player.isOnline()){
			if(hasGod){
				player.sendMessage(ChatColor.GREEN + "God mode toggled.");
			}else{
				player.sendMessage(ChatColor.RED + "God mode removed.");
			}
		}
	}
	
	/**
	 * Returns the Arrow Manager
	 * @return
	 */
	public ArrowManager getArrowManager(){
		return arrowManager;
	}
	
	/**
	 * Returns the Armor and Tool Permissions Manager
	 * @return
	 */
	public ArmorToolManager getArmorToolManager(){
		return armorToolManager;
	}

	/**
	 * Returns if the player has god mode
	 * 
	 * @return
	 */
	public boolean isGod() {
		return this.hasGod;
	}


	/**
	 * Returns the Spell Manager of the Player.
	 * 
	 * @return
	 */
	public PlayerSpellManager getSpellManager() {
		return this.spellManager;
	}


	/**
	 * Returns the LevelManager of the Player.
	 * 
	 * @return
	 */
	public PlayerLevelManager getPlayerLevelManager() {
		return this.levelManager;
	}
	
}
