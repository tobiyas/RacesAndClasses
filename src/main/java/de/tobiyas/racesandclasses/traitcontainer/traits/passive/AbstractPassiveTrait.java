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
package de.tobiyas.racesandclasses.traitcontainer.traits.passive;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitStringUtils;

public abstract class AbstractPassiveTrait extends AbstractBasicTrait{

	protected AbstractTraitHolder traitHolder;
	
	protected double value;
	protected String operation;
	
	

	/**
	 * Calculates the new value by evaluating the operation
	 * to the old damage value
	 * 
	 * @param oldDmg
	 * @return
	 */
	protected double getNewValue(RaCPlayer player, double oldDmg, String toModify) {
		double modDamage = modifyToPlayer(player, oldDmg, toModify);
		return TraitStringUtils.getNewValue(modDamage, operation, value);
	}
	
}
