package de.tobiyas.racesandclasses.util.traitutil;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;

public class TraitConfigurationFailedException extends HolderTraitParseException {	
	private static final long serialVersionUID = -1228403755162849596L;
	
	public TraitConfigurationFailedException(String string) {
		super(string);
	}

}
