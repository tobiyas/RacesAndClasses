package de.tobiyas.racesandclasses.eventprocessing.events.holderevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public abstract class HolderSelectedEvent extends Event{

	private static HandlerList handlers = new HandlerList();
	
	
	/**
	 * The player selecting the class
	 */
	protected Player player;
	
	
	/**
	 * The holder the player has selected to be
	 */
	protected AbstractTraitHolder holderToSelect;

	/**
	 * If the player should get an Cooldown after change.
	 */
	protected final boolean giveCooldown;
	
	
	/**
	 * A player has selected a holder.
	 * 
	 * @param player that selected the holder
	 * @param holderToSelect that was selected
	 */
	public HolderSelectedEvent(Player player, AbstractTraitHolder holderToSelect) {
		this.player = player;
		this.holderToSelect = holderToSelect;
		
		this.giveCooldown = true;
	}
	
	/**
	 * A player has selected a holder.
	 * 
	 * @param player that selected the holder
	 * @param holderToSelect that was selected
	 * @param if the player should get an Cooldown
	 */
	public HolderSelectedEvent(Player player, AbstractTraitHolder holderToSelect, boolean giveCooldown) {
		this.player = player;
		this.holderToSelect = holderToSelect;
		
		this.giveCooldown = giveCooldown;
	}
	
	
	public Player getPlayer() {
		return player;
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


	/**
	 * Returns the new Holder the player selects
	 * 
	 * @return
	 */
	public AbstractTraitHolder getHolderToSelect() {
		return holderToSelect;
	}

	public boolean isGiveCooldown() {
		return giveCooldown;
	}
}
