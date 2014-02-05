package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

/**
 * This Interface marks if the Trait has restrictions to
 * needing materials / health / anything else to be possible to trigger.
 * 
 * @author tobiyas
 */
public interface ConsumingTrait {

	/**
	 * Checks if the player has enough of the wanted stuff to trigger
	 * 
	 * @return true if has, false if not.
	 */
	public boolean hasEnoughToTrigger();
}
