package de.tobiyas.racesandclasses.healthmanagement;

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
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class HealthContainer {
	
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	private HealthDisplayRunner display;
	private ArrowManager arrowManager;
	private ArmorToolManager armorToolManager;
	private String playerName;
	
	private double maxHealth;
	
	private boolean hasGod;
	
	public HealthContainer(String player, double maxHealth){
		this.hasGod = false;
		this.playerName = player;
		this.maxHealth = maxHealth;
		
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		MemberConfig memberConfig = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player);
		if(memberConfig != null){
			display = new HealthDisplayRunner(memberConfig, this);
		}		
		
		arrowManager = new ArrowManager(player);
		armorToolManager = new ArmorToolManager(player);
		checkStats();
	}
	
	
	public double getMaxHealth(){
		return maxHealth;
	}
	
	public void setMaxHealth(int maxHealth){
		this.maxHealth = maxHealth;
	}
	
	public boolean save(){
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML);
		config.load();
		if(!config.isConfigurationSection("playerdata." + playerName))
			config.createSection("playerdata." + playerName);
		config.set("playerdata." + playerName + ".hasGod", hasGod);
		
		return config.save();
	}
	
	public static HealthContainer constructContainerFromYML(String player){
		YAMLConfigExtended config = new YAMLConfigExtended(RacesAndClasses.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerdata.yml");
		config.load();
		
		boolean hasGod = config.getBoolean("playerdata." + player + ".hasGod");
		
		RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player);
		ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(player);
		
		double maxHealth = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		if(raceContainer != null)
			maxHealth = raceContainer.getRaceMaxHealth();
		
		if(classContainer != null)
			maxHealth = classContainer.modifyToClass(maxHealth);
		
		HealthContainer container = new HealthContainer(player, maxHealth);
		if(hasGod)
			container.switchGod();
		return container;
	}

	public void checkStats() {
		RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(playerName);
		ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(playerName);
		
		if(raceContainer == null) {
			maxHealth = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		}else{
			int tempMaxHealth = raceContainer.getRaceMaxHealth();
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
	}
	
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
	
	public ArrowManager getArrowManager(){
		return arrowManager;
	}
	
	public ArmorToolManager getArmorToolManager(){
		return armorToolManager;
	}
	
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
	

}
