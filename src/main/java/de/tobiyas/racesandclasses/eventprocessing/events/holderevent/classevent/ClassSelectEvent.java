package de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectEvent;

/**
 * This Event is fired, when a Player tries to select a Class. 
 * 
 * @author Tobiyas
 */
public class ClassSelectEvent extends HolderSelectEvent{

	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	
	/**
	 * A player has selected a class.
	 * 
	 * @param player that selected the class
	 * @param classToSelect that was selected
	 */
	public ClassSelectEvent(Player player, ClassContainer classToSelect) {
		super(player,  classToSelect);
	}


	public ClassContainer getClassToSelect() {
		return (ClassContainer) holderToSelect;
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
