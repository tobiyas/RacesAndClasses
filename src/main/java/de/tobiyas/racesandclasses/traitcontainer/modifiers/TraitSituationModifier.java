package de.tobiyas.racesandclasses.traitcontainer.modifiers;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public interface TraitSituationModifier {

	
	/**
	 * If the Trait can be applied to the Player.
	 * 
	 * @param player to check if it is appliable.
	 * @param toModify the parameter to modify.
	 * 
	 * @return true if applyable.
	 */
	public boolean canBeApplied(String toModify, RaCPlayer player);
	
	
	/**
	 * Applies the Value to the Modifier.
	 * 
	 * @param value to modify.
	 * 
	 * @return the modified Value.
	 */
	public double apply(RaCPlayer player, double value);
	
	
	/**
	 * Applies the Value to the Modifier.
	 * 
	 * @param value to modify.
	 * 
	 * @return the modified Value.
	 */
	public int apply(RaCPlayer player, int value);
}
