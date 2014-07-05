package de.tobiyas.racesandclasses.traitcontainer.traits.pattern;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public abstract class AbstractActivateAETrait extends AbstractBasicTrait {

	public AbstractActivateAETrait() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public TraitResults trigger(EventWrapper wrapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}

}
