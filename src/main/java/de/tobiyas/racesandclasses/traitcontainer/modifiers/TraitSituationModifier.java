package de.tobiyas.racesandclasses.traitcontainer.modifiers;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

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
	 * @param player to modify for.
	 * @param value to modify.
	 * @param trait that this is called from
	 * 
	 * @return the modified Value.
	 */
	public double apply(RaCPlayer player, double value, Trait trait);
	
	
	/**
	 * Applies the Value to the Modifier.
	 * 
	 * @param player to modify for.
	 * @param value to modify.
	 * @param trait that this is called from
	 * 
	 * @return the modified Value.
	 */
	public int apply(RaCPlayer player, int value, Trait trait);
}
