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
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race;

import java.util.HashSet;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.chat.ChatColorUtils;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class RaceContainer extends AbstractTraitHolder{

	protected double raceMaxHealth;
	
	protected String raceChatColor;
	protected String raceChatFormat;
	
	
	/**
	 * Creates a Race Container with the passed ConfigTotal and the passed name.
	 * 
	 * @param config to create with
	 * @param name to create with
	 */
	protected RaceContainer(YAMLConfigExtended config, String name){
		super(config, name);
	}
	
	
	/**
	 * Creates the RaceContainer from a sub-class
	 * 
	 * @param name
	 */
	protected RaceContainer(String name){
		super(new YAMLConfigExtended(Consts.racesYML), name);
	}
	
	@Override
	protected void readConfigSection() throws HolderConfigParseException{
		try{
			this.displayName = config.getString(configNodeName + ".config.name", configNodeName);
			this.manaBonus = config.getDouble(configNodeName + ".config.manabonus", 0);
			this.holderTag = ChatColorUtils.decodeColors(config.getString(configNodeName + ".config.racetag", "[" + configNodeName + "]"));
			this.raceMaxHealth = config.getDouble(configNodeName + ".config.raceMaxHealth", RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth());
			this.raceChatColor = config.getString(configNodeName + ".config.chat.color", RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_color());
			this.raceChatFormat = config.getString(configNodeName + ".config.chat.format", RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_format());
			
			readArmor();
		}catch(Exception exp){
			throw new HolderConfigParseException();
		}
	}
	
	@Override
	protected void addSTDTraits(){
		Trait normalArrow = TraitStore.buildTraitByName("NormalArrow", this);
		normalArrow.addTraitHolder(this);
		traits.add(normalArrow);
	}
	
	/**
	 * loads a RaceContainer from an YamlConfig
	 * 
	 * @param config
	 * @param name
	 * @return the container
	 */
	public static RaceContainer loadRace(YAMLConfigExtended config, String name) {
		RaceContainer container = new RaceContainer(config, name);
		return (RaceContainer) container;
	}
	
	@Override
	public boolean containsPlayer(RaCPlayer player){
		AbstractTraitHolder container = RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(player);
		if(container == null) return false;
		return container.getDisplayName().equals(configNodeName);
	}

	
	public double getRaceMaxHealth(){
		return raceMaxHealth;
	}


	public void editTABListEntry(RaCPlayer player) {
		if(player == null) return;
		
		if(!RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_adaptListName()) return;
		String total = holderTag + player.getName();
		if(total.length() > 16) total = total.substring(0, 15);
		
		player.getPlayer().setPlayerListName(total);
	}
	
	public String getRaceChatColor(){
		return raceChatColor;
	}
	
	public String getRaceChatFormat(){
		return raceChatFormat;
	}
	
	
	/**
	 * Creates the default race with default traits and values.
	 * @return
	 */
	public static RaceContainer generateSTDRace(){
		String defaultName = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultRaceName();
		String defaultTag = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultRaceTag();
		Consts.defaultRace = defaultName;
		
		String holderName = Consts.defaultRace;
		RaceContainer container = new RaceContainer(null, holderName);

		container.holderTag = defaultTag;
		container.raceChatColor = "";
		container.raceChatFormat = "";

		container.raceMaxHealth = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		container.armorUsage = new boolean[] { false, false, false, false, false };
	    container.traits = new HashSet<Trait>();

	    container.addSTDTraits();
	    
	    return container;
	}


	@Override
	protected String getContainerTypeAsString() {
		return "race";
	}


	@Override
	public AbstractHolderManager getHolderManager() {
		return RacesAndClasses.getPlugin().getRaceManager();
	}


	@Override
	public double getMaxHealthMod() {
		return raceMaxHealth - 20;
	}
}
