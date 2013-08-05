package de.tobiyas.racesandclasses.listeners.holderchangegui;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.ClassChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.ClassSelectEvent;

public class ClassChangeListenerGui extends HolderChangeListenerGui {

	
	public ClassChangeListenerGui(){
		super(plugin.getClassManager());
	}

	
	@Override
	protected HolderSelectEvent generateHolderSelectEvent(String playerName,
			AbstractTraitHolder newHolder) {
		return new ClassSelectEvent(Bukkit.getPlayer(playerName), (ClassContainer) newHolder);
	}

	
	@Override
	protected HolderSelectEvent generateHolderChangeEvent(String playerName,
			AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		return new ClassChangeEvent(Bukkit.getPlayer(playerName), (ClassContainer) newHolder, (ClassContainer) oldHolder);
	}
	
}
