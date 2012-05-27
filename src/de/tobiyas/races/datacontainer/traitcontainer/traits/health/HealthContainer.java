package de.tobiyas.races.datacontainer.traitcontainer.traits.health;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.util.economy.defaults.YAMLConfigExtended;

public class HealthContainer {
	
	private String player;
	
	private double currentHealth;
	private int maxHealth;
	
	public HealthContainer(String player, double currentHealth, int maxHealth){
		this.player = player;
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
	}
	
	public void reduceLife(double amount){
		currentHealth -= amount;
		Player player = Bukkit.getPlayer(this.player);
				
		if(currentHealth < 0) 
			player.setHealth(0);
		else
			player.setHealth((int) (currentHealth/maxHealth) * 20);
	}
	
	public void increaseLife(double amount){
		currentHealth += amount;
		if(currentHealth > maxHealth) currentHealth = maxHealth;
	}
	
	public boolean save(){
		YAMLConfigExtended config = new YAMLConfigExtended(Races.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerData.yml");
		config.load();
		config.createSection("playerdata." + player);
		config.set("playerdata." + player + ".currentHealth", currentHealth);
		
		return config.save();
	}
	
	public static HealthContainer constructContainerFromYML(String player){
		YAMLConfigExtended config = new YAMLConfigExtended(Races.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerData.yml");
		config.load();
		
		double currentHealth = config.getDouble("playerdata." + player + "currentHealth");
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player);
		
		int maxHealth = Races.getPlugin().interactConfig().getconfig_defaultHealth();
		if(container != null)
			maxHealth = container.getRaceMaxHealth();
		
		return new HealthContainer(player, currentHealth, maxHealth);
	}

}
