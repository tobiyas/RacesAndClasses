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
package de.tobiyas.racesandclasses.util.permissions;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class VaultHookProvider  {

	/**
	 * The Hook instance.
	 */
	private static VaultHook hook;
	
	/* Private Constructor, because this is only a provider. */
	private VaultHookProvider() {}
	
	
	/**
	 * Returns the Vault hook
	 * <br>This is a lazy init.
	 * 
	 * @return the Vault Hook
	 */
	public static VaultHook getHook(){
		if(hook == null){
			init(RacesAndClasses.getPlugin());
		}
		
		return hook;
	}
	
	/**
	 * Shuts down the Vault hook
	 */
	public static void shutdown(){
		if(hook != null){
			hook.shutDown();
		}
	}



	/**
	 * Inits Vault if it is present.
	 * 
	 * @param racesAndClasses to register to.
	 * @return
	 */
	public static void init(RacesAndClasses racesAndClasses) {
		//if vault is not present, we can't use it.
		if(Bukkit.getPluginManager().getPlugin("Vault") == null) return;
		
		if(hook == null) hook = new VaultHook(racesAndClasses);
	}
}
