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
package de.tobiyas.racesandclasses.configuration.managing;

import de.tobiyas.racesandclasses.configuration.global.ChannelConfig;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfig;
import de.tobiyas.racesandclasses.configuration.armory.ArmorConfig;
import de.tobiyas.racesandclasses.configuration.member.MemberConfigManager;
import de.tobiyas.racesandclasses.configuration.raceteams.RaceTeamManager;
import de.tobiyas.racesandclasses.configuration.racetoclass.RaceToClassConfiguration;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfigManager;

public class ConfigManager {
	
	private final GeneralConfig generalConfig;
	private final ChannelConfig channelConfig;
	private final RaceToClassConfiguration raceToClassConfig;
	private final ArmorConfig armorConfig;
	
	private final MemberConfigManager memberConfigManager;
	private final TraitConfigManager traitConfigManager;
	private final RaceTeamManager raceTeamManager;
	
	
	/**
	 * Creates all Configurations
	 */
	public ConfigManager(){
		generalConfig = new GeneralConfig();
		channelConfig = new ChannelConfig();
		raceToClassConfig = new RaceToClassConfiguration();
		armorConfig = new ArmorConfig();
		
		memberConfigManager = new MemberConfigManager();
		traitConfigManager = new TraitConfigManager();
		raceTeamManager = new RaceTeamManager();
	}
	
	/**
	 * reloads all Configurations
	 */
	public void reload(){
		generalConfig.reload();
		channelConfig.reload();
		raceToClassConfig.reload();
		armorConfig.reload();
		
		memberConfigManager.reload();
		traitConfigManager.reload();
		raceTeamManager.reaload();
	}
	
	
	/**
	 * Gets the general global config with general options
	 * 
	 * @return
	 */
	public GeneralConfig getGeneralConfig(){
		return generalConfig;
	}
	
	
	/**
	 * Gets the config for the Channels
	 * @return
	 */
	public ChannelConfig getChannelConfig(){
		return channelConfig;
	}

	
	/**
	 * Gets the Selection config from Race -> Class
	 * 
	 * @return
	 */
	public RaceToClassConfiguration getRaceToClassConfig() {
		return raceToClassConfig;
	}

	/**
	 * The member config manager to get configs of a member
	 * 
	 * @return the memberConfigManager
	 */
	public MemberConfigManager getMemberConfigManager() {
		return memberConfigManager;
	}

	/**
	 * @return the traitConfigManager
	 */
	public TraitConfigManager getTraitConfigManager() {
		return traitConfigManager;
	}
	
	
	/**
	 * Returns the Race-Team Manager.
	 * 
	 * @return RaceTeamManager.
	 */
	public RaceTeamManager getRaceTeamManager() {
		return raceTeamManager;
	}

	/**
	 * Returns the Armor-Config.
	 * 
	 * @return the Armor Config to use.
	 */
	public ArmorConfig getArmorConfig() {
		return armorConfig;
	}

}
