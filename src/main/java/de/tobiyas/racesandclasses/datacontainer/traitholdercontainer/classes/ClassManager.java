package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassSelectedEvent;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ClassManager extends AbstractHolderManager{

	
	public ClassManager(){
		super(Consts.playerDataYML, Consts.classesYML);
		
		DefaultContainer.createSTDClasses();
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


	@Override
	protected String getCorrectFieldFromDBHolder(
			PlayerHolderAssociation container) {
		return container.getClassName();
	}


	@Override
	protected String getDBFieldName() {
		return "className";
	}


	@Override
	protected void saveContainerToDBField(PlayerHolderAssociation container,
			String name) {
		container.setClassName(name);
	}


	@Override
	protected HolderSelectEvent generateAfterSelectEvent(String player,
			AbstractTraitHolder newHolder) {
		return new AfterClassSelectedEvent(Bukkit.getPlayer(player), (ClassContainer)newHolder);
	}


	@Override
	protected HolderSelectEvent generateAfterChangeEvent(String player,
			AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		return new AfterClassChangedEvent(Bukkit.getPlayer(player), (ClassContainer) newHolder, (ClassContainer) oldHolder);
	}
	
	@Override
	protected AbstractTraitHolder getStartingHolder() {
		String className = plugin.getConfigManager().getGeneralConfig().getConfig_takeClassWhenNoClass();
		if(className == null || "".equals(className)){
			return getDefaultHolder();
		}
		
		AbstractTraitHolder holder = getHolderByName(className);
		
		return holder != null ? holder : getDefaultHolder();
	}
}
