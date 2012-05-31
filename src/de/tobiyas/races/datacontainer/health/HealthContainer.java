package de.tobiyas.races.datacontainer.health;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.configuration.member.MemberConfig;
import de.tobiyas.races.configuration.member.MemberConfigManager;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;

public class HealthContainer {
	
	@SuppressWarnings("unused")
	private HealthDisplay display;
	private String player;
	
	private double currentHealth;
	private int maxHealth;
	
	private long lastDamage;
	
	public HealthContainer(String player, double currentHealth, int maxHealth){
		this.player = player;
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
		
		if(currentHealth > maxHealth) currentHealth = maxHealth;
		lastDamage = System.currentTimeMillis();
		
		Player onlinePlayer = Bukkit.getPlayer(player);
		if(onlinePlayer != null)
			setPlayerPercentage();
		
		MemberConfig memberConfig = MemberConfigManager.getInstance().getConfigOfPlayer(player);
		if(memberConfig != null){
			display = new HealthDisplay(memberConfig, this);
		}
	}
	
	public void reduceLife(double amount){
		if((System.currentTimeMillis() - lastDamage) < Races.getPlugin().interactConfig().getconfig_imunBetweenDamage())
			return;
		
		currentHealth -= amount;
		Player player = Bukkit.getPlayer(this.player);
				
		if(currentHealth < 0) 
			player.setHealth(0);
		else
			setPlayerPercentage();
		
		lastDamage = System.currentTimeMillis();
	}
	
	public void setPlayerPercentage(){
		Player player = Bukkit.getPlayer(this.player);
		if(player == null) return;
		if(currentHealth > maxHealth) currentHealth = maxHealth;
		player.setHealth((int) ((currentHealth/maxHealth) * 20));
	}
	
	public void increaseLife(double amount){
		currentHealth += amount;
		if(currentHealth > maxHealth) currentHealth = maxHealth;
		setPlayerPercentage();
	}
	
	public double getCurrentHealth(){
		return currentHealth;
	}
	
	public int getMaxHealth(){
		return maxHealth;
	}
	
	public void setMaxHealth(int maxHealth){
		this.maxHealth = maxHealth;
		setPlayerPercentage();
	}
	
	public boolean save(){
		YAMLConfigExtended config = new YAMLConfigExtended(Races.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerdata.yml");
		config.load();
		if(!config.isConfigurationSection("playerdata." + player))
			config.createSection("playerdata." + player);
		config.set("playerdata." + player + ".currentHealth", currentHealth);
		
		return config.save();
	}
	
	public static HealthContainer constructContainerFromYML(String player){
		YAMLConfigExtended config = new YAMLConfigExtended(Races.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerdata.yml");
		config.load();
		
		double currentHealth = config.getDouble("playerdata." + player + ".currentHealth");
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player);
		
		int maxHealth = Races.getPlugin().interactConfig().getconfig_defaultHealth();
		if(container != null)
			maxHealth = container.getRaceMaxHealth();
		
		return new HealthContainer(player, currentHealth, maxHealth);
	}

	public void fullHeal() {
		currentHealth = maxHealth;
		setPlayerPercentage();
	}

	public void checkStats() {
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player);
		if(container == null) 
			maxHealth = Races.getPlugin().interactConfig().getconfig_defaultHealth();
		else{
			int tempMaxHealth = container.getRaceMaxHealth();
			if(tempMaxHealth <= 0) return;
			maxHealth = tempMaxHealth;
		}
		
		setPlayerPercentage();
	}

}
