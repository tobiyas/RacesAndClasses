package de.tobiyas.racesandclasses.eventprocessing.events.stun;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerStunnedEvent extends PlayerEvent implements Cancellable {


	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	
	/**
	 * The Time in ticks.
	 */
	private int timeInTicks;
	
	/**
	 * If the event is canceled.
	 */
	private boolean canceled;
	
	
	public PlayerStunnedEvent(Player stunned, int timeInTicks) {
		super(stunned);
		this.timeInTicks = timeInTicks;
		this.canceled = false;
	}

	public int getTimeInTicks() {
		return timeInTicks;
	}

	public void setTimeInTicks(int timeInTicks) {
		this.timeInTicks = timeInTicks;
	}

	@Override
	public boolean isCancelled() {
		return canceled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.canceled = cancel;
	}
	
	
}
