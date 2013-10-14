package de.tobiyas.racesandclasses.eventprocessing.events.mana;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ManaRegenerationEvent extends PlayerEvent implements Cancellable {

	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * The amount to regenerate
	 */
	private double amount;
	
	/**
	 * Tells if it is canceled.
	 */
	private boolean isCanceled;
	
	
	public ManaRegenerationEvent(Player player, double amount) {
		super(player);
		
		this.amount = amount;
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

	public double getAmount(){
		return amount;
	}
	
	public void setAmount(double newAmount){
		this.amount = newAmount;
	}
}
