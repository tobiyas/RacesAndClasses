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
package de.tobiyas.racesandclasses.generate.traits;

import java.util.Map;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class VisibleTrait extends AbstractBasicTrait{


	@TraitInfos(category = "test", traitName = "VisibleTrait", visible = true)
	@Override
	public void importTrait() {
		
	}

	@TraitEventsUsed(registerdClasses = {}, traitPriority = 0)
	@Override
	public void generalInit() {
		
	}

	@Override
	public String getName() {
		return "VisibleTrait";
	}

	@Override
	protected String getPrettyConfigIntern() {
		return "Nothing";
	}

	@TraitConfigurationNeeded()
	@Override
	public void setConfiguration(Map<String, Object> configMap) {
	}

	@Override	
	public TraitResults trigger(EventWrapper eventWrapper) {
		return TraitResults.False();
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return true;
	}
}
