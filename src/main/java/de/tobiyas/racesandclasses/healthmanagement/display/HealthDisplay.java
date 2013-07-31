package de.tobiyas.racesandclasses.healthmanagement.display;


public interface HealthDisplay {
	
	
	/**
	 * Displays the passed Health to the player
	 * 
	 * @param currentHealth the health of the player
	 * @param maxHealth the maximal health the player can have
	 */
	public void display(double currentHealth, double maxHealth);
}
