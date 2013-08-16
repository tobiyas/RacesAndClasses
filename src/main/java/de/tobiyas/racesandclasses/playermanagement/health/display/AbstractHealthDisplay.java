package de.tobiyas.racesandclasses.playermanagement.health.display;

import org.bukkit.ChatColor;

public abstract class AbstractHealthDisplay implements HealthDisplay{

	protected String playerName;
	
	/**
	 * Inits with a player
	 * 
	 * @param player to display to
	 */
	public AbstractHealthDisplay(String playerName) {
		this.playerName = playerName;
	}
	
	
	@Override
	public abstract void display(double currentHealth, double maxHealth);
	
	
	/**
	 * Gets the Color of the percentage
	 * Green for > 60%
	 * <br>Yellow for < 60%, > 30%
	 * <br>Red for < 30%
	 * 
	 * @param currentHealth the current Health of a player
	 * @param maxHealth the maximal health of a player
	 * @return the color the percentage has
	 */
	protected ChatColor getColorOfPercent(double currentHealth, double maxHealth){
		double percentage = currentHealth / maxHealth;
		
		if(percentage > 0.6) return ChatColor.GREEN;
		if(percentage >= 0.3) return ChatColor.YELLOW;
		if(percentage < 0.3) return ChatColor.RED;
		
		return ChatColor.LIGHT_PURPLE;
	}
	
	
	/**
	 * Calculates a bar with the passed health + the bar length
	 * 
	 * @param currentHealth
	 * @param maxHealth
	 * @return
	 */
	protected String calcForHealth(double currentHealth, double maxHealth, int healthBarLength){
		double healthPresentBarLength = ((currentHealth / maxHealth) * (healthBarLength));
		
		if(healthPresentBarLength < 0) healthPresentBarLength = 0;
		if(healthPresentBarLength > healthBarLength) healthPresentBarLength = healthBarLength;
		
		String healthLeft = ChatColor.GREEN + "";
		for(int i = 0; i < healthPresentBarLength; i++){
			healthLeft += "|";
			healthBarLength --;
		}
		
		String healthRest = ChatColor.RED + "";
		for(; healthBarLength > 0; healthBarLength--){
			healthRest += "|";
		}
		
		if(currentHealth == maxHealth){
			healthRest += ChatColor.GREEN + "|";
		}else{
			healthRest += ChatColor.RED + "|";
		}
		
		
		String chatString = healthLeft + healthRest;
		return chatString;
	}

}
