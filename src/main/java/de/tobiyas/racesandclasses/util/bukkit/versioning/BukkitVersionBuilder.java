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

import org.bukkit.Bukkit;

public class BukkitVersionBuilder {

	/**
	 * Returns the build number of the CraftBukkit build.
	 * 
	 * If it could not be read, {@link BukkitVersion#empty()} is returned.
	 * @return
	 */
	public static BukkitVersion getbukkitBuildNumber(){
		String bukkitVersion = Bukkit.getServer().getBukkitVersion();
		String[] bukkitVersionSplit = bukkitVersion.split("-");
		if(bukkitVersionSplit.length < 2){
			return BukkitVersion.empty();
		}
		
		String[] minecraftVersionSplit = bukkitVersionSplit[0].split("\\.");
		if(minecraftVersionSplit.length < 3){
			if(minecraftVersionSplit.length < 2) return BukkitVersion.empty();
			
			String[] newArray = new String[3];
			newArray[0] = minecraftVersionSplit[0];
			newArray[1] = minecraftVersionSplit[1];
			newArray[2] = "0";
			
			minecraftVersionSplit = newArray;
		}
		
		String[] revisionVersionSplit = bukkitVersionSplit[1].split("\\.");
		if(revisionVersionSplit.length < 2){
			return BukkitVersion.empty();
		}
		
		try{
			int bukkitMainVersion = Integer.parseInt(minecraftVersionSplit[0]);
			int bukkitSubVersion = Integer.parseInt(minecraftVersionSplit[1]);
			int bukkitSubSubVersion = Integer.parseInt(minecraftVersionSplit[2]);
			
			int revisionMainVersion = Integer.parseInt(revisionVersionSplit[0].replace("R", ""));
			int revisionSubVersion = Integer.parseInt(revisionVersionSplit[1]);
			
			return new BukkitVersion(bukkitMainVersion, bukkitSubVersion, bukkitSubSubVersion, revisionMainVersion, revisionSubVersion);
		}catch(NumberFormatException exp){
			return BukkitVersion.empty();
		}
	}

}
