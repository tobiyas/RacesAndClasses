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
	 * Searches for the ConfigTotal with the display name passed.
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
	 * Searches for the ConfigTotal with the path name passed.
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
