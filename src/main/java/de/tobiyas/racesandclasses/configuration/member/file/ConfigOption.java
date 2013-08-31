package de.tobiyas.racesandclasses.configuration.member.file;

import java.io.IOException;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

import de.tobiyas.racesandclasses.util.persistence.YAMLPersistenceProvider;
import de.tobiyas.util.config.YAMLConfigExtended;

@Entity
public class ConfigOption {

	/**
	 * An enum to simplify value storage / loading.
	 * 
	 * @author Tobiyas
	 */
	@Embeddable
	public enum SaveFormat{
		INT("000"),
		DOUBLE("001"),
		STRING("002"),
		BOOLEAN("003"),
		
		UNKNOWN("999");
		
		/**
		 * An Id for parsing later
		 */
		public final String id;
		
		/**
		 * Creates the enum with the passed id
		 * @param id
		 */
		private SaveFormat(String id) {
			this.id = id;
		}
		
		/**
		 * Parses the format with the given ID.
		 * If no ID fits, {@link #UNKNOWN} is returned.
		 * This id is trimmed to the first 3 characters.
		 * 
		 * @param id the id to search for
		 * @return the resolved {@link SaveFormat} or {@link #UNKNOWN}.
		 */
		public static SaveFormat parseFromId(String id){
			if(id.length() < 3) return UNKNOWN;
			
			id = id.substring(0,3);
			
			for(SaveFormat format : values()){
				if(id.equalsIgnoreCase(format.id)){
					return format;
				}
			}
			
			return UNKNOWN;
		}
	}
	
	
	/**
	 * The path to the player invisible part
	 */
	@Transient
	protected final static String DEFAULT_VISIBLE_PATH = "visible";
	
	/**
	 * The path to the value
	 */
	@Transient
	protected final static String DEFAULT_VALUE_PATH = "val";
	
	/**
	 * The path to the value
	 */
	@Transient
	protected final static String DEFAULT_DEFAULTVALUE_PATH = "defval";
	
	/**
	 * The path to the value
	 */
	@Transient
	protected final static String DEFAULT_FORMAT_PATH = "format";
	
	/**
	 * path to nice human readable name
	 */
	@Transient
	protected final static String DEFAULT_DISPLAY_NAME_PATH = "name";
	
	
	
	/**
	 * The path to the config option
	 */
	@Id
	@NotEmpty
	protected String path;
	
	/**
	 * The value placed in the option
	 */
	@Transient
	protected Object value;
	
	
	/**
	 * If there is an default value.
	 */
	@Transient
	protected Object defaultValue;
	
	/**
	 * if the player can see this value
	 */
	@NotNull
	protected boolean visible;
	
	/**
	 * The format the value is saved as
	 */
	@Embedded
	protected SaveFormat format;
	
	/**
	 * The display name of the option
	 */
	@NotEmpty
	protected String displayName;
	
	
	/**
	 * Creates a new config option that represents the following:
	 * <br> - a path to the config
	 * <br> - the value saved
	 * 
	 * The default value is assumed the value passed.
	 * The display name is the path.
	 * Always visible.
	 * 
	 * @param path to the config
	 * @param value the value to store
	 */
	public ConfigOption(String path, String playerName, Object value) {
		this(path, playerName, path, value, value, true);
	}
	
	/**
	 * Creates a new config option that represents the following:
	 * <br> - a path to the config
	 * <br> - the display name of the option
	 * <br> - the value saved
	 * <br> - a default value
	 * 
	 * Always visible.
	 * 
	 * @param path to the config
	 * @param value the value to store
	 * @param defaultValue the default value when no other value given
	 */
	public ConfigOption(String path, String playerName, String displayName, Object value, Object defaultValue){
		this(path, playerName, displayName, value, defaultValue, true);
	}
	
	
	/**
	 * Creates a new config option that represents the following:
	 * <br> - a path to the config
	 * <br> - the display name of the option
	 * <br> - the value saved
	 * <br> - the default value
	 * <br> - if it is visible
	 * 
	 * @param path to the config
	 * @param value the value to store
	 * @param visible if it is visible to the player
	 */
	public ConfigOption(String path, String playerName, String displayName, Object value, Object defaultValue, boolean visible) {
		this.path = path;
		this.value = value;
		this.visible = visible;
		
		this.defaultValue = defaultValue;
		this.displayName = displayName;
		this.format = identifyFormat(value);
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param option
	 */
	protected ConfigOption(ConfigOption option) {
		this.path = option.path;
		this.value = option.value;
		this.visible = option.visible;
		
		this.defaultValue = option.defaultValue;
		this.displayName = option.displayName;
		this.format = option.format;
	}
	
	
	/**
	 * ONLY FOR DB ACCESS!!!!
	 */
	public ConfigOption(){
	}

	
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	
	
	/**
	 * Tries to set the new value.
	 * Returns true if worked,
	 * returns false if new value is in wrong format.
	 * 
	 * @param value the value to set
	 * 
	 * @return true if worked, false otherwise
	 */
	public boolean setObjectValue(Object value) {
		SaveFormat newValueFormat = identifyFormat(value);
		if(newValueFormat == SaveFormat.UNKNOWN || newValueFormat != format){
			return false;
		}
		
		this.value = value;
		return true;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the defaultValue
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @return the format
	 */
	public SaveFormat getFormat() {
		return format;
	}

	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Save this config Option to the YAML file.
	 * It is saved to the pre + this.path
	 * 
	 * @param config to save to
	 * @param pre to save to
	 */
	public void save(String pre){
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(true);
		
		config.set(pre + "." + path + "." + DEFAULT_FORMAT_PATH, format.name());
		config.set(pre + "." + path + "." + DEFAULT_VALUE_PATH, value);
		config.set(pre + "." + path + "." + DEFAULT_DEFAULTVALUE_PATH, defaultValue);
		config.set(pre + "." + path + "." + DEFAULT_VISIBLE_PATH, visible);
		config.set(pre + "." + path + "." + DEFAULT_DISPLAY_NAME_PATH, displayName);
		
		config.save();
	}
	
	
	/**
	 * Builds a Option from a path, a prefix and a configuration file.
	 * 
	 * @param pre to search
	 * @param path to use
	 * 
	 * @return the fully loaded configuration
	 * 
	 * @throws IOException when the Option could not be build.
	 */
	public static ConfigOption loadFromPath(String pre, String playerName, String path) throws IOException{
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(true);
		
		boolean visible = config.getBoolean(pre + "." + path + "." + DEFAULT_VISIBLE_PATH, true);
		Object defaultValue = config.get(pre + "." + path + "." + DEFAULT_DEFAULTVALUE_PATH, null);
		Object value = config.get(pre + "." + path + "." + DEFAULT_VALUE_PATH, defaultValue);
		
		String displayName = config.getString(pre + "." + path + "." + DEFAULT_DISPLAY_NAME_PATH, path);
		
		if(value == null || identifyFormat(value) == SaveFormat.UNKNOWN){
			throw new IOException("Could not determin Type of '" + path + "' was: " + value);
		}
		
		return new ConfigOption(path, playerName, displayName, value, defaultValue, visible);
	}
	
	
	/**
	 *  Tries to load the value first.
	 *  If this does not work, it provides a new Option with the default value.
	 * 
	 * 
	 * @param config to read
	 * @param pre to read from
	 * @param path to read
	 * 
	 * @param defaultDisplayName
	 * @param defaultValue if reading did not work
	 * @param defaultVisiblity if reading did not work
	 * @return
	 */
	public static ConfigOption loadFromPathOrCreateDefault(String pre, String playerName,
			String path, String defaultDisplayName, Object defaultValue, boolean defaultVisiblity){
		
		try{
			return loadFromPath(pre, playerName, path);
		}catch(IOException exp){
			ConfigOption option = new ConfigOption(path, playerName, defaultDisplayName, defaultValue, defaultValue, defaultVisiblity);
			option.save(pre);
			return option;
		}
	}

	/**
	 * Checks if the option is in an valid state.
	 * 
	 * @param config to check
	 * @param pre to check
	 * @param path to check
	 * @return true if it is valid, false otherwise
	 */
	public static boolean isInValidFormat(String pre, String playerName, String path) {
		try{
			loadFromPath(pre, playerName, path);
			return true;
		}catch(IOException exp){
			return false;
		}
	}
	
	/**
	 * tries to identify the format of a Object
	 * Returns UNKNOWN if nothing matched 
	 *  
	 * @param value to check
	 * @return
	 */
	protected static SaveFormat identifyFormat(Object value){
		if(value instanceof String){
			return SaveFormat.STRING;
		}
		
		if(value instanceof Integer){
			return SaveFormat.INT;
		}
		
		if(value instanceof Boolean){
			return SaveFormat.BOOLEAN;
		}
		
		if(value instanceof Double){
			return SaveFormat.DOUBLE;
		}
		
		return SaveFormat.UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + (visible ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigOption other = (ConfigOption) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (format != other.format)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (visible != other.visible)
			return false;
		return true;
	}

	
	/**
	 * Creates a copy of the current option.
	 * 
	 * @return
	 */
	public ConfigOption createCopy() {
		return new ConfigOption(this);
	}

	
	////////////////////////////
	//Setters for DB Building.//
	////////////////////////////
	public void setPath(String path) {
		this.path = path;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setFormat(SaveFormat format) {
		this.format = format;
	}

}
