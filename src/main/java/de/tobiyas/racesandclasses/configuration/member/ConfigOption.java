package de.tobiyas.racesandclasses.configuration.member;

import java.io.IOException;

import de.tobiyas.util.config.YAMLConfigExtended;

public class ConfigOption {

	/**
	 * An enum to simplify value storage / loading.
	 * 
	 * @author Tobiyas
	 */
	public enum SaveFormat{
		INT,
		DOUBLE,
		STRING,
		BOOLEAN,
		
		UNKNOWN;
	}
	
	/**
	 * The path to the player invisible part
	 */
	protected final static String DEFAULT_VISIBLE_PATH = "visible";
	/**
	 * The path to the value
	 */
	protected final static String DEFAULT_VALUE_PATH = "val";
	/**
	 * The path to the value
	 */
	protected final static String DEFAULT_DEFAULTVALUE_PATH = "defval";
	/**
	 * The path to the value
	 */
	protected final static String DEFAULT_FORMAT_PATH = "format";
	/**
	 * path to nice human readable name
	 */
	protected final static String DEFAULT_DISPLAY_NAME_PATH = "name";
	
	
	
	/**
	 * The path to the config option
	 */
	private final String path;
	
	/**
	 * The value placed in the option
	 */
	private Object value;
	
	
	/**
	 * If there is an default value.
	 */
	private final Object defaultValue;
	
	/**
	 * if the player can see this value
	 */
	private final boolean visible;
	
	/**
	 * The format the value is saved as
	 */
	private final SaveFormat format;
	
	/**
	 * The display name of the option
	 */
	private final String displayName;
	
	
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
	public ConfigOption(String path, Object value) {
		this(path, path, value, value, true);
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
	public ConfigOption(String path, String displayName, Object value, Object defaultValue){
		this(path, displayName, value, defaultValue, true);
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
	public ConfigOption(String path, String displayName, Object value, Object defaultValue, boolean visible) {
		this.path = path;
		this.value = value;
		this.visible = visible;
		
		this.defaultValue = defaultValue;
		this.displayName = displayName;
		this.format = identifyFormat(value);
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
	public boolean setValue(Object value) {
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
	public void saveToYaml(YAMLConfigExtended config, String pre){
		config.load();
		
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
	 * @param config to build from
	 * @param pre to search
	 * @param path to use
	 * 
	 * @return the fully loaded configuration
	 * 
	 * @throws IOException when the Option could not be build.
	 */
	public static ConfigOption loadFromPath(YAMLConfigExtended config, String pre, String path) throws IOException{
		config.load();
		
		boolean visible = config.getBoolean(pre + "." + path + "." + DEFAULT_VISIBLE_PATH, true);
		Object defaultValue = config.get(pre + "." + path + "." + DEFAULT_DEFAULTVALUE_PATH, null);
		Object value = config.get(pre + "." + path + "." + DEFAULT_VALUE_PATH, defaultValue);
		
		String displayName = config.getString(pre + "." + path + "." + DEFAULT_DISPLAY_NAME_PATH, path);
		
		if(identifyFormat(value) == SaveFormat.UNKNOWN){
			throw new IOException("Could not determin Type of '" + path + "' was: " + value);
		}
		
		return new ConfigOption(path, displayName, value, defaultValue, visible);
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
	public static ConfigOption loadFromPathOrCreateDefault(YAMLConfigExtended config, String pre, 
			String path, String defaultDisplayName, Object defaultValue, boolean defaultVisiblity){
		
		try{
			return loadFromPath(config, pre, path);
		}catch(IOException exp){
			ConfigOption option = new ConfigOption(path, defaultDisplayName, defaultValue, defaultValue, defaultVisiblity);
			option.saveToYaml(config, pre);
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
	public static boolean isInValidFormat(YAMLConfigExtended config,
			String pre, String path) {
		try{
			loadFromPath(config, pre, path);
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
}
