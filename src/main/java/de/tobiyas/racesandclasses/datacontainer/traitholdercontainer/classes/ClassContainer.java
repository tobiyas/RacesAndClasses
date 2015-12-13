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

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;


public class ClassContainer extends AbstractTraitHolder{
	
	/**
	 * Private constructor to only build via static builder
	 * 
	 * @param config
	 * @param name
	 */
	protected ClassContainer(YAMLConfigExtended config, String name){
		super(config, name);
	}
	
	
	/**
	 * Creates a ClassContainer for Sub-classed classes
	 * 
	 * @param name to create
	 */
	protected ClassContainer(String name){
		super(new YAMLConfigExtended(Consts.classesYML) ,name);
	}
	
	
	@Override
	protected void readConfigSection() throws HolderConfigParseException{
		super.readConfigSection();
		
		try{
			//Fill more Infos here:
		}catch(Exception exp){
			throw new HolderConfigParseException();
		}
		
		//Backwards compability.
		this.holderTag = config.getString(configNodeName + ".config.classtag", holderTag);
	}
	
	
	
	@Override
	protected void addSTDTraits(){
		//None needed
	}
	
	/**
	 * loads a RaceContainer from an YamlConfig.
	 * Does not load yet.
	 * 
	 * @param config
	 * @param name
	 * @return the container
	 * @throws HolderParsingException 
	 */
	public static AbstractTraitHolder loadClass(YAMLConfigExtended config, String name){
		ClassContainer container = new ClassContainer(config, name);
		return (ClassContainer) container;
	}

	
	@Override
	public boolean containsPlayer(RaCPlayer player){
		AbstractTraitHolder container = RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(player);
		if(container == null) return false;
		return container.getDisplayName().equals(configNodeName);
	}
	


	@Override
	protected String getContainerTypeAsString() {
		return "class";
	}

	
	@Override
	public AbstractHolderManager getHolderManager() {
		return RacesAndClasses.getPlugin().getClassManager();
	}

}
