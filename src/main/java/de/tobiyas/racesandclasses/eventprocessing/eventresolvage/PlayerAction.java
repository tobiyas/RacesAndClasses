package de.tobiyas.racesandclasses.eventprocessing.eventresolvage;

public enum PlayerAction {

	/**
	 * player interacts with nothing
	 */
	INTERACT_AIR,
	
	/**
	 * Player is interacting with block
	 */
	INTERACT_BLOCK,
	
	/**
	 * Player is interacting with entity
	 */
	INTERACT_ENTITY,
	
	/**
	 * Player hits a block
	 */
	HIT_BLOCK,

	/**
	 * Player hits in Air.
	 */
	HIT_AIR,
	
	/**
	 * The Player has moved.
	 * <br>This includes teleportation
	 */
	PLAYER_MOVED,
	
	/**
	 * Player is getting targeted
	 */
	PLAYER_TARGETED,
	
	/**
	 * Player is taking damage
	 */
	TAKE_DAMAGE,
	
	/**
	 * The Player is dealing damage
	 */
	DO_DAMAGE,
	
	/**
	 * Player is casting a spell
	 */
	CAST_SPELL,
	
	/**
	 * The Player is changing his spell
	 */
	CHANGE_SPELL, 
	
	/**
	 * No action realized...
	 */
	UNKNOWN, 

}
