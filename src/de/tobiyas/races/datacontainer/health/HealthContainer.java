package de.tobiyas.races.datacontainer.health;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.Races;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.races.configuration.member.MemberConfig;
import de.tobiyas.races.configuration.member.MemberConfigManager;
import de.tobiyas.races.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.races.datacontainer.arrow.ArrowManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.util.consts.Consts;

public class HealthContainer {
	
	private HealthDisplay display;
	private ArrowManager arrowManager;
	private ArmorToolManager armorToolManager;
	private String player;
	
	private double currentHealth;
	private double maxHealth;
	private boolean hasGod;
	
	public HealthContainer(String player, double currentHealth, double maxHealth){
		this.hasGod = false;
		this.player = player;
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
		
		if(currentHealth > maxHealth) currentHealth = maxHealth;
		
		Player onlinePlayer = Bukkit.getPlayer(player);
		if(onlinePlayer != null)
			setPlayerPercentage();
		
		MemberConfig memberConfig = MemberConfigManager.getInstance().getConfigOfPlayer(player);
		if(memberConfig != null){
			display = new HealthDisplay(memberConfig, this);
		}
		
		arrowManager = new ArrowManager(player);
		armorToolManager = new ArmorToolManager(player);
		checkStats();
	}
	
	public void reduceLife(double amount, DamageCause cause){
		Player player = Bukkit.getPlayer(this.player);		
		if(player == null) return;
		
		if(player.getGameMode() == GameMode.CREATIVE)
			return;
		
		if(hasGod)
			return;
		
		double modifiedDamage = armorToolManager.calcDamageToArmor(amount, cause);
		currentHealth -= modifiedDamage; //TODO TEST!
		
		if(currentHealth < 0){
			if(player.isDead()) return;
			player.setHealth(0);
		}else{
			setPlayerPercentage();
			player.playEffect(EntityEffect.HURT);
		}
	}
	
	public void setPlayerPercentage(){
		Player player = Bukkit.getPlayer(this.player);
		if(player == null) return;
		
		if(currentHealth > maxHealth) currentHealth = maxHealth;
		if(currentHealth < 0) currentHealth = 0;
		
		int transferredValue = (int) Math.ceil((currentHealth/maxHealth) * 20);
		if(transferredValue == 20 && (currentHealth != maxHealth))
			transferredValue = 19;
		
		player.setHealth(transferredValue);
	}
	
	public void increaseLife(double amount){
		currentHealth += amount;
		if(currentHealth > maxHealth) currentHealth = maxHealth;
		setPlayerPercentage();
	}
	
	public double getCurrentHealth(){
		return currentHealth;
	}
	
	public double getMaxHealth(){
		return maxHealth;
	}
	
	public void setMaxHealth(int maxHealth){
		this.maxHealth = maxHealth;
		setPlayerPercentage();
	}
	
	public boolean save(){
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML);
		config.load();
		if(!config.isConfigurationSection("playerdata." + player))
			config.createSection("playerdata." + player);
		config.set("playerdata." + player + ".currentHealth", currentHealth);
		config.set("playerdata." + player + ".hasGod", hasGod);
		
		return config.save();
	}
	
	public static HealthContainer constructContainerFromYML(String player){
		YAMLConfigExtended config = new YAMLConfigExtended(Races.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerdata.yml");
		config.load();
		
		double currentHealth = config.getDouble("playerdata." + player + ".currentHealth", 20);
		boolean hasGod = config.getBoolean("playerdata." + player + ".hasGod");
		
		RaceContainer raceContainer = RaceManager.getManager().getRaceOfPlayer(player);
		ClassContainer classContainer = ClassManager.getInstance().getClassOfPlayer(player);
		
		double maxHealth = Races.getPlugin().getGeneralConfig().getconfig_defaultHealth();
		if(raceContainer != null)
			maxHealth = raceContainer.getRaceMaxHealth();
		
		if(classContainer != null)
			maxHealth = classContainer.modifyToClass(maxHealth);
		
		HealthContainer container = new HealthContainer(player, currentHealth, maxHealth);
		if(hasGod)
			container.switchGod();
		return container;
	}

	public void fullHeal() {
		currentHealth = maxHealth;
		setPlayerPercentage();
	}

	public void checkStats() {
		RaceContainer raceContainer = RaceManager.getManager().getRaceOfPlayer(player);
		ClassContainer classContainer = ClassManager.getInstance().getClassOfPlayer(player);
		
		if(raceContainer == null) 
			maxHealth = Races.getPlugin().getGeneralConfig().getconfig_defaultHealth();
		else{
			int tempMaxHealth = raceContainer.getRaceMaxHealth();
			if(tempMaxHealth <= 0) return;
			maxHealth = tempMaxHealth;
		}
		
		if(classContainer != null)
			maxHealth = classContainer.modifyToClass(maxHealth);
		
		arrowManager.rescanClass();
		armorToolManager.rescanPermission();
		setPlayerPercentage();
	}
	
	public void switchGod(){
		hasGod = !hasGod;
		Player player = Bukkit.getPlayer(this.player);
		if(player != null)
			if(hasGod)
				player.sendMessage(ChatColor.GREEN + "God mode toggled.");
			else
				player.sendMessage(ChatColor.RED + "God mode removed.");
	}
	
	public ArrowManager getArrowManager(){
		return arrowManager;
	}
	
	public ArmorToolManager getArmorToolManager(){
		return armorToolManager;
	}
	
	public void forceHPOut(){
		Player playerObject = Bukkit.getPlayer(player);
		if(playerObject == null) return;
		display.forceHPOut(playerObject);
	}

}
