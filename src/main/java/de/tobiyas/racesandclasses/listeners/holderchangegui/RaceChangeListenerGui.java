package de.tobiyas.racesandclasses.listeners.holderchangegui;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;

public class RaceChangeListenerGui extends HolderChangeListenerGui {

	public RaceChangeListenerGui() {
		super(plugin.getRaceManager());
	}

	
	@Override
	protected HolderPreSelectEvent generateHolderSelectEvent(String playerName,
			AbstractTraitHolder newHolder) {
		return new PreRaceSelectEvent(Bukkit.getPlayer(playerName), (RaceContainer) newHolder);
	}

	
	@Override
	protected HolderPreSelectEvent generateHolderChangeEvent(String playerName,
			AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		return new PreRaceChangeEvent(Bukkit.getPlayer(playerName), (RaceContainer) newHolder, (RaceContainer) oldHolder);
	}

}
