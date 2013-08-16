package de.tobiyas.racesandclasses.playermanagement;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.MemberConfig;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.playermanagement.health.HealthDisplayRunner;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.consts.Consts;
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
		checkStats();
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
	 * @return true if worked, false if not
	 */
	public boolean save(){
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML);
		config.load();
		if(!config.isConfigurationSection("playerdata." + playerName))
			config.createSection("playerdata." + playerName);
		config.set("playerdata." + playerName + ".hasGod", hasGod);
		
		return config.save();
	}
	
	
	/**
	 * Generates a Health Container from the Player Data YAML file.
	 * The player passed will be unfold and load.
	 * 
	 * @param player to load
	 * @return the container corresponding to the palyer.
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
			
		PlayerContainer container = new PlayerContainer(player, maxHealth);
		if(hasGod){
			container.switchGod();
		}
		return container;
	}

	/**
	 * Checks the Player if he has any Wrong set values and resets the MaxHealth if needed.
	 */
	public void checkStats() {
		RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(playerName);
		ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(playerName);
		
		if(raceContainer == null) {
			maxHealth = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		}else{
			double tempMaxHealth = raceContainer.getRaceMaxHealth();
			if(tempMaxHealth <= 0) return;
			maxHealth = tempMaxHealth;
		}
		
		if(classContainer != null){
			maxHealth = classContainer.modifyToClass(maxHealth);
		}
			
		arrowManager.rescanClass();
		armorToolManager.rescanPermission();
		armorToolManager.checkArmorNotValidEquiped();
		
		Player player = Bukkit.getPlayer(playerName);
		if(player != null && player.isOnline()){
			Player bukkitPlayer = Bukkit.getPlayer(playerName);
			
			double currentMaxHealth = CompatibilityModifier.BukkitPlayer.safeGetMaxHealth(bukkitPlayer);
			if(Math.abs(currentMaxHealth - maxHealth) > 0.5){
				CompatibilityModifier.BukkitPlayer.safeSetMaxHealth(maxHealth, bukkitPlayer);
			}
		}
		
		spellManager.rescan();
	}
	
	/**
	 * Switches the God state of a player.
	 */
	public void switchGod(){
		hasGod = !hasGod;
		Player player = Bukkit.getPlayer(this.playerName);
		if(player != null){
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
	

}
