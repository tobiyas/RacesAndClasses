package de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;

public class AfterRaceChangedEvent extends AfterRaceSelectedEvent {

	
	
	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * The race to change from
	 */
	private RaceContainer oldRace;
	
	
	/**
	 * Player changed his class from another class.
	 * 
	 * @param player that changed
	 * @param classToSelect new Class
	 * @param oldClass old Class
	 */
	public AfterRaceChangedEvent(Player player, RaceContainer raceToSelect, RaceContainer oldRace) {
		super(player, raceToSelect);
		
		this.oldRace = oldRace;
	}
	
	
	public RaceContainer getOldRace() {
		return oldRace;
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
