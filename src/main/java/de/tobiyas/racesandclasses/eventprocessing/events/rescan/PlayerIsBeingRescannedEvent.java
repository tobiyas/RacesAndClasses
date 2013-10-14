package de.tobiyas.racesandclasses.eventprocessing.events.rescan;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * This event is fired when a Player is being rescanned.
 * <br>This means this is the correct time to set all Values new.
 * <br>
 * <br>NOTICE: This event is an Indicator and can not be canceled!
 * <br>NOTICE2: All Health + Modifications for the Main Plugin are already set. 
 * So Spells, armor, tools, etc are set correct already.
 * This is only an Event for Traits to be called for an Reset of their Stuff.
 * 
 * @author Tobiyas
 *
 */
public class PlayerIsBeingRescannedEvent extends PlayerEvent {

	
	private final static HandlerList handlers = new HandlerList();
	
	
	/**
	 * Create a {@link PlayerIsBeingRescannedEvent}.
	 * <br>It indicates that an Rescan of the Player has been Indicated
	 *  
	 * @param player the player that is rescanned
	 */
	public PlayerIsBeingRescannedEvent(Player player) {
		super(player);
	}

	
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	

	public static HandlerList getHandlerList() {
        return handlers;
    }


}
