/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.RegenerationTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper.RegainResource;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.passive.AbstractPassiveTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class RegenerationTrait extends AbstractPassiveTrait {

	@TraitEventsUsed(registerdClasses = { EntityRegainHealthEvent.class })
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "RegenerationTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "regeneration: " + operation + " " + value;
	}

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "operation", classToExpect = String.class, optional=true), 
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class, optional=true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		operation = configMap.getAsString("operation", "+");
		value = configMap.getAsDouble("value",1);
	}

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		EntityRegainHealthEvent Eevent = (EntityRegainHealthEvent) event;
		double oldValue = CompatibilityModifier.EntityRegainHealth
				.safeGetAmount(Eevent);
		double newValue = getNewValue(eventWrapper.getPlayer(), oldValue, "value");

		CompatibilityModifier.EntityRegainHealth.safeSetAmount(Eevent, newValue);

		return TraitResults.True();
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait increases the life regeneration of a Player.");
		return helpList;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if (!(trait instanceof RegenerationTrait))
			return false;
		RegenerationTrait otherTrait = (RegenerationTrait) trait;

		return value >= otherTrait.value;
	}

	@TraitInfos(category = "passive", traitName = "RegenerationTrait", visible = true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(wrapper.getRegainResource() != RegainResource.HEALTH) return false;
		
		if (wrapper.getRegainReason() == RegainReason.SATIATED) return true;
		return false;
	}
}
