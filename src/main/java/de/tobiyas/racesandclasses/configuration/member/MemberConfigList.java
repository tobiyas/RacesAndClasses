package de.tobiyas.racesandclasses.configuration.member;

import java.util.LinkedList;

import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;

public class MemberConfigList <T extends ConfigOption> extends LinkedList<T> {
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
		if(displayName == null) return null;
		
		for(T option : this){
			if(displayName.equalsIgnoreCase(option.getDisplayName())){
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
		if(pathName == null) return null;
		
		for(ConfigOption option : this){
			if(pathName.equalsIgnoreCase(option.getPath())){
				return option;
			}
		}
		
		return null;
	}
}