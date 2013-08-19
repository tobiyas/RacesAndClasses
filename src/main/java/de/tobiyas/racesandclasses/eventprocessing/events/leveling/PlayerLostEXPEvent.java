package de.tobiyas.racesandclasses.eventprocessing.events.leveling;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerLostEXPEvent extends LevelEvent implements Cancellable{

private final static HandlerList handlers = new HandlerList();
	
	
	/**
	 * indicates if the Event is canceled
	 */
	private boolean cancled = false;
	
	
	/**
	 * The EXP lost
	 */
	private int exp;
	
	
	/**
	 * Creates the Event with the Player loses the EXP
	 * and the EXP lost.
	 * 
	 * @param player
	 * @param exp
	 */
	public PlayerLostEXPEvent(String player, int exp) {
		super(player);
		
		this.exp = exp;
	}


	/**
	 * @return the exp
	 */
	public int getExp() {
		return exp;
	}


	/**
	 * @param exp the exp to set
	 */
	public void setExp(int exp) {
		this.exp = exp;
	}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	

	public static HandlerList getHandlerList() {
        return handlers;
    }


	@Override
	public boolean isCancelled() {
		return cancled;
	}


	@Override
	public void setCancelled(boolean cancel) {
		this.cancled = cancel;
	}

}
