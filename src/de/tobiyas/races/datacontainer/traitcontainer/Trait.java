package de.tobiyas.races.datacontainer.traitcontainer;

import org.bukkit.event.Event;

public interface Trait{
	
	public String getName();
	
	public Object getValue();
	
	public void setValue(Object obj);
	
	public ReturnFilter modify(Event event);
}
