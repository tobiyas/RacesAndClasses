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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.DamageReduceTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
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

public class DamageReduceTrait extends AbstractPassiveTrait{
	
	
	@TraitEventsUsed(registerdClasses = {EntityDamageEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "DamageReduceTrait";
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		return "damage reduce: " + operation + " " + value;
	}

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "operation", classToExpect = String.class), 
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		operation = (String) configMap.get("operation");
		value = (Double) configMap.get("value");
	}
	

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		if(!(event instanceof EntityDamageEvent)) return TraitResults.False();
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return TraitResults.False();
		
		double oldValue = CompatibilityModifier.EntityDamage.safeGetDamage(Eevent);
		double newValue = getNewValue(eventWrapper.getPlayer(), oldValue, "value");
		
		CompatibilityModifier.EntityDamage.safeSetDamage(newValue, Eevent);			
		return TraitResults.True();
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Your taken damage will be decreased or divided by an value.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof DamageReduceTrait)) return false;
		DamageReduceTrait otherTrait = (DamageReduceTrait) trait;
		
		return value >= otherTrait.value;
	}
	
	@TraitInfos(category="passive", traitName="DamageReduceTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return false;
		
		return true;
	
	}
}
