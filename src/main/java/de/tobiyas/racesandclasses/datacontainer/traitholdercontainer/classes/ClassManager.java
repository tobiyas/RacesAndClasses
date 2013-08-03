package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ClassManager extends AbstractHolderManager{

	private static ClassManager classManager;

	
	public ClassManager(){
		super(Consts.playerDataYML, Consts.classesYML);
		classManager = this;
		
		DefaultContainer.createSTDClasses();
	}
	
	
	public static ClassManager getInstance(){
		return classManager;
	}

	@Override
	protected AbstractTraitHolder generateTraitHolderAndLoad(
			YAMLConfigExtended traitHolderConfig, String holderName)
			throws HolderParsingException {
		
		return ClassContainer.loadClass(traitHolderConfig, holderName);
	}


	@Override
	protected String getConfigPrefix() {
		return "class";
	}


	@Override
	public AbstractTraitHolder getDefaultHolder() {
		//no default class needed
		return null;
	}


	@Override
	protected void initDefaultHolder() {
		//not needed
	}


	@Override
	public String getContainerTypeAsString() {
		return "class";
	}
}
