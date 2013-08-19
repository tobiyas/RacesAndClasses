package de.tobiyas.racesandclasses.eventprocessing.events.leveling;

import org.bukkit.event.HandlerList;

public class LevelDownEvent extends LevelEvent {

private final static HandlerList handlers = new HandlerList();
	
	
	/**
	 * The level the player started
	 */
	protected int fromLevel;
	
	
	/**
	 * The Level the Player is now
	 */
	protected int toLevel;
	
	
	/**
	 * Indicates the Player has done a Level down.
	 * 
	 * @param player that leveled.
	 * @param fromLevel the Level the Player was before
	 * @param toLevel the Level the Player is afterwards.
	 */
	public LevelDownEvent(String player, int fromLevel, int toLevel) {
		super(player);
		
		this.fromLevel = fromLevel;
		this.toLevel = toLevel;
	}


	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	

	public static HandlerList getHandlerList() {
        return handlers;
    }



	/**
	 * @return the fromLevel
	 */
	public int getFromLevel() {
		return fromLevel;
	}



	/**
	 * @return the toLevel
	 */
	public int getToLevel() {
		return toLevel;
	}
	
	

}
