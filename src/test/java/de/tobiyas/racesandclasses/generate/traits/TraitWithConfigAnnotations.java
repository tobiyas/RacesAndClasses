package de.tobiyas.racesandclasses.generate.traits;

import java.util.Map;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class TraitWithConfigAnnotations extends AbstractBasicTrait {

	

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "optional", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "needed", classToExpect = Integer.class, optional = false)
	})
	@Override
	public void setConfiguration(Map<String, Object> configurationMap)
			throws TraitConfigurationFailedException {
		
		if(!configurationMap.containsKey("needed")){
			throw new TraitConfigurationFailedException("'needed' field not found!");
		}
	}

	@Override
	public void importTrait() {
	}

	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	protected String getPrettyConfigIntern() {
		return "";
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
