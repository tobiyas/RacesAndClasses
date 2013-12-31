package de.tobiyas.racesandclasses.configuration.member.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.MemberConfigList;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption.SaveFormat;
import de.tobiyas.racesandclasses.persistence.converter.DBConverter;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.util.chat.WordParsing;
import de.tobiyas.util.config.YAMLConfigExtended;

public class MemberConfig extends Observable {
	
	/*
	 * The values for predefined stuff
	 */
	public static final String lifeDisplayEnable = "displayenable";
	public static final String displayInterval = "displayinterval";
	public static final String chatChannel = "chatchannel";
	public static final String cooldownInformation = "cooldowninform";
	public static final String displayType = "displaytype";
	
	
	/**
	 * The player this config belongs
	 */
	protected String player;
	
	/**
	 * The Configuration of the player as List
	 */
	protected MemberConfigList<ConfigOption> configList;
	
	/**
	 * The Plugin to call misc stuff upon
	 */
	protected static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The prefix for the config
	 */
	protected final String configPre;
	
	
	
	/**
	 * This constructor does nothing. No saving. No loading.
	 * It is just for inheritence.
	 * 
	 * @param player
	 * @param configPre
	 */
	protected MemberConfig(String player, String configPre){
		this.configPre = configPre;
		this.player = player;
	}
	
	/**
	 * Constructs a new Member Config for a player.
	 * It loads from the YAML loaded, if the player is found, and creates a new one if no exists.
	 * 
	 * @param player to create
	 */
	protected MemberConfig(String player){
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		this.player = player;
		configList = new MemberConfigList<ConfigOption>();
		configPre = "playerdata." + player + ".config";
		
		boolean defaultEnableHealthBar = DBConverter.getGeneralConfig().isConfig_enable_healthbar_in_chat();

		
		//first load the default values we know already
		ConfigOption lifeDisplayEnable = ConfigOption.loadFromPathOrCreateDefault(configPre, player, "lifeDisplayEnable", MemberConfig.lifeDisplayEnable, defaultEnableHealthBar, true);
		
		ConfigOption lifeDisplayInterval = ConfigOption.loadFromPathOrCreateDefault(configPre, player, "lifeDisplayInterval", MemberConfig.displayInterval, 60, true);
		
		ConfigOption currentChannel = ConfigOption.loadFromPathOrCreateDefault(configPre, player, "channelsCurrent", MemberConfig.chatChannel, "Global", true);
		
		ConfigOption informCooldownReady = ConfigOption.loadFromPathOrCreateDefault(configPre, player, "cooldownInform", MemberConfig.cooldownInformation, true, true);

		ConfigOption displayType = ConfigOption.loadFromPathOrCreateDefault(configPre, player, "displayType", MemberConfig.displayType, "scoreboard", true);
		
		configList.add(lifeDisplayEnable);
		configList.add(lifeDisplayInterval);
		configList.add(currentChannel);
		configList.add(informCooldownReady);
		configList.add(displayType);
		
		
		//Add other vars
		for(String value : config.getChildren(configPre)){
			if(configList.containsPathName(value)) continue;
			
			if(ConfigOption.isInValidFormat(configPre, player, value)){
				try{
					ConfigOption option = ConfigOption.loadFromPath(configPre, player, value);
					configList.add(option);
				}catch(IOException exp){}
			}
		}
	}
	

	/**
	 * Creates a new Member config.
	 * 
	 * @param player
	 * @return
	 */
	public static MemberConfig createMemberConfig(String player){
		return new MemberConfig(player);
	}
	
	
	/**
	 * Returns the Name of the Player of this Config.
	 * 
	 * @return holder of Configuration.
	 */
	public String getName(){
		return player;
	}

	/**
	 * Saves the current configuration.
	 * 
	 * WARNING: heavy saving actions here.
	 * @return 
	 */
	public MemberConfig save(){
		for(ConfigOption option : configList){
			if(!option.needsSaving) continue;
			
			option.save(configPre);
		}
		
		return this;
	}


	/**
	 * changes an attribute related to the passed display name and the
	 * value.
	 * 
	 * @param attribute to change
	 * @param value to change to
	 * @return true if worked, false otherwise
	 */
	public boolean changeAttribute(String attribute, Object value) {
		ConfigOption option = configList.getConfigOptionByDisplayName(attribute);
		if(option == null){
			return false;
		}
		
		SaveFormat format = option.getFormat();
		
		Object convertedValue = WordParsing.parseToSaveFormat(value, format);
		boolean returnValue = option.setObjectValue(convertedValue);
		option.save(configPre);
		
		if(returnValue){
			this.notifyObservers(attribute);
			this.setChanged();
		}
		
		return returnValue;
	}
	
		
	/**
	 * Gets all display names of all attributes
	 * 
	 * @param withInvisible if true, also returns invisible attributes
	 * 
	 * @return
	 */
	public List<String> getSupportetAttributes(boolean withInvisible){
		List<String> attributes = new ArrayList<String>();
		for(ConfigOption option : configList){
			if(withInvisible || option.isVisible()){
				attributes.add(option.getDisplayName());
			}
		}
		
		return attributes;
	}


	/**
	 * Returns a map of display names to values of the config.
	 * 
	 * @param withInvisible if true, also returns invisible attributes
	 * 
	 * @return
	 */
	public Map<String, Object> getCurrentConfig(boolean withInvisible) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		for(ConfigOption option : configList){
			if(withInvisible || option.isVisible()){
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
		ConfigOption option = configList.getConfigOptionByDisplayName(cooldownInformation);
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
	 * Returns the value corresponding to the path passed
	 * If not found, defaultValue is returned
	 * 
	 * @param path
	 * @param defaultValue the value returned if not found.
	 * @return
	 */
	public Object getValueOfPath(String path, Object defaultValue){
		return configList.containsPathName(path) ? configList.getConfigOptionByPath(path).getValue() : defaultValue;
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
	 * Returns the value corresponding to the display name passed
	 * If not found, Null is returned.
	 * 
	 * @param displayName
	 * @param defaultValue the value returned if not found.
	 * @return
	 */
	public Object getValueDisplayName(String displayName, Object defaultValue){
		return configList.contains(displayName) ? configList.getConfigOptionByDisplayName(displayName).getValue() : defaultValue;
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
		
		return option.setObjectValue(newValue);
	}
	
	
	/**
	 * Creates a new Option for the path and the display name.
	 * If any collision occurs (path already exist or display name already exists), false is returned.
	 * If the value can not be decoded to a correct value, false is returned.
	 * 
	 * True is returned, when everything worked.
	 * 
	 * @param path to set
	 * @param displayName to set
	 * @param value to set
	 * @param defaultValue to set
	 * @param visible to set
	 * 
	 * @return true if worked, false otherwise
	 */
	public boolean addOption(String path, String displayName, Object value, Object defaultValue, boolean visible){
		ConfigOption option = configList.getConfigOptionByPath(path);
		if(option != null){
			return false;
		}

		option = configList.getConfigOptionByDisplayName(displayName);
		if(option != null){
			return false;
		}
		
		option = new ConfigOption(path, player, displayName, value, defaultValue, visible);
		if(option.getFormat() == SaveFormat.UNKNOWN){
			return false;
		}
		
		configList.add(option);
		return true;
	}


	/**
	 * Checks if a Config option with the given name is present.
	 * Returns true if the name is found as DisplayName
	 * 
	 * @param identifier to check for
	 * @return true if found, false otherwise.
	 */
	public boolean containsValue(String identifier) {
		return configList.contains(identifier);
	}
	
	
	/**
	 * Returns all Config entries.
	 * This list is a copied list -> Changes will not affect the MemberConfig.
	 * -> read only.
	 * 
	 * WARNING! This is only for copiing from.
	 * 
	 * @return
	 */
	public List<ConfigOption> getAllConfigs(){
		List<ConfigOption> configList = new LinkedList<ConfigOption>();
		
		for(ConfigOption config : this.configList){
			configList.add(config.createCopy());
		}
		
		return configList;
	}
}
