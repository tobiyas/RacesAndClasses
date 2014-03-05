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
package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

import org.bukkit.entity.Player;

public interface ContinousCostMagicTrait {

	/**
	 * Returns the time every how many seconds the trait is checked.
	 * 
	 * @return every how many seconds the trait drains the stuff.
	 */
	public int everyXSeconds();
	
	
	/**
	 * Activates the Trait.
	 * 
	 * @param player the player to activate to
	 * @return true if activated, false otherwise.
	 */
	public boolean activate(Player player);
	
	
	/**
	 * Deactivates the Trait.
	 * 
	 * @param player the player to deactivate
	 * @return true if deactivated, false otherwise.
	 */
	public boolean deactivate(Player player);
	
	
	/**
	 * Return true if the Trait is activated.
	 * 
	 * @param player the player to check if active
	 * @return false if not.
	 */
	public boolean isActivated(Player player);
}
