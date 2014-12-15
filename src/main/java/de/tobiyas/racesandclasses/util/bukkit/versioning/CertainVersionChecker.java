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
package de.tobiyas.racesandclasses.util.bukkit.versioning;

public class CertainVersionChecker {

	/**
	 * Checks if the Bukkit version is above the passed version
	 * 
	 * @param version to check for
	 * @return true if is above, false otherwise
	 */
	public static boolean bukkitVersionIsAbove(int mainVersion, int subVersion, int subSubVersion, int mainRevision, int subRevision){
		BukkitVersion version = BukkitVersionBuilder.getbukkitBuildNumber();
		BukkitVersion checkVersion = new BukkitVersion(mainVersion, subVersion, subSubVersion, mainRevision, subRevision);
		
		return version.isGreater(checkVersion);
	}
	
	
	
	/**
	 * Checks if the Bukkit version is above Bukkit 1.6
	 * 
	 * @return true if Bukkit version is above 1.7
	 */
	public static boolean isAbove1_6(){
		return bukkitVersionIsAbove(1, 6, 0, 0, 0);
	}



	/**
	 * Checks if the Bukkit version is above Bukkit 1.7
	 * 
	 * @return true if Bukkit version is above 1.7
	 */
	public static boolean isAbove1_7(){
		return bukkitVersionIsAbove(1, 7, 0, 0, 0);
	}
	
	
	/**
	 * Checks if the Bukkit version is above Bukkit 1.8
	 * 
	 * @return true if Bukkit version is above 1.8
	 */
	public static boolean isAbove1_8(){
		return bukkitVersionIsAbove(1, 8, 0, 0, 0);
	}
}
