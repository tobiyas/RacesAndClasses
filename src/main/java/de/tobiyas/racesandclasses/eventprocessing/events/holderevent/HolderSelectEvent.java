package de.tobiyas.racesandclasses.eventprocessing.events.holderevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public abstract class HolderSelectEvent extends Event implements Cancellable{

	/**
	 * The player selecting the class
	 */
	protected Player player;
	
	
	/**
	 * The holder the player has selected to be
	 */
	protected AbstractTraitHolder holderToSelect;
	
	
	/**
	 * Tells if the event is canceled or not
	 */
	protected boolean isCancelled;
	
	
	/**
	 * The message that is sent when canceling. 
	 * <br>Needs to be set when canceled.
	 */
	protected String cancelMessage;
	
	
	/**
	 * A player has selected a holder.
	 * 
	 * @param player that selected the holder
	 * @param holderToSelect that was selected
	 */
	public HolderSelectEvent(Player player, AbstractTraitHolder holderToSelect) {
		this.player = player;
		this.holderToSelect = holderToSelect;
		
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



	public String getCancelMessage() {
		return cancelMessage;
	}
}
