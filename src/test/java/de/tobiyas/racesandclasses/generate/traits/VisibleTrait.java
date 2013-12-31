package de.tobiyas.racesandclasses.generate.traits;

import java.util.Map;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class VisibleTrait extends AbstractBasicTrait{


	@TraitInfos(category = "test", traitName = "VisibleTrait", visible = true)
	@Override
	public void importTrait() {
		
	}

	@TraitEventsUsed(registerdClasses = {}, traitPriority = 0)
	@Override
	public void generalInit() {
		
	}

	@Override
	public String getName() {
		return "VisibleTrait";
	}

	@Override
	public String getPrettyConfiguration() {
		return "Nothing";
	}

	@TraitConfigurationNeeded()
	@Override
	public void setConfiguration(Map<String, Object> configMap) {
	}

	@Override
	public boolean trigger(Event event) {
		return false;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}

	@Override
	public boolean canBeTriggered(Event event) {
		return true;
	}
}
