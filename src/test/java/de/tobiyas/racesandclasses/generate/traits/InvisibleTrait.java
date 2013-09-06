package de.tobiyas.racesandclasses.generate.traits;

import java.util.Map;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitInfos;

public class InvisibleTrait extends AbstractBasicTrait{

	public InvisibleTrait() {
	}

	@TraitInfos(category = "useless", traitName = "InvisibleTrait", visible = false)
	@Override
	public void importTrait() {

	}

	@TraitEventsUsed(registerdClasses = {}, traitPriority = 1)
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "InvisibleTrait";
	}

	@Override
	public String getPrettyConfiguration() {
		return "Nothing";
	}

	@TraitConfigurationNeeded(neededFields = {})
	@Override
	public void setConfiguration(Map<String, String> configMap) {
	}

	@Override
	public boolean modify(Event event) {
		return false;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}

}
