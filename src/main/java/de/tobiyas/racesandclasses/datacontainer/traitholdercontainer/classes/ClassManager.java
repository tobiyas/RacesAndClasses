/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassSelectedEvent;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ClassManager extends AbstractHolderManager{

	
	public ClassManager(){
		super(Consts.classesYML, "classes");
	}
	
	
	@Override
	public void init() {
		DefaultContainer.createSTDClasses();
		super.init();
	}

	@Override
	protected AbstractTraitHolder generateTraitHolder(
			YAMLConfigExtended traitHolderConfig, String holderName) {
		
		return ClassContainer.loadClass(traitHolderConfig, holderName);
	}


	@Override
	protected String getConfigPrefix() {
		return "class";
	}


	@Override
	public AbstractTraitHolder getDefaultHolder() {
		String className = plugin.getConfigManager().getGeneralConfig().getConfig_takeClassWhenNoClass();
		if(className == null || className.isEmpty()) return null;
		
		AbstractTraitHolder container = getHolderByName(className);
		return container;
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
	protected HolderSelectedEvent generateAfterSelectEvent(RaCPlayer player,
			AbstractTraitHolder newHolder) {
		return new AfterClassSelectedEvent(player.getPlayer(), (ClassContainer)newHolder);
	}


	@Override
	protected HolderSelectedEvent generateAfterChangeEvent(RaCPlayer player,
			AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		return new AfterClassChangedEvent(player.getPlayer(), (ClassContainer) newHolder, (ClassContainer) oldHolder);
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
