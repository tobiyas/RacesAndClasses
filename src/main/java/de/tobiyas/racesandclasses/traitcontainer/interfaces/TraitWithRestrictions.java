package de.tobiyas.racesandclasses.traitcontainer.interfaces;

import java.util.Set;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

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
	 * Trait works only in Lava
	 */
	public static final String ONLY_IN_LAVA_PATH = "onlyInLava";
	
	/**
	 * Trait works only in Lava
	 */
	public static final String ONLY_ON_SNOW = "onlyOnSnow";	
	
	/**
	 * Trait has a Cooldown
	 */
	public static final String COOLDOWN_TIME_PATH = "cooldown";
	
	/**
	 * Trait works only on Day.
	 */
	public static final String ONLY_ON_DAY_PATH = "onlyOnDay";
	
	/**
	 * Trait only works in the Night.
	 */
	public static final String ONLY_IN_NIGHT_PATH = "onlyInNight";
	
	/**
	 * The Path to the Display Name
	 */
	public static final String DISPLAY_NAME_PATH = "displayName";
	
	
	
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
	 * @param player to check
	 * @param event that called the trigger
	 * 
	 * @return true if restrictions met, false otherwise
	 */
	public boolean checkRestrictions(Player player, Event event);
	
	/**
	 * Returns the total uplink time in seconds
	 * 
	 * @return the time in seconds
	 */
	public int getMaxUplinkTime();
	
	
	/**
	 * This is triggered when the User has uplink on the Skill,
	 * But would be triggered.
	 * <br> Returning TRUE does NOT paste the Uplink message!
	 * 
	 * @param event the event that would be triggered
	 * @return true to NOT display uplink message!
	 */
	public boolean triggerButHasUplink(Event event);
	
	
	/**
	 * Returns if the Trait should notify it's user everytime on trigger for the Uplink.
	 * 
	 * @return true if should, false if not.
	 */
	public boolean notifyTriggeredUplinkTime();
	
}
