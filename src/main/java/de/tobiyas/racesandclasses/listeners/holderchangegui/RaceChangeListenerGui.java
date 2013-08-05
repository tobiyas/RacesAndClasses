package de.tobiyas.racesandclasses.listeners.holderchangegui;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.RaceChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.RaceSelectEvent;

public class RaceChangeListenerGui extends HolderChangeListenerGui {

	public RaceChangeListenerGui() {
		super(plugin.getRaceManager());
	}

	
	@Override
	protected HolderSelectEvent generateHolderSelectEvent(String playerName,
			AbstractTraitHolder newHolder) {
		return new RaceSelectEvent(Bukkit.getPlayer(playerName), (RaceContainer) newHolder);
	}

	
	@Override
	protected HolderSelectEvent generateHolderChangeEvent(String playerName,
			AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		return new RaceChangeEvent(Bukkit.getPlayer(playerName), (RaceContainer) newHolder, (RaceContainer) oldHolder);
	}

}
