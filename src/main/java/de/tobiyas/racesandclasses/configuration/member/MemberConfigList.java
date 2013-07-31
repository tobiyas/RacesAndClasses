package de.tobiyas.racesandclasses.configuration.member;

import java.util.LinkedList;

public class MemberConfigList extends LinkedList<ConfigOption> {
	private static final long serialVersionUID = 3484898002881544703L;

	
	/**
	 * checks if the value is found in the list.
	 * It searches for the DisplayName of an {@link ConfigOption}
	 * 
	 * @param displayName
	 * @return
	 */
	public boolean contains(String displayName){
		return getConfigOptionByDisplayName(displayName) != null;
	}

	
	/**
	 * checks if the value is found in the list.
	 * It searches for the Path of an {@link ConfigOption}
	 * 
	 * @param displayName
	 * @return
	 */
	public boolean containsPathName(String pathName){
		return getConfigOptionByPath(pathName) != null;
	}

	/**
	 * Searches for the Config with the display name passed.
	 * Returns the correct {@link ConfigOption} or Null if not found.
	 * 
	 * @param displayName to search for
	 * 
	 * @return the searched config or NULL
	 */
	public ConfigOption getConfigOptionByDisplayName(String displayName) {
		for(ConfigOption option : this){
			if(option.getDisplayName().equalsIgnoreCase(displayName)){
				return option;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Searches for the Config with the path name passed.
	 * Returns the correct {@link ConfigOption} or Null if not found.
	 * 
	 * @param pathName to search for
	 * 
	 * @return the searched config or NULL
	 */
	public ConfigOption getConfigOptionByPath(String pathName) {
		for(ConfigOption option : this){
			if(option.getPath().equalsIgnoreCase(pathName)){
				return option;
			}
		}
		
		return null;
	}
}