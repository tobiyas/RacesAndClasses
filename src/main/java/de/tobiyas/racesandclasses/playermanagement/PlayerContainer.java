package de.tobiyas.racesandclasses.playermanagement;

import java.io.File;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.worldresolver.WorldResolver;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.playermanagement.health.HealthDisplayRunner;
import de.tobiyas.racesandclasses.playermanagement.leveling.CustomPlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.MCPlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.util.config.YAMLConfigExtended;

public class PlayerContainer {
	
	/**
	 * The plugin to get the managers from.
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The Display to output the HP
	 */
	private HealthDisplayRunner display;
	
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
	private String playerName;
	
	
	/**
	 * The maximal Health of the player.
	 * 
	 * NOTICE: This value is calculated by the Plugin.
	 * It can vary from the real Value. Then another Plugin
	 * is overwriting this plugin.
	 */
	private double maxHealth;
	
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
	public PlayerContainer(String player){
		this.playerName = player;
		this.armorToolManager = new ArmorToolManager(player);
		this.arrowManager = new ArrowManager(player);
		
		this.hasGod = false;
		
		
		//choose level manager.
		if(plugin.getConfigManager().getGeneralConfig().isConfig_useRaCInbuildLevelSystem()){
			this.levelManager = new CustomPlayerLevelManager(player);			
		}else{
			this.levelManager = new MCPlayerLevelManager(player);
		}
		
		this.spellManager = new PlayerSpellManager(player);
		
		this.maxHealth = 20;
	}
	
	
	/**
	 * Creates the new Health container of a player.
	 * 
	 * NOTICE: The maxHealth value can be overwritten by the Constructor,
	 * since it is checking the stats of the player.
	 * 
	 * @param player to create to.
	 * @param maxHealth to create with.
	 */
	public PlayerContainer(String player, double maxHealth){
		this.hasGod = false;
		this.playerName = player;
		this.maxHealth = maxHealth;
		this.spellManager = new PlayerSpellManager(player);
		
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		MemberConfig memberConfig = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player);
		if(memberConfig != null){
			//this should never be null, since it is created if not existent
			display = new HealthDisplayRunner(memberConfig, this);
		}
		
		arrowManager = new ArrowManager(player);
		armorToolManager = new ArmorToolManager(player);
		levelManager = new CustomPlayerLevelManager(playerName);
	}
	
	
	/**
	 * Returns the calculated Max Health of  the player.
	 * @return
	 */
	public double getMaxHealth(){
		return maxHealth;
	}
	
	/**
	 * Sets the calculated maxHealth of a player.
	 * 
	 * NOTICE: It does not set this to the player!
	 * You need to call {@link #checkStats()} to verify
	 * everything is set correct.
	 * 
	 * @param maxHealth the parameter of the max Health.
	 */
	public void setMaxHealth(int maxHealth){
		this.maxHealth = maxHealth;
	}
	
	/**
	 * Saves the current state of the Container to the player data yml file
	 * 
	 * @param saveToDB if true it is saved to the Database. false to PlayerData yml file.
	 * @return true if worked, false if not
	 */
	public boolean save(boolean saveToDB){
		if(saveToDB){
			PlayerSavingContainer container = PlayerSavingContainer.generateNewContainer(playerName);
			
			container.setHasGod(hasGod);
			levelManager.saveTo(container);
			
			try{
				plugin.getDatabase().save(container);
				return true;
			}catch(PersistenceException exp){
				return false;
			}catch(Exception exp){
				plugin.getDebugLogger().logStackTrace(exp);
				return false;
			}
		}else{
			YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
			if(!config.isConfigurationSection("playerdata." + playerName))
				config.createSection("playerdata." + playerName);
			config.set("playerdata." + playerName + ".hasGod", hasGod);
			
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
	public static PlayerContainer constructFromDB(String player){		
		try{
			PlayerSavingContainer container = plugin.getDatabase().find(PlayerSavingContainer.class).where().ieq("playerName", player).findUnique();
			if(container == null) throw new PersistenceException("Not found.");
			
			PlayerContainer playerContainer = new PlayerContainer(player, 20).checkStats();
			
			playerContainer.levelManager.setCurrentLevel(container.getPlayerLevel());
			playerContainer.levelManager.setCurrentExpOfLevel(container.getPlayerLevelExp());
			
			if(container.isHasGod()){
				playerContainer.hasGod = true;
			}
			
			return playerContainer.checkStats();
		}catch(PersistenceException exp){
			PlayerContainer playerContainer = new PlayerContainer(player, 20);
			playerContainer.checkStats();
			return playerContainer;
		}
	}
	
	
	/**
	 * Loads the PlayerData from the passed source (db or yml file).
	 * 
	 * @param player to load
	 * @param fromDB true if to load from DB.
	 * @return the loaded PlayerContainer.
	 */
	public static PlayerContainer loadPlayerContainer(String player ,boolean fromDB){
		if(fromDB){
			return constructFromDB(player);
		}else{
			return constructContainerFromYML(player);
		}
	}
	
	
	/**
	 * Generates a Health Container from the Player Data YAML file.
	 * The player passed will be unfold and load.
	 * 
	 * @param player to load
	 * @return the container corresponding to the player.
	 */
	public static PlayerContainer constructContainerFromYML(String player){
		YAMLConfigExtended config = new YAMLConfigExtended(RacesAndClasses.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerdata.yml");
		config.load();
		
		boolean hasGod = config.getBoolean("playerdata." + player + ".hasGod");
		
		RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player);
		ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(player);
		
		double maxHealth = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		if(raceContainer != null){
			maxHealth = raceContainer.getRaceMaxHealth();
		}
			
		if(classContainer != null){
			maxHealth = classContainer.modifyToClass(maxHealth);
		}
			
		PlayerContainer container = new PlayerContainer(player, maxHealth).checkStats();
		if(hasGod){
			container.switchGod();
		}
		return container;
	}

	/**
	 * Checks the Player if he has any Wrong set values and resets the MaxHealth if needed.
	 */
	public PlayerContainer checkStats() {
		final Player player = Bukkit.getPlayer(playerName);
		boolean isOnDisabledWorld = WorldResolver.isOnDisabledWorld(player);
		boolean keepMaxHPOnDisabledWorld = plugin.getConfigManager().getGeneralConfig().isConfig_keep_max_hp_on_disabled_worlds();
		
		RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(playerName);
		ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(playerName);
		
		if(raceContainer == null || (isOnDisabledWorld && !keepMaxHPOnDisabledWorld)) {
			maxHealth = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		}else{
			double tempMaxHealth = raceContainer.getRaceMaxHealth();
			if(tempMaxHealth <= 0) return this;
			maxHealth = tempMaxHealth;
		}
		
		if(classContainer != null && keepMaxHPOnDisabledWorld){
			maxHealth = classContainer.modifyToClass(maxHealth);
		}
			
		arrowManager.rescanClass();
		armorToolManager.rescanPermission();
		armorToolManager.checkArmorNotValidEquiped();
		
		spellManager.rescan();
		levelManager.checkLevelChanged();
		
		if(player != null && player.isOnline()){			
			Player bukkitPlayer = Bukkit.getPlayer(playerName);

			double currentMaxHealth = CompatibilityModifier.BukkitPlayer.safeGetMaxHealth(bukkitPlayer);
			if(Math.abs(currentMaxHealth - maxHealth) >= 0.5){
				CompatibilityModifier.BukkitPlayer.safeSetMaxHealth(maxHealth, bukkitPlayer);
			}
						
			
		}
		
		return this;
	}
	
	/**
	 * Switches the God state of a player.
	 */
	public void switchGod(){
		hasGod = !hasGod;
		Player player = Bukkit.getPlayer(this.playerName);
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
	 * Forces to produce an HP message.
	 */
	public void forceHPOut(){
		Player playerObject = Bukkit.getPlayer(playerName);
		if(playerObject == null) return;
		checkStats();
		display.forceHPOut();
	}


	/**
	 * Returns the current Health of the Player
	 * Note: This is only an Delegation from the Player Object
	 * 
	 * @return
	 */
	public double getCurrentHealth() {
		Player player = Bukkit.getPlayer(playerName);
		if(player != null && player.isOnline()){
			double currentHealth = CompatibilityModifier.BukkitPlayer.safeGetHealth(player);
			return currentHealth;
		}
		
		return 0;
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
