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
import de.tobiyas.racesandclasses.pets.PlayerPetManager;
import de.tobiyas.racesandclasses.playermanagement.display.scoreboard.PlayerRaCScoreboardManager;
import de.tobiyas.racesandclasses.playermanagement.health.HealthManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.CustomPlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.HeroesLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.MCPlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.McMMOLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.SkillAPILevelManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.skilltree.PlayerSkillTreeManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.racesandclasses.saving.PlayerSavingManager;

public class PlayerContainer {
	
	/**
	 * The plugin to get the managers from.
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();

	/**
	 * The ArrowManager of the Player.
	 */
	private final ArrowManager arrowManager;
	
	/**
	 * The Armor and Tool Manager of the Player
	 */
	private final ArmorToolManager armorToolManager;
	
	/**
	 * The PetManager of the player.
	 */
	private final PlayerPetManager petManager;
	
	/**
	 * The Name of the Player associated with this Container
	 */
	private final RaCPlayer player;
	
	/**
	 * The health manager to use.
	 */
	protected final HealthManager healthManager;
	
	/**
	 * The Spell Manager managing the Spells of the Player.
	 */
	private final PlayerSpellManager spellManager;
	
	
	/**
	 * The Level Manager of the Player.
	 */
	private final PlayerLevelManager levelManager;
	
	/**
	 * The Scoreboard manager for Scoreboards.
	 */
	private final PlayerRaCScoreboardManager playerScoreboardManager;
	
	/**
	 * The Skill-Tree manager for the Player.
	 */
	private final PlayerSkillTreeManager playerSkillTreeManager;
	
	/**
	 * The saving Container to use.
	 */
	private final PlayerSavingData savingContainer;
	
	
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
		
		//Loads the Saving Container.
		this.savingContainer = PlayerSavingManager.get().getPlayerData(player.getUniqueId());
		this.armorToolManager = new ArmorToolManager(player);
		this.arrowManager = new ArrowManager(player);
		this.healthManager = new HealthManager(player);
		this.playerScoreboardManager = new PlayerRaCScoreboardManager(player);
		this.petManager = new PlayerPetManager(player);
		this.playerSkillTreeManager = new PlayerSkillTreeManager(player, savingContainer);
		
		//choose level manager.
		switch(plugin.getConfigManager().getGeneralConfig().getConfig_useLevelSystem()){
			case RacesAndClasses : this.levelManager = new CustomPlayerLevelManager(player, savingContainer); break;
			case VanillaMC : this.levelManager = new MCPlayerLevelManager(player); break;
			case SkillAPI : this.levelManager = new SkillAPILevelManager(player); break;
			case mcMMO : this.levelManager = new McMMOLevelManager(player); break;
			case Heroes : this.levelManager = new HeroesLevelManager(player); break;
			
			//if none found (should not happen) the RaC level manager is used.
			default: this.levelManager = new CustomPlayerLevelManager(player, savingContainer);
		}
		
		this.spellManager = new PlayerSpellManager(player);		
	}
	
	
	/**
	 * Inits all managers.
	 */
	public void init(){
		rescan();
	}
	
	public void rescan(){
		if(player == null || !player.isOnline()) return;
		
		arrowManager.rescanPlayer();
		armorToolManager.rescanPermission();
		armorToolManager.checkArmorNotValidEquiped();
		healthManager.rescanPlayer();
		
		spellManager.rescan();
		levelManager.checkLevelChanged();
		
		player.getPlayer().setMetadata("LEVEL", new FixedMetadataValue(plugin, levelManager.getCurrentLevel()));
	}
	
	
	/**
	 * This ticks the Containe once per second.
	 */
	public void tick(){
		levelManager.tick();
		petManager.tick();
	}

	
	
	/**
	 * Returns the calculated Max Health of  the player.
	 * @return
	 */
	public HealthManager getHealthManager(){
		return healthManager;
	}
	
	
	/**
	 * Switches the God state of a player.
	 */
	public void switchGod(){
		savingContainer.setGodMode(!savingContainer.isGodModeEnabled());
		
		if(player != null && player.isOnline()){
			if(savingContainer.isGodModeEnabled()){
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
		return this.savingContainer.isGodModeEnabled();
	}
	
	/**
	 * Gets the Saving Container.
	 * @return the saving Container.
	 */
	public PlayerSavingData getSavingData(){
		return this.savingContainer;
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
	 * Returns the Score-board Manager for the Player.
	 * 
	 * @return score-board Manager.
	 */
	public PlayerRaCScoreboardManager getPlayerScoreboardManager() {
		return playerScoreboardManager;
	}

	/**
	 * Returns the LevelManager of the Player.
	 * 
	 * @return
	 */
	public PlayerLevelManager getPlayerLevelManager() {
		return this.levelManager;
	}


	/**
	 * Returns the PetManager of this player.
	 */
	public PlayerPetManager getPlayerPetManager() {
		return this.petManager;
	}
	
	/**
	 * Clears everything in the Containers.
	 */
	public void shutdown(){
		petManager.despawnAndClear();
	}


	/**
	 * Returns the SkillTree Manager
	 */
	public PlayerSkillTreeManager getSkillTreeManager() {
		return playerSkillTreeManager;
	}
	
}
