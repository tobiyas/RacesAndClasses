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
package de.tobiyas.racesandclasses.configuration.member.database;

import de.tobiyas.racesandclasses.configuration.member.MemberConfigList;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;

public class DBMemberConfig extends MemberConfig {

	
	/**
	 * Creates the super class
	 * 
	 * @param player to load / create new.
	 */
	protected DBMemberConfig(String player) {
		super(player, "playerdata." + player + ".config.");
		
		configList = new MemberConfigList<ConfigOption>();
		
		boolean defaultEnableHealthBar = plugin.getConfigManager().getGeneralConfig().isConfig_enable_healthbar_in_chat();
		
		//first load the default values we know already
		DBConfigOption lifeDisplayEnable = DBConfigOption.loadFromPathOrCreateDefault(player, MemberConfig.lifeDisplayEnable, 
				defaultEnableHealthBar, defaultEnableHealthBar, true);
				
		DBConfigOption lifeDisplayInterval = DBConfigOption.loadFromPathOrCreateDefault(player, MemberConfig.displayInterval, 60, 60, true);
		
		DBConfigOption currentChannel = DBConfigOption.loadFromPathOrCreateDefault(player, MemberConfig.chatChannel, "Global", "Global", true);
		
		DBConfigOption informCooldownReady = DBConfigOption.loadFromPathOrCreateDefault(player, MemberConfig.cooldownInformation, true, true, true);
		
		DBConfigOption displayType = DBConfigOption.loadFromPathOrCreateDefault(player, "displayType", MemberConfig.displayType, "scoreboard", true);
		
		
		configList.add(lifeDisplayEnable);
		configList.add(lifeDisplayInterval);
		configList.add(currentChannel);
		configList.add(informCooldownReady);
		configList.add(displayType);
		
		//Add other vars
		for(DBConfigOption value : plugin.getDatabase().find(DBConfigOption.class).where().ieq("playerName", player).findList()){
			if(value == null) continue;
			
			if(configList.containsPathName(value.getPath())) continue;
			configList.add(value);
		}
	}

	
	/**
	 * Copy constructor.
	 * 
	 * NO SAVING HAPPENS HERE!!!
	 * 
	 * @param config
	 */
	protected DBMemberConfig(MemberConfig config){
		super(config.getName(), "playerdata." + config.getName() + ".config.");
		
		configList = new MemberConfigList<ConfigOption>();
		
		//Add other vars
		for(ConfigOption value: config.getAllConfigs()){
			if(value == null) continue;
			
			if(configList.containsPathName(value.getPath())) continue;
			configList.add(DBConfigOption.copyFrom(value));
		}
	}
	
	
	@Override
	public DBMemberConfig save(){
		for(int i = 0; i < this.configList.size(); i++){
			ConfigOption option = configList.get(i);
			if(option != null && option instanceof DBConfigOption){
				DBConfigOption dbOption = (DBConfigOption) option;
				dbOption.save(""); //no pre needed.
			}
		}
		
		return this;
	}
	
	
	/**
	 * Creates a new Member config.
	 * 
	 * @param player
	 * @return
	 */
	public static DBMemberConfig createMemberConfig(String player){
		return new DBMemberConfig(player);
	}



	/**
	 * Copies the passed Config and saves it to the DB
	 * 
	 * @param config
	 * @return
	 */
	public static DBMemberConfig copyFrom(MemberConfig config) {
		if(config instanceof DBMemberConfig) return (DBMemberConfig) config;		
		return new DBMemberConfig(config);
	}
	
	
	
}
