package de.tobiyas.races.datacontainer.traitcontainer.traits;

import org.bukkit.event.Event;

import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;

public interface Trait{
	
	public void generalInit();
	
	public String getName();
	
	public RaceContainer getRace();
	
	public Object getValue();
	
	public String getValueString();
	
	public void setValue(Object obj);
	
	public boolean modify(Event event);
	
	public boolean isVisible();
}