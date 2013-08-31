package de.tobiyas.racesandclasses.APIs;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;

public class MemberConfigurationAPI {

	/**
	 * The plugin to call config stuff upon
	 */
	private static final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * This is the setter Part of the Configuration API.
	 * Here you can store values to a player.
	 * 
	 * @author Tobiyas
	 *
	 */
	public static class Set {

		
		/**
		 * Sets an Int value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key
		 * @param value the value to set.
		 * @param visibleForPlayer if the player can see this value.
		 * 
		 * @return true if it worked, false otherwise
		 */
		public static boolean setIntToPlayer(String playerName, String identifier, int value, boolean visibleForPlayer){
			return setValueToPlayer(playerName, identifier, value, visibleForPlayer);
		}
		
		
		/**
		 * Sets a Double value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * 
		 * @param playerName the player to change the config.
		 * @param identidier the route to the key
		 * @param value the value to set.
		 * @param visibleForPlayer if the player can see this value.
		 * 
		 * @return true if it worked, false otherwise
		 */
		public static boolean setDoubleToPlayer(String playerName, String identifier, double value, boolean visibleForPlayer){
			return setValueToPlayer(playerName, identifier, value, visibleForPlayer);
		}
		
		/**
		 * Sets a String value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * 
		 * @param playerName the player to change the config.
		 * @param identidier the route to the key
		 * @param value the value to set.
		 * @param visibleForPlayer if the player can see this value.
		 * 
		 * @return true if it worked, false otherwise
		 */
		public static boolean setStringToPlayer(String playerName, String identifier, String value, boolean visibleForPlayer){
			return setValueToPlayer(playerName, identifier, value, visibleForPlayer);
		}
		
		
		/**
		 * Sets a Boolean value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * 
		 * @param playerName the player to change the config.
		 * @param identidier the route to the key
		 * @param value the value to set.
		 * @param visibleForPlayer if the player can see this value.
		 * 
		 * @return true if it worked, false otherwise
		 */
		public static boolean setBooleanToPlayer(String playerName, String identifier, boolean value, boolean visibleForPlayer){
			return setValueToPlayer(playerName, identifier, value, visibleForPlayer);
		}
		
		
		/**
		 * Sets the object to the specific Player. If not found, a new one is created.
		 * If stored value can not be stored, because it is not supported, false is returned.
		 * 
		 * @param playerName to save to
		 * @param identifier as display name
		 * @param value to save
		 * @param visibleForPlayer if the player can see it
		 * 
		 * @return true if worked, false otherwise
		 */
		private static boolean setValueToPlayer(String playerName, String identifier, Object value, boolean visibleForPlayer){
			MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName);
			if(config == null){
				return false;
			}
			
			if(!config.containsValue(identifier)){
				return config.addOption(identifier, identifier, value, value, visibleForPlayer);
			}else{
				return config.setValue(identifier, value);
			}
		}
	}
	
	/**
	 * This is the getter Part of the Configuration API.
	 * Here you can get saved values.
	 * 
	 * @author Tobiyas
	 *
	 */
	public static class Get{
		
		/**
		 * Gets an Int value From the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * Returns {@link Integer#MIN_VALUE} if not found.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 * 
		 * @return the Integer value looked for, or {@link Integer#MIN_VALUE} if not found
		 */
		public static int getIntFromPlayer(String playerName, String identifier){
			Object value = getObjectFromPlayer(playerName, identifier, null);
			if(value == null || !(value instanceof Integer)){
				return Integer.MIN_VALUE;
			}
			
			return (Integer) value;
		}
		
		
		/**
		 * Sets a Double value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * Returns {@link Double#MIN_VALUE} if not found.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 * 
		 * @return the Double looked for, or {@link Double#MIN_VALUE} if not found.
		 */
		public static double getDoubleFromPlayer(String playerName, String identifier){
			Object value = getObjectFromPlayer(playerName, identifier, null);
			if(value == null || !(value instanceof Double)){
				return Double.MIN_VALUE;
			}
			
			return (Double) value;
		}
		
		/**
		 * Sets a String value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * Return "" (empty String) if not found.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 * 
		 * @return the String looked for, or "" if not found.
		 */
		public static String getStringFromPlayer(String playerName, String identifier){
			Object value = getObjectFromPlayer(playerName, identifier, null);
			if(value == null || !(value instanceof String)){
				return "";
			}
			
			return (String) value;
		}
		
		
		/**
		 * Gets a Boolean value from the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * Returns false if not found.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 * 
		 * @return the boolean looked for, false if not found
		 */
		public static boolean getBooleanToPlayer(String playerName, String identifier){
			Object value = getObjectFromPlayer(playerName, identifier, null);
			if(value == null || !(value instanceof Boolean)){
				return false;
			}
			
			return (Boolean) value;
		}
		
		
		/**
		 * Gets the Object stored for a specific player.
		 * Returns the default Value if not found. Null as default is supported.
		 * 
		 * @param playerName to get the value from
		 * @param identifier to search for
		 * @param defaultValue to return if not worked
		 * 
		 * @return the searched Value or the defaultValue if not found.
		 */
		private static Object getObjectFromPlayer(String playerName, String identifier, Object defaultValue){
			MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerName);
			if(config == null || !config.containsValue(identifier)){
				return defaultValue;
			}
			
			return config.getValueDisplayName(identifier);
		}
		
		
		//Gets with default Values:
		
		/**
		 * Gets an Int value From the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * Returns default Value if not found.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 * @param defaultValue returned if not found
		 * 
		 * @return the Integer value looked for, or the defaultValue if not found
		 */
		public static int getIntFromPlayer(String playerName, String identifier, int defaultValue){
			Object value = getObjectFromPlayer(playerName, identifier, null);
			if(value == null || !(value instanceof Integer)){
				return defaultValue;
			}
			
			return (Integer) value;
		}
		
		
		/**
		 * Sets a Double value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * Returns default Value if not found.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 * @param defaultValue returned if not found
		 * 
		 * @return the Double looked for, or the defaultValue if not found.
		 */
		public static double getDoubleFromPlayer(String playerName, String identifier, double defaultValue){
			Object value = getObjectFromPlayer(playerName, identifier, null);
			if(value == null || !(value instanceof Double)){
				return defaultValue;
			}
			
			return (Double) value;
		}
		
		/**
		 * Sets a String value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * Returns default Value if not found.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 * @param defaultValue returned if not found
		 * 
		 * @return the String looked for, or the defaultValue if not found.
		 */
		public static String getStringFromPlayer(String playerName, String identifier, String defaultValue){
			Object value = getObjectFromPlayer(playerName, identifier, null);
			if(value == null || !(value instanceof String)){
				return defaultValue;
			}
			
			return (String) value;
		}
		
		
		/**
		 * Gets a Boolean value from the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * Returns default Value if not found.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 * @param defaultValue returned if not found
		 * 
		 * @return the boolean looked for, or the defaultValue if not found
		 */
		public static boolean getBooleanToPlayer(String playerName, String identifier, boolean defaultValue){
			Object value = getObjectFromPlayer(playerName, identifier, null);
			if(value == null || !(value instanceof Boolean)){
				return defaultValue;
			}
			
			return (Boolean) value;
		}
		
	}
}