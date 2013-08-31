package de.tobiyas.racesandclasses.playermanagement.health;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.playermanagement.PlayerContainer;
import de.tobiyas.racesandclasses.playermanagement.health.display.ChatHealthBar;
import de.tobiyas.racesandclasses.playermanagement.health.display.HealthDisplay;
import de.tobiyas.racesandclasses.playermanagement.health.display.HealthDisplay.DisplayType;
import de.tobiyas.racesandclasses.playermanagement.health.display.ScoreBoardHealthBar;

public class HealthDisplayRunner implements Runnable {
	
	private MemberConfig config;
	private PlayerContainer healthContainer;
	private double oldValue;
	private int oldInterval;
	
	private HealthDisplay display;
	
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
		
		Object healthDisplayTypeAsObject = config.getValueDisplayName("healthDisplayType");
		String healthDisplayType = "score";
		if(healthDisplayTypeAsObject != null && healthDisplayTypeAsObject instanceof String){
			healthDisplayType = (String) healthDisplayTypeAsObject;
		}
		
		DisplayType type = DisplayType.resolve(healthDisplayType);
		
		display = generateFromType(type, config.getName());
	}
	
	
	private HealthDisplay generateFromType(DisplayType type, String name){
		switch (type) {
		case Chat:
			return new ChatHealthBar(name);
		
		case Scoreboard:
			return new ScoreBoardHealthBar(name);

		//case Spout: //TODO maybe add.
			//return new SpoutHealthBar(name);
			
		default:
			return new ChatHealthBar(name);
		}
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
