package de.tobiyas.racesandclasses.eventprocessing.events.quickslot;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class QuickSlotEvent extends PlayerEvent implements Cancellable{


	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * The SlotNumber pressed
	 */
	private final int slotNumber;
	
	/**
	 * Tells if it is canceled.
	 */
	private boolean isCanceled;
	
	
	public QuickSlotEvent(Player who, int slotNumber) {
		super(who);
		this.slotNumber = slotNumber;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return isCanceled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.isCanceled = cancel;
	}

	
	/**
	 * The SlotNumber pressed
	 */
	public int getSlotNumber(){
		return slotNumber;
	}
}
