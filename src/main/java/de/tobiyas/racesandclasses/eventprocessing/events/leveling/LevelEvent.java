package de.tobiyas.racesandclasses.eventprocessing.events.leveling;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class LevelEvent extends PlayerEvent {

	private final static HandlerList handlers = new HandlerList();
	
	private final String playerName;
	
	/**
	 * Creates a new Level Event. 
	 * Manditory is the Player that the event is associated to.
	 * 
	 * @param player the Event is associated to.
	 */	
	public LevelEvent(String playerName){
		super(Bukkit.getPlayer(playerName));
		this.playerName = playerName;
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
