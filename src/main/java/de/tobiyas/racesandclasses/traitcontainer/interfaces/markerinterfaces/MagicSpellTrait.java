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

import java.util.UUID;

/**
 * This interface indicates that the Trait is a magic Spell.
 * It indicates that it needs magic points to cast.
 * 
 * @author Tobiyas
 */
public interface MagicSpellTrait extends TraitWithRestrictions, TraitWithCost {
	
	
	/**
	 * Returns the channeling time in Seconds.
	 * <br>Double because of example 1.5 seconds.
	 */
	public double getChannelingTime();

	/**
	 * Notifies that the Channeling got kicked.
	 * @param player the Player that got kicked.
	 */
	public void gotKicked(UUID player);
	
	/**
	 * Returns if the Trait is kickable.
	 */
	public boolean isKickable();
	
}
