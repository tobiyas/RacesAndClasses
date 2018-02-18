package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana;

import java.util.Map;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithCost;

public interface ManaManager {

	/**
	 * Rescans the Player and resets the Max Mana if needed.
	 */
	public abstract void rescanPlayer();
	
	/**
	 * A player tries to cast a spell.
	 * Returns true if the spell may be casted.
	 * Returns false if the player does not have enough Mana.
	 * 
	 * When Mana is used, the Mana is removed from the mana Pool.
	 * 
	 * @param spellToCast to cast
	 * @return true if spell cast is permitted, false otherwise.
	 */
	public abstract boolean playerCastSpell(TraitWithCost spellToCast);

	/**
	 * Checks if the Player has enough Mana for the Spell.
	 * 
	 * WARNING: Only returns true if the Spell actually needs Mana!
	 * 
	 * @param spell to check
	 * @return true if has enough Mana, false if not, or no Mana spell.
	 */
	public abstract boolean hasEnoughMana(TraitWithCost spell);

	/**
	 * Fills the current Mana Pool of the player by a specific Value.
	 * It returns the resulting current mana Pool.
	 * 
	 * If the Mana Pool exceeds it's max size, it is filled but not overfilled.
	 * 
	 * @param value to fill
	 * @return the Current Mana Pool size aftert filling.
	 */
	public abstract double fillMana(double value);

	/**
	 * Drowns the current Mana Pool of the player by a specific Value.
	 * It returns the resulting current mana Pool.
	 * 
	 * If the Mana Pool exceeds to be empty (0), it is emptied but will not be negative.
	 * 
	 * @param value to drown
	 * @return the Current Mana Pool size after drowning.
	 */
	public abstract double drownMana(double value);

	/**
	 * Checks if the Player has enough Mana.
	 * 
	 * @param manaNeeded the mana needed.
	 * @return true if has enough, false if not.
	 */
	public abstract boolean hasEnoughMana(double manaNeeded);

	/**
	 * @return the maxMana
	 */
	public abstract double getMaxMana();

	/**
	 * @return the currentMana
	 */
	public abstract double getCurrentMana();

	/**
	 * Adds a max Mana bonus.
	 * 
	 * @param key to add
	 * @param value to add
	 */
	public abstract void addMaxManaBonus(String key, double value);

	/**
	 * Remove a max Mana bonus.
	 * 
	 * @param key to remove
	 */
	public abstract void removeMaxManaBonus(String key);

	/**
	 * Returns all Bonusses.
	 * 
	 * @return all Bonuses.
	 */
	public abstract Map<String, Double> getAllBonuses();

	/**
	 * @return the player
	 */
	public abstract RaCPlayer getPlayer();

	/**
	 * Returns if the Mana of this player is
	 * fully filled.
	 * 
	 * @return true if filled, false otherwise.
	 */
	public abstract boolean isManaFull();
	
	/**
	 * Ticks the Manager.
	 */
	public abstract void tick();

	/**
	 * Gets the max Mana boost by the registered name.
	 * @param boostID to get.
	 * @return the boost or 0 if not present.
	 */
	public abstract double getManaBoostByName(String boostID);

}