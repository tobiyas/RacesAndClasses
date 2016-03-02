package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

import org.bukkit.Material;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public interface TraitWithCost {

	/**
	 * Returns the cost of the Spell.
	 * The Cost can vary on which Cost Type used.
	 * 
	 * @param the player to check for.
	 * 
	 * @return the costs of the spell
	 */
	double getCost(RaCPlayer player);

	/**
	 * Returns the {@link CostType} of the Spell.
	 * To see the different type of costs, see {@link CostType}
	 * 
	 * @return the CostType of the Spell.
	 * 
	 * @see CostType
	 */
	CostType getCostType();

	/**
	 * returns the Material Type of CostType.
	 * <br> Returns null if no Material costType is needed
	 * 
	 * @return the material for casting.
	 */
	Material getCastMaterialType();
	
	
	/**
	 * returns the Material damage for casting.
	 * @return the material damage value for casting.
	 */
	byte getCastMaterialDamage();
	
	/**
	 * returns the name of the casting material.
	 * @return the material name for casting.
	 */
	String getCastMaterialName();
	
	/**
	 * triggered when the spell should be triggered, but no CostType is present.
	 * 
	 * @param event that triggered
	 * @param player that triggered the spell
	 */
	void triggerButDoesNotHaveEnoghCostType(EventWrapper wrapper);
	
	
	/**
	 * Returns true if a CostCheck is needed for this event.
	 * 
	 * @param event the Event to check
	 * 
	 * @return true if costCheck needed, false otherwise.
	 */
	boolean needsCostCheck(EventWrapper wrapper);

}