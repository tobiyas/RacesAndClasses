package de.tobiyas.races.datacontainer.traitcontainer.traits;

import org.bukkit.event.Event;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;

public interface Trait{
	
	@interface TraitInfo{
		int traitPriority() default 3;
		Class<?>[] registerdClasses() default {};
	};
	public void generalInit();
	
	public String getName();
	
	public RaceContainer getRace();
	
	public ClassContainer getClazz();
	
	public Object getValue();
	
	public String getValueString();
	
	public void setValue(Object obj);
	
	public boolean modify(Event event);
	
	public boolean isVisible();
}
