package de.tobiyas.races.datacontainer.traitcontainer;

import org.bukkit.event.Event;

import de.tobiyas.races.datacontainer.race.RaceContainer;

public interface Trait{
	
	public String getName();
	
	public RaceContainer getRace();
	
	public Object getValue();
	
	public String getValueString();
	
	public void setValue(Object obj);
	
	public boolean modify(Event event);
	
	public boolean isVisible();
}
