package de.tobiyas.racesandclasses.listeners.holderchangegui;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;

public class ClassChangeListenerGui extends HolderChangeListenerGui {

	
	public ClassChangeListenerGui(){
		super(ClassManager.getInstance());
	}
	
}
