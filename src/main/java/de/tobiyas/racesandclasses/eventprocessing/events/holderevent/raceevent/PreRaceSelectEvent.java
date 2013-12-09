package de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;

public class PreRaceSelectEvent extends HolderPreSelectEvent{

	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * A player has selected a race.
	 * 
	 * @param player that selected the class
	 * @param raceToSelect that was selected
	 */
	public PreRaceSelectEvent(Player player, RaceContainer raceToSelect) {
		super(player, raceToSelect);
	}
	
	/**
	 * A player has selected a race.
	 * 
	 * @param player that selected the class
	 * @param raceToSelect that was selected
	 * @param checkPermissions if the permissions should be checked
	 * @param checkCooldown if the Cooldown should be checked
	 */
	public PreRaceSelectEvent(Player player, RaceContainer raceToSelect, boolean checkPermissions, boolean checkCooldown) {
		super(player, raceToSelect, checkCooldown, checkPermissions);
	}
	
	
	public RaceContainer getRaceToSelect() {
		return (RaceContainer) holderToSelect;
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
