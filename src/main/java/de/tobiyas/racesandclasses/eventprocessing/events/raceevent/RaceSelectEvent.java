package de.tobiyas.racesandclasses.eventprocessing.events.raceevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;

public class RaceSelectEvent extends Event implements Cancellable{

	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	
	/**
	 * The player selecting the class
	 */
	private Player player;
	
	
	/**
	 * The race the player has selected to be
	 */
	private RaceContainer raceToSelect;
	
	
	/**
	 * Tells if the event is canceled or not
	 */
	private boolean isCancelled;
	
	
	/**
	 * The message that is sent when canceling. 
	 * <br>Needs to be set when canceled.
	 */
	private String cancelMessage;
	
	
	/**
	 * A player has selected a race.
	 * 
	 * @param player that selected the class
	 * @param raceToSelect that was selected
	 */
	public RaceSelectEvent(Player player, RaceContainer raceToSelect) {
		this.player = player;
		this.raceToSelect = raceToSelect;
		
		this.isCancelled = false;
		this.cancelMessage = "";
	}
	
	
	
	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}


	/**
	 * @deprecated use {@link #setCancelled(String)} instead.
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;
	}
	
	
	/**
	 * Cancels the Event + sets a message why it is cancelled.
	 * 
	 * @param message
	 */
	public void setCancelled(String message){
		this.isCancelled = true;
		this.cancelMessage = message;		
	}
	
	
	public Player getPlayer() {
		return player;
	}



	public RaceContainer getRaceToSelect() {
		return raceToSelect;
	}



	public String getCancelMessage() {
		return cancelMessage;
	}



	/**
	 * needed for Bukkit to get the list of Handlers interested
	 * @return
	 */
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
