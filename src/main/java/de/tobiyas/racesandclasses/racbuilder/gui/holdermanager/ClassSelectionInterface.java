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
package de.tobiyas.racesandclasses.racbuilder.gui.holdermanager;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.racbuilder.AbstractHolderBuilder;
import de.tobiyas.racesandclasses.racbuilder.ClassHolderBuilder;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;

public class ClassSelectionInterface extends HolderSelectionInterface {

	public ClassSelectionInterface(Player player,
			BasicSelectionInterface parent, AbstractHolderManager holderManager, RacesAndClasses plugin) {
		
		super(player, parent, holderManager, plugin);
		
	}

	@Override
	protected AbstractHolderBuilder generateNewHolderBuilder(String name) {
		return new ClassHolderBuilder(name);
	}

	@Override
	protected AbstractHolderBuilder generateHolderBuilderFor(
			AbstractTraitHolder holder) {
		return new ClassHolderBuilder((ClassContainer) holder);
	}

}
