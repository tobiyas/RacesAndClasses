package de.tobiyas.racesandclasses.listeners.holderchangegui;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;

public class ClassChangeListenerGui extends HolderChangeListenerGui {

	
	public ClassChangeListenerGui(){
		super(plugin.getClassManager());
	}

	
	@Override
	protected HolderPreSelectEvent generateHolderSelectEvent(String playerName,
			AbstractTraitHolder newHolder) {
		return new PreClassSelectEvent(Bukkit.getPlayer(playerName), (ClassContainer) newHolder);
	}

	
	@Override
	protected HolderPreSelectEvent generateHolderChangeEvent(String playerName,
			AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		return new PreClassChangeEvent(Bukkit.getPlayer(playerName), (ClassContainer) newHolder, (ClassContainer) oldHolder);
	}
	
}
