package de.tobiyas.racesandclasses.eventprocessing.events.classevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;

public class ClassSelectEvent extends Event implements Cancellable{

	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	
	/**
	 * The player selecting the class
	 */
	private Player player;
	
	
	/**
	 * The class the player has selected to be
	 */
	private ClassContainer classToSelect;
	
	
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
	 * A player has selected a class.
	 * 
	 * @param player that selected the class
	 * @param classToSelect that was selected
	 */
	public ClassSelectEvent(Player player, ClassContainer classToSelect) {
		this.player = player;
		this.classToSelect = classToSelect;
		
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



	public ClassContainer getClassToSelect() {
		return classToSelect;
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
