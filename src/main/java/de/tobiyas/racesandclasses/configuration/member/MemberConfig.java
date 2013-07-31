package de.tobiyas.racesandclasses.configuration.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.ConfigOption.SaveFormat;
import de.tobiyas.racesandclasses.util.chat.WordParsing;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class MemberConfig {
	
	/*
	 * The values for predefined stuff
	 */
	public static final String lifeDisplayEnable = "displayenable";
	public static final String displayInterval = "displayinterval";
	public static final String chatChannel = "chatchannel";
	public static final String cooldownInformation = "cooldowninform";
	
	
	/**
	 * The player this config belongs
	 */
	private String player;
	
	/**
	 * The Configuration of the player as List
	 */
	private MemberConfigList configList;
	
	/**
	 * The global Player Data file to save the config to
	 */
	private static YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML);
	
	/**
	 * The Plugin to call misc stuff upon
	 */
	private static RacesAndClasses plugin;
	
	private final String configPre;
	
	private MemberConfig(String player){
		plugin = RacesAndClasses.getPlugin();
		
		config.load();
		this.player = player;
		configList = new MemberConfigList();
		configPre = "playerdata." + player + ".config.";
		
		boolean defaultEnableHealthBar = plugin.getConfigManager().getGeneralConfig().isConfig_enable_healthbar_in_chat();

		//first load the default values we know already
		ConfigOption lifeDisplayEnable = ConfigOption.loadFromPathOrCreateDefault(config, 
				configPre, "lifeDisplay.enable", "displayenable", defaultEnableHealthBar, true);
		
		ConfigOption lifeDisplayInterval = ConfigOption.loadFromPathOrCreateDefault(config, 
				configPre, "lifeDisplay.interval", "displayinterval", 60, true);
		
		ConfigOption currentChannel = ConfigOption.loadFromPathOrCreateDefault(config, 
				configPre, "channels.current", "chatchannel", "Global", true);
		
		ConfigOption informCooldownReady = ConfigOption.loadFromPathOrCreateDefault(config, 
				configPre, "cooldown.inform", "cooldowninform", true, true);
		
		configList.add(lifeDisplayEnable);
		configList.add(lifeDisplayInterval);
		configList.add(currentChannel);
		configList.add(informCooldownReady);
		
		
		//Add other vars
		for(String value : config.getChildren(configPre)){
			if(configList.containsPathName(value)) continue;
			
			if(ConfigOption.isInValidFormat(config, configPre, value)){
				try{
					ConfigOption option = ConfigOption.loadFromPath(config, configPre, value);
					configList.add(option);
				}catch(IOException exp){}
			}
		}
		
		save();
	}
	

	/**
	 * Creates a new Member config.
	 * This should not be used!
	 * 
	 * @param player
	 * @return
	 */
	public static MemberConfig createMemberConfig(String player){
		return new MemberConfig(player);
	}
	
	
	
	public String getName(){
		return player;
	}

	public void save(){
		config.load();
		
		for(ConfigOption option : configList){
			option.saveToYaml(config, configPre);
		}
		
		config.save();
	}


	/**
	 * changes an attribute related to the passed display name and the
	 * value.
	 * 
	 * @param attribute to change
	 * @param value to change to
	 * @return true if worked, false otherwise
	 */
	public boolean changeAttribute(String attribute, String value) {
		ConfigOption option = configList.getConfigOptionByDisplayName(attribute);
		if(option == null){
			return false;
		}
		
		SaveFormat format = option.getFormat();
		
		Object convertedValue = WordParsing.parseToSaveFormat(value, format);
		option.setValue(convertedValue);
		option.saveToYaml(config, configPre);
		
		return false;
	}
	
		
	/**
	 * Gets all display names of all attributes that are visible
	 * 
	 * @return
	 */
	public ArrayList<String> getSupportetAttributes(){
		ArrayList<String> attributes = new ArrayList<String>();
		for(ConfigOption option : configList){
			if(option.isVisible()){
				attributes.add(option.getDisplayName());
			}
		}
		
		return attributes;
	}


	/**
	 * Returns a map of display names to values of the config.
	 * Only visible options are shown.
	 * 
	 * @return
	 */
	public HashMap<String, Object> getCurrentConfig() {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		for(ConfigOption option : configList){
			if(option.isVisible()){
				attributes.put(option.getDisplayName(), option.getValue());
			}
		}
		
		return attributes;
	}


	/**
	 * Returns the current chat channel of a player
	 * 
	 * @return
	 */
	public String getCurrentChannel() {
		ConfigOption option = configList.getConfigOptionByDisplayName(chatChannel);
		if(option == null){
			return "Global";
		}
		
		return (String) option.getValue();
	}


	/**
	 * Returns the current value if cooldowns shall be anounced.
	 * 
	 * @return
	 */
	public boolean getInformCooldownReady() {
		ConfigOption option = configList.getConfigOptionByDisplayName(chatChannel);
		if(option == null){
			return true;
		}
		
		return (Boolean) option.getValue();
	}
	
	
	/**
	 * Returns the current value if life Display shall be shown
	 * 
	 * @return
	 */
	public boolean getEnableLifeDisplay(){
		boolean defaultEnableHealthBar = plugin.getConfigManager().getGeneralConfig().isConfig_enable_healthbar_in_chat();
		
		ConfigOption option = configList.getConfigOptionByDisplayName(lifeDisplayEnable);
		if(option == null){
			return defaultEnableHealthBar;
		}
		
		return (Boolean) option.getValue();
	}
	
	
	/**
	 * Returns the current value of the interval time to check for the health bar
	 * Value is in ticks -> (20 per second).
	 * 
	 * @return
	 */
	public int getLifeDisplayInterval(){
		ConfigOption option = configList.getConfigOptionByDisplayName(displayInterval);
		if(option == null){
			return 60;
		}
		
		return (Integer) option.getValue();
	}
	
	
	/**
	 * Returns the value corresponding to the path passed
	 * If not found, Null is returned
	 * 
	 * @param path
	 * @return
	 */
	public Object getValueOfPath(String path){
		return configList.containsPathName(path) ? configList.getConfigOptionByPath(path).getValue() : null;
	}
	
	/**
	 * Returns the value corresponding to the display name passed
	 * If not found, Null is returned.
	 * 
	 * @param displayName
	 * @return
	 */
	public Object getValueDisplayName(String displayName){
		return configList.contains(displayName) ? configList.getConfigOptionByDisplayName(displayName).getValue() : null;
	}
	
	
	/**
	 * Sets a new value to the config.
	 * Returns false if wrong value type.
	 * 
	 * @param displayName the option to set.
	 * @param newValue the new value to set.
	 * 
	 * @return true if worked, false otherwise (mostly wrong type)
	 */
	public boolean setValue(String displayName, Object newValue){
		ConfigOption option = configList.getConfigOptionByDisplayName(displayName);
		if(option == null){
			return false;
		}
		
		return option.setValue(newValue);
	}
}
