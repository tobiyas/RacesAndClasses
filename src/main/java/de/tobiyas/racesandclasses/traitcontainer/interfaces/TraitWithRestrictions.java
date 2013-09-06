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
	 * Checks the Restrictions of the Trait
	 * for a player passed.
	 * 
	 * @param player
	 * @return
	 */
	public boolean checkRestrictions(Player player);	
}
