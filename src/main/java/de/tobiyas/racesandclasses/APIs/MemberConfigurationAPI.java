package de.tobiyas.racesandclasses.APIs;

import de.tobiyas.racesandclasses.RacesAndClasses;

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
		 * @param identidier the route to the key
		 * @param value the value to set.
		 * @param visibleForPlayer if the player can see this value.
		 */
		public static void setIntToPlayer(String playerName, String identidier, int value, boolean visibleForPlayer){
			
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
		 */
		public static void setDoubleToPlayer(String playerName, String identifier, double value, boolean visibleForPlayer){
			
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
		 */
		public static void setStringToPlayer(String playerName, String identifier, String value, boolean visibleForPlayer){
			
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
		 */
		public static void setBooleanToPlayer(String playerName, String identifier, boolean value, boolean visibleForPlayer){
			
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
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 */
		public static int getIntFromPlayer(String playerName, String identifier){
			
			return -1;
		}
		
		
		/**
		 * Sets a Double value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 */
		public static double getDoubleFromPlayer(String playerName, String identifier){
			
			return -1;
		}
		
		/**
		 * Sets a String value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 */
		public static String getStringFromPlayer(String playerName, String identifier){
			
			return "";
		}
		
		
		/**
		 * Sets a Boolean value to the player Config.
		 * If visibleForPlayer is true, the player can see 
		 * this value when inspection his configuration.
		 * 
		 * If it is false, he can't see it.
		 * 
		 * @param playerName the player to change the config.
		 * @param identifier the route to the key.
		 */
		public static boolean setBooleanToPlayer(String playerName, String identifier){
			
			return false;
		}
	}
}