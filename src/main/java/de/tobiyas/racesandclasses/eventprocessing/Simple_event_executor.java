package de.tobiyas.racesandclasses.eventprocessing;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class Simple_event_executor implements EventExecutor {

	
	@Override
	public void execute(Listener listener, Event event) throws EventException {
		TraitEventManager.fireEvent(event);
	}

}
