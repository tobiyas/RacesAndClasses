package de.tobiyas.racesandclasses.traitcontainer.interfaces;

import java.util.Set;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public interface TraitWithRestrictions {

	/**
	 * The Config-Path to the Min Level to use
	 */
	public static final String MIN_LEVEL_PATH = "minLevel";
	
	/**
	 * The Config-Path to the Max Level to use
	 */
	public static final String MAX_LEVEL_PATH = "maxLevel";
	
	/**
	 * The Biomes to use.
	 */
	public static final String BIOME_PATH = "biomes";

	/**
	 * Trait works only in water
	 */
	public static final String ONLY_IN_WATER_PATH = "onlyInWater";

	/**
	 * Trait works only on Land
	 */
	public static final String ONLY_ON_LAND_PATH = "onlyOnLand";
	
	
	
	/**
	 * Returns the minimum Level required with the 
	 * 
	 * @return
	 */
	public int getMinimumLevel();
	
	
	/**
	 * Returns the maximum level this trait can be used with.
	 * 
	 * @return
	 */
	public int getMaximumLevel();
	
	
	/**
	 * Returns all Biomes the Trait can be used on.
	 * 
	 * @return the Biomes to be used on
	 */
	public Set<Biome> getBiomeRestrictions();


	/**
	 * Returns if the Trait only triggers in the water
	 * 
	 * @return true if works only in water
	 */
	public boolean isOnlyInWater();

	
	/**
	 * Returns if the Trait only triggers on land
	 * 
	 * @return true if works only on land
	 */
	public boolean isOnlyOnLand();

	/**
	 * Checks the Restrictions of the Trait
	 * for a player passed.
	 * 
	 * @param player
	 * @return
	 */
	public boolean checkRestrictions(Player player);
	
	
}
