package de.tobiyas.racesandclasses.healthmanagement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.MemberConfig;
import de.tobiyas.racesandclasses.healthmanagement.display.ChatHealthBar;
import de.tobiyas.racesandclasses.healthmanagement.display.HealthDisplay;

public class HealthDisplayRunner implements Runnable {
	
	private MemberConfig config;
	private HealthContainer healthContainer;
	private double oldValue;
	private int oldInterval;
	
	private HealthDisplay display;
	
	private int scedulerTask;
	
	public HealthDisplayRunner(MemberConfig config, HealthContainer healthContainer){
		if(config == null) return;
		this.config = config;
		this.healthContainer = healthContainer;
		this.oldValue = healthContainer.getCurrentHealth();
		
		oldInterval = config.getLifeDisplayInterval();
		scedulerTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(RacesAndClasses.getPlugin(), this, oldInterval, oldInterval);
		
		display = new ChatHealthBar(config.getName()); //TODO Use different
	}

	@Override
	public void run() {
		checkInterval();
		if(config.getEnableLifeDisplay()){
			String playerName = config.getName();
			Player player = Bukkit.getPlayer(playerName);
			if(player != null && healthContainer != null && 
				oldValue != healthContainer.getCurrentHealth()){
					display();
			}
		}
	}
	
	private void display(){
		double currentHealth = healthContainer.getCurrentHealth();
		double maxHealth = healthContainer.getMaxHealth();
		
		display.display(currentHealth, maxHealth);
		
		oldValue = currentHealth;
	}
	
	private void checkInterval(){
		int checkInterval = config.getLifeDisplayInterval();
		if(checkInterval == oldInterval) return;
		
		if(checkInterval < 20){
			checkInterval = 20;
			config.setValue(MemberConfig.displayInterval, 20);
		}
		
		oldInterval = checkInterval;
		
		Bukkit.getScheduler().cancelTask(scedulerTask);
		scedulerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(RacesAndClasses.getPlugin(), this, oldInterval, oldInterval);
	}


	
	public void forceHPOut() {
		display();
	}

}
