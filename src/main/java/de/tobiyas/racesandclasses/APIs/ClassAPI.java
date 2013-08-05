package de.tobiyas.racesandclasses.APIs;

import java.util.List;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;

public class ClassAPI {

	/**
	 * The plugin to get the ClassManager from
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Returns the Class of a player.
	 * If the player has no Class, the Default Class is returned.
	 * 
	 * @param playerName to search
	 * @return the {@link RaceContainer} of the player
	 */
	public static ClassContainer getClassOfPlayer(String playerName){
		ClassManager classManager = plugin.getClassManager();
		ClassContainer clazz = (ClassContainer) classManager.getHolderOfPlayer(playerName);
		if(clazz != null){
			return clazz;
		}else{
			return (ClassContainer) classManager.getDefaultHolder();
		}
	}
	
	
	/**
	 * Returns the {@link ClassContainer} by the name.
	 * If the Class is not found, Null is returned.
	 * 
	 * @param className to search
	 * @return the Class corresponding to the name
	 */
	public static ClassContainer getClassByName(String className){
		ClassManager classManager = plugin.getClassManager();
		return (ClassContainer) classManager.getHolderByName(className);
	}

	
	/**
	 * Returns a List of all Class names available
	 * 
	 * @return list of Class names
	 */
	public static List<String> getAllClassNames(){
		return plugin.getClassManager().getAllHolderNames();
	}
	
	
	/**
	 * Gives the passed Player a Class.
	 * If he already has one, the Class is changed to the new one.
	 * 
	 * Returns true on success, false if:
	 *  - playerName can not be found on Bukkit.getPlayer(playerName).
	 *  - the new className is not found.
	 *  - any internal error occurs.
	 * 
	 * @param playerName the player that the Class should be changed.
	 * @param className to change to
	 * @return true if worked, false otherwise
	 */
	public static boolean addPlayerToClass(String playerName, String className){
		if(Bukkit.getPlayer(playerName) == null) return false;
		
		ClassManager manager = plugin.getClassManager();
		ClassContainer wantedClass = (ClassContainer) manager.getHolderByName(className);
		if(wantedClass == null){
			return false;
		}
		
		return manager.changePlayerHolder(playerName, className);
	}
}
