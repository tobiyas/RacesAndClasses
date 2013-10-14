package de.tobiyas.racesandclasses.eventprocessing.events.holderevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

/**
 * This Event is fired when the Holder tries to select / change his holder.
 * <br>If it is not cancled, it works.
 * 
 * @author Tobiyas
 *
 */
public class HolderPreSelectEvent extends HolderSelectEvent implements Cancellable{

	private static HandlerList handlers = new HandlerList();
	
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
	 * 
	 * @param player
	 * @param holderToSelect
	 */
	public HolderPreSelectEvent(Player player,
			AbstractTraitHolder holderToSelect) {
		super(player, holderToSelect);
		
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
