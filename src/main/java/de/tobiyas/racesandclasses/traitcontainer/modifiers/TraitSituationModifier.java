package de.tobiyas.racesandclasses.traitcontainer.modifiers;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;

public interface TraitSituationModifier {

	
	/**
	 * If the Trait can be applied to the Player.
	 * 
	 * @param player to check if it is appliable.
	 * 
	 * @return true if applyable.
	 */
	public boolean canBeApplied(RaCPlayer player);
	
	
	/**
	 * Applies the Value to the Modifier.
	 * 
	 * @param value to modify.
	 * 
	 * @return the modified Value.
	 */
	public double apply(double value);
	
	
	/**
	 * Applies the Value to the Modifier.
	 * 
	 * @param value to modify.
	 * 
	 * @return the modified Value.
	 */
	public int apply(int value);
}
