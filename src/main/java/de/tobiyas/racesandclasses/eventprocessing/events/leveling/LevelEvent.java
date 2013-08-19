package de.tobiyas.racesandclasses.eventprocessing.events.leveling;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class LevelEvent extends Event {

	private final static HandlerList handlers = new HandlerList();
	
	
	/**
	 * The PlayerName this event is associated to.
	 */
	protected final String playerName;
	
	
	/**
	 * Creates a new Level Event. 
	 * Manditory is the Player that the event is associated to.
	 * 
	 * @param player the Event is associated to.
	 */
	public LevelEvent(String player) {
		this.playerName = player;
	}


	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	

	public static HandlerList getHandlerList() {
        return handlers;
    }



	/**
	 * @return the player name
	 */
	public String getPlayerName() {
		return playerName;
	}
	
	
}
