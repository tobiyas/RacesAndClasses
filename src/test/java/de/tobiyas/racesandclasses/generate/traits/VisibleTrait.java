package de.tobiyas.racesandclasses.generate.traits;

import java.util.Map;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitInfos;

public class VisibleTrait implements Trait{

	private AbstractTraitHolder holder;
	

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

	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder) {
		this.holder = abstractTraitHolder;
	}

	@Override
	public AbstractTraitHolder getTraitHolder() {
		return holder;
	}

}
