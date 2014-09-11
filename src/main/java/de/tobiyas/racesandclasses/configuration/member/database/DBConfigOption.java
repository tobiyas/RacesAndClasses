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

import javax.persistence.Entity;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotEmpty;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;


@Entity
@Table(name = "_config_options")
public class DBConfigOption extends ConfigOption {
	
	@NotEmpty
	private String stringValue;
	
	@NotEmpty
	private String stringDefaultValue;
	
	
	/**
	 * The Plugin to call DB Operations on
	 */
	@Transient
	private final RacesAndClasses plugin;
	
	
	/**
	 * @see ConfigOption
	 */
	public DBConfigOption(String path, RaCPlayer player, Object value) {
		super(path, player, value);
		
		plugin = RacesAndClasses.getPlugin();
		
		this.stringValue = String.valueOf(value);
		this.stringDefaultValue = String.valueOf(value);
		this.player = player;
	}
	
	
	/**
	 * @see ConfigOption
	 */
	public DBConfigOption(String path, RaCPlayer player, String displayName, Object value, Object defaultValue, boolean visible) {
		super(path, player, displayName, value, defaultValue, visible);
		
		plugin = RacesAndClasses.getPlugin();
		
		this.stringValue = String.valueOf(value);
		this.stringDefaultValue = String.valueOf(defaultValue);
		this.player = player;
	}

	
	
	/**
	 * DB init Constructor.
	 * DO NOT USE!
	 */
	public DBConfigOption(){
		super();
		
		plugin = RacesAndClasses.getPlugin();
	}

	/**
	 * DB init Constructor.
	 * DO NOT USE!
	 */
	public DBConfigOption(RaCPlayer playerName){
		super(playerName);
		
		plugin = RacesAndClasses.getPlugin();
	}
	
	
	
	/**
	 * Copy constructor
	 * 
	 * @param option
	 */
	protected DBConfigOption(ConfigOption option, RaCPlayer player){
		super(option);
		
		plugin = RacesAndClasses.getPlugin();
		this.player = player;
	}
	
	

	@Override
	public void save(String pre) {
		if(!needsSaving) return;
		//When no saving needed, don't try.
		
		try{
			if(plugin.getDatabase().find(DBConfigOption.class).where().ieq("path", player + path).findUnique() == null){
				plugin.getDatabase().save(this);
			}else{
				plugin.getDatabase().update(this);
			}
			
			needsSaving = false;
		}catch(Exception exp){
			//We don't want the console to get spammed as shit.
			//plugin.getDebugLogger().logStackTrace(exp);
		}
		
	}
	
	
	public RaCPlayer getPlayerUUID() {
		return player;
	}


	/**
	 * Tries to load the path.
	 * If the path is not available, 
	 * it creates a new Option with the passed values.
	 * 
	 * 
	 * @param path
	 * @param player
	 * @param displayName
	 * @param value
	 * @param defaultValue
	 * @param defaultVisiblity
	 * 
	 * @return
	 */
	public static DBConfigOption loadFromPathOrCreateDefault(RaCPlayer player, String displayName, 
			Object value, Object defaultValue, boolean defaultVisiblity){
		
		EbeanServer dbServer = RacesAndClasses.getPlugin().getDatabase();
		try{
			DBConfigOption option = dbServer.find(DBConfigOption.class).where().ieq("path", player.getUniqueId() + displayName).findUnique();
			if(option != null) return option;
			
			throw new PersistenceException("Value not found.");
		}catch(PersistenceException exp){
			DBConfigOption config = new DBConfigOption(displayName, player, displayName, value, defaultValue, defaultVisiblity);
			config.save("");
			return config;
		}
		
	}
	
	
	/**
	 * Copies from an ConfigTotal Option
	 * 
	 * @param option to copy
	 * @return the copied DBConfigOption
	 */
	public static DBConfigOption copyFrom(ConfigOption option){
		String path = option.getPath();
		String displayName = option.getDisplayName();
		RaCPlayer playerUUID = option.getPlayerUUID();
		
		Object value = option.getValue();
		Object defaultValue = option.getDefaultValue();
		boolean visible = option.isVisible();
		
		return new DBConfigOption(path, playerUUID, displayName, value, defaultValue, visible);
	}


	/**
	 * ONLY FOR DB ACCESS!!!!
	 * 
	 * @param player
	 */
	public void setPlayerName(RaCPlayer player) {
		if(this.player != null) return;
		this.player = player;
	}
	
	
	@Override
	public void setPath(String path){
		this.path = path;
	}
	
	@Override
	public String getPath(){
		return this.player + this.path;
	}


	
	
	@Override
	public String getDisplayName() {
		return super.getDisplayName();
	}


	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}


	@Override
	public void setDisplayName(String displayName) {
		super.setDisplayName(displayName);
	}
	
	
	public String getStringValue() {
		return stringValue;
	}


	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}


	public String getStringDefaultValue() {
		return stringDefaultValue;
	}


	public void setStringDefaultValue(String stringDefaultValue) {
		this.stringDefaultValue = stringDefaultValue;
	}

	
	@Override
	public Object getValue(){
		return parseValueToFormat(stringValue, format);
	}
	

	@Override
	public boolean setObjectValue(Object value) {
		boolean worked = super.setObjectValue(value);
		if(worked){
			this.stringValue = String.valueOf(value);
		}
		
		return worked;
	}


	/**
	 * Parses an Object from an String and a Format.
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	protected Object parseValueToFormat(String value, SaveFormat format) {
		switch(format){
			case BOOLEAN: return Boolean.parseBoolean(value); 
			case DOUBLE: return Double.parseDouble(value); 
			case INT: return Integer.parseInt(value); 
			case STRING: return value; 
			case UNKNOWN: return null;
		}
		
		//Dead code!
		return null;
	}
}
