package de.tobiyas.racesandclasses.playermanagement.health;

import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.playermanagement.PlayerContainer;
import de.tobiyas.racesandclasses.playermanagement.display.Display;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.DisplayGenerator;

public class HealthDisplayRunner implements Runnable, Observer {
	
	private MemberConfig config;
	private PlayerContainer healthContainer;
	private double oldValue;
	private int oldInterval;
	
	private Display display;
	
	private int scedulerTask;
	
	/**
	 * Inits the HealthDisplaytRunner that shows the Health.
	 * 
	 * @param config to load options from
	 * @param healthContainer to contact with.
	 */
	public HealthDisplayRunner(MemberConfig config, PlayerContainer healthContainer){
		if(config == null) return;
		this.config = config;
		this.healthContainer = healthContainer;
		this.oldValue = healthContainer.getCurrentHealth();
		
		oldInterval = config.getLifeDisplayInterval();
		scedulerTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(RacesAndClasses.getPlugin(), this, oldInterval, oldInterval);
		
		rescanDisplay();
		config.addObserver(this);
	}
	
	
	/**
	 * This re-registers the display.
	 * <br>Meaning to throw the old one away and generate a new one.
	 */
	private void rescanDisplay(){
		if(display != null){
			display.unregister();
		}
		
		display = DisplayGenerator.generateDisplay(config.getName(), DisplayInfos.HEALTH);
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


	@Override
	public void update(Observable o, Object arg) {
		String changedValue = (String) arg;
		
		if(changedValue.equalsIgnoreCase("displayType")){
			rescanDisplay();
		}
	}

}
