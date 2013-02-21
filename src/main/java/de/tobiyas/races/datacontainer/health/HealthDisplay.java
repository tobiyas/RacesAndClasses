package de.tobiyas.races.datacontainer.health;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.member.MemberConfig;
import de.tobiyas.races.util.consts.Consts;

public class HealthDisplay implements Runnable {
	
	private MemberConfig config;
	private HealthContainer healthContainer;
	private double oldValue;
	private int oldInterval;
	
	private int scedulerTask;
	
	public HealthDisplay(MemberConfig config, HealthContainer healthContainer){
		if(config == null) return;
		this.config = config;
		this.healthContainer = healthContainer;
		this.oldValue = healthContainer.getCurrentHealth();
		
		oldInterval = config.getLifeDisplayInterval();
		scedulerTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Races.getPlugin(), this, oldInterval, oldInterval);
	}

	@Override
	public void run() {
		checkInterval();
		if(config.getEnableLifeDisplay()){
			String playerName = config.getName();
			Player player = Bukkit.getPlayer(playerName);
			if(player != null && healthContainer != null && 
				oldValue != healthContainer.getCurrentHealth()){
				display(player);
			}
		}
	}
	
	private void display(Player player){
		double newValue = healthContainer.getCurrentHealth();
		double maxHealth = healthContainer.getMaxHealth();
		
		double healthAbs = ((newValue / maxHealth) * (Consts.healthBarLength));
		if(healthAbs < 0) healthAbs = 0;
		if(healthAbs > maxHealth) healthAbs = maxHealth;
		
		String healthLeft = ChatColor.GREEN + "";
		int totalLength = Consts.healthBarLength;
		for(int i = 0; i < healthAbs; i++){
			healthLeft += "|";
			totalLength --;
		}
		
		String healthRest = ChatColor.RED + "";
		for(; totalLength > 0; totalLength--)
			healthRest += "|";
		
		if(newValue == maxHealth)
			healthRest += ChatColor.GREEN + "|";
		else
			healthRest += ChatColor.RED + "|";
		
		int pre = (int) Math.floor(newValue);
		int after = (int) Math.floor(newValue * 100D) % 100;
		player.sendMessage(ChatColor.YELLOW + "Health: " + healthLeft + healthRest + 
				ChatColor.YELLOW + " " + getColorOfPercent(newValue, maxHealth) + 
				pre + "." + after + ChatColor.YELLOW + "/" + ChatColor.GREEN + (double) maxHealth);
		oldValue = newValue;
	}
	
	private void checkInterval(){
		int checkInterval = config.getLifeDisplayInterval();
		if(checkInterval == oldInterval) return;
		
		if(checkInterval < 20){
			checkInterval = 20;
			config.setLifeDisplayIntervall(20);
		}
		oldInterval = checkInterval;
		
		Bukkit.getScheduler().cancelTask(scedulerTask);
		scedulerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Races.getPlugin(), this, oldInterval, oldInterval);
	}
	
	private ChatColor getColorOfPercent(double currentHealth, double maxHealth){
		double percentage = currentHealth / maxHealth;
		
		if(percentage > 0.6)
			return ChatColor.GREEN;
		if(percentage >= 0.3)
			return ChatColor.YELLOW;
		if(percentage < 0.3)
			return ChatColor.RED;
		
		return ChatColor.LIGHT_PURPLE;
	}

	public void forceHPOut(Player player) {
		display(player);
	}

}
