package de.tobiyas.racesandclasses.generate.traits;

import java.util.Map;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

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
	protected String getPrettyConfigIntern() {
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
