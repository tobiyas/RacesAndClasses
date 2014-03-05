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

public class BukkitVersion {

	private final int bukkitMainVersion;
	private final int bukkitSubVersion;
	private final int bukkitSubSubVersion;
	
	private final int revisionMainVersion;
	private final int revisionSubVersion;
	
	
	private BukkitVersion(){
		this.bukkitMainVersion = -1;
		this.bukkitSubVersion = -1;
		this.bukkitSubSubVersion = -1;
		
		this.revisionMainVersion = -1;
		this.revisionSubVersion = -1;
	}
	
	/**
	 * generate with all fields
	 */
	public BukkitVersion(int bukkitMainVersion, int bukkitSubVersion, int bukkitSubSubVersion, int revisionMainVersion, int revisionSubVersion){
		this.bukkitMainVersion = bukkitMainVersion;
		this.bukkitSubVersion = bukkitSubVersion;
		this.bukkitSubSubVersion = bukkitSubSubVersion;
		
		this.revisionMainVersion = revisionMainVersion;
		this.revisionSubVersion = revisionSubVersion;
	}
	
	
	
	/**
	 * Returns an empty bukkit Version.
	 * All Properties set to -1.
	 * @return
	 */
	public static BukkitVersion empty() {
		return new BukkitVersion();
	}

	public int getBukkitMainVersion() {
		return bukkitMainVersion;
	}

	public int getBukkitSubVersion() {
		return bukkitSubVersion;
	}

	public int getBukkitSubSubVersion() {
		return bukkitSubSubVersion;
	}

	public int getRevisionMainVersion() {
		return revisionMainVersion;
	}

	public int getRevisionSubVersion() {
		return revisionSubVersion;
	}

	
	/**
	 * Returns a related number to the Bukkit build.
	 * 
	 * @return
	 */
	public int getTotalNumber(){
		return revisionSubVersion + (revisionMainVersion * 10) + (bukkitSubSubVersion * 100) 
				+ (bukkitSubVersion * 1000) + (bukkitMainVersion * 10000);
	}

	
	/**
	 * Returns true if the version is greater than the passed Version.
	 * 
	 * @param checkVersion
	 * @return
	 */
	public boolean isGreater(BukkitVersion checkVersion) {
		return getTotalNumber() > checkVersion.getTotalNumber();
	}
}
