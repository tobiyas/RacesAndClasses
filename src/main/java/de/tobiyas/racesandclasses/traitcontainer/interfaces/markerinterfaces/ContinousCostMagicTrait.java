package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

import org.bukkit.entity.Player;

public interface ContinousCostMagicTrait {

	/**
	 * Returns the time every how many seconds the trait is checked.
	 * 
	 * @return every how many seconds the trait drains the stuff.
	 */
	public int everyXSeconds();
	
	
	/**
	 * Activates the Trait.
	 * 
	 * @param player the player to activate to
	 * @return true if activated, false otherwise.
	 */
	public boolean activate(Player player);
	
	
	/**
	 * Deactivates the Trait.
	 * 
	 * @param player the player to deactivate
	 * @return true if deactivated, false otherwise.
	 */
	public boolean deactivate(Player player);
	
	
	/**
	 * Return true if the Trait is activated.
	 * 
	 * @param player the player to check if active
	 * @return false if not.
	 */
	public boolean isActivated(Player player);
}
