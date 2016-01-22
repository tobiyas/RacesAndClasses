package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;

public interface TraitJoinLeave {

	/**
	 * The Player joins the Trait.
	 * <br>This is called when a player gets online and the Player 
	 * joins the TraitHolder holding this trait.
	 * @param player that joined
	 */
	public void playerJoines(RaCPlayer player);

	/**
	 * The Player leaves the Trait.
	 * <br>This is called when a player gets offline and the Player 
	 * leaves the TraitHolder holding this trait.
	 * @param player that joined
	 */
	public void playerLeaves(RaCPlayer player);
}
