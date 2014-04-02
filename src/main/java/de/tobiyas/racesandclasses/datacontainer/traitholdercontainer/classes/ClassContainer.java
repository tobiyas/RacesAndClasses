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

import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.util.chat.ChatColorUtils;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ClassContainer extends AbstractTraitHolder{
	
	protected String classHealthModify;
	protected double classHealthModValue;
	
	
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
		try{
			this.manaBonus = config.getDouble(holderName + ".config.manabonus", 0);
			this.holderTag = ChatColorUtils.decodeColors(config.getString(holderName + ".config.classtag", "[" + holderName + "]"));
			this.classHealthModValue = evaluateValue(config.getString(holderName + ".config.health", "+0"));
			
			readArmor();
		}catch(Exception exp){
			throw new HolderConfigParseException();
		}
	}
	
	protected double evaluateValue(String val){
		char firstChar = val.charAt(0);
		
		classHealthModify = "";
		
		if(firstChar == '+')
			classHealthModify = "+";
		
		if(firstChar == '*')
			classHealthModify = "*";
		
		if(firstChar == '-')
			classHealthModify = "-";
		
		if(classHealthModify == ""){
			classHealthModify = "*";
		}else{
			val = val.substring(1, val.length());
		}

		double value = 1;
		try{
			value = Double.valueOf(val);			
		}catch(Exception exp){}
		
		return value;
	}
	
	
	@Override
	protected void addSTDTraits(){
		//None needed
	}
	
	/**
	 * loads a RaceContainer from an YamlConfig
	 * 
	 * @param config
	 * @param name
	 * @return the container
	 * @throws HolderParsingException 
	 */
	public static AbstractTraitHolder loadClass(YAMLConfigExtended config, String name) throws HolderParsingException{
		ClassContainer container = new ClassContainer(config, name);
		return (ClassContainer) container.load();
	}

	
	@Override
	public boolean containsPlayer(UUID player){
		AbstractTraitHolder container = RacesAndClasses.getPlugin().getClassManager().getHolderOfPlayer(player);
		if(container == null) return false;
		return container.getName().equals(holderName);
	}
	
	
	public double modifyToClass(double maxHealth){
		return evaluateClassMod(maxHealth);
	}
	
	
	private double evaluateClassMod(double maxHealth){
		char firstChar = classHealthModify.charAt(0);
		if(firstChar == '+')
			return maxHealth + classHealthModValue;
		
		if(firstChar == '*')
			return maxHealth * classHealthModValue;
		
		if(firstChar == '-')
			return maxHealth - classHealthModValue;
		
		return maxHealth;
	}


	@Override
	protected String getContainerTypeAsString() {
		return "class";
	}


	/**
	 * Returns the Class Health modifier
	 * @return
	 */
	public String getClassHealthModify() {
		return classHealthModify;
	}


	/**
	 * Returns the Class Health modification value
	 * @return
	 */
	public double getClassHealthModValue() {
		return classHealthModValue;
	}
	
	@Override
	public AbstractHolderManager getHolderManager() {
		return RacesAndClasses.getPlugin().getClassManager();
	}
}
