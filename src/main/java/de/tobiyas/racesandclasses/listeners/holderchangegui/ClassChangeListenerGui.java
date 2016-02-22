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
package de.tobiyas.racesandclasses.listeners.holderchangegui;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class ClassChangeListenerGui extends HolderChangeListenerGui {

	
	public ClassChangeListenerGui(){
		super(plugin.getClassManager());
	}

	
	@Override
	protected HolderPreSelectEvent generateHolderSelectEvent(RaCPlayer player,
			AbstractTraitHolder newHolder) {
		
		return new PreClassSelectEvent(player.getPlayer(), (ClassContainer) newHolder);
	}

	
	@Override
	protected HolderPreSelectEvent generateHolderChangeEvent(RaCPlayer player,
			AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		
		return new PreClassChangeEvent(player.getPlayer(), (ClassContainer) newHolder, (ClassContainer) oldHolder);
	}
	
}
