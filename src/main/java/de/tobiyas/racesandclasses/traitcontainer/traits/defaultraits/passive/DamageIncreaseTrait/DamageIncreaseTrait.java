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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.DamageIncreaseTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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

public class DamageIncreaseTrait extends AbstractPassiveTrait {
	
	
		
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "DamageIncreaseTrait";
	}
		
	@Override
	protected String getPrettyConfigIntern() {
		return "damage: " + operation + " " +  value;
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
	public TraitResults trigger(EventWrapper eventWrapper) {   
		Event event = eventWrapper.getEvent();
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		Entity entity = Eevent.getDamager();
		if(entity instanceof Arrow)
			entity = CompatibilityModifier.Shooter.getShooter((Arrow)entity);
		
		double oldValue = CompatibilityModifier.EntityDamage.safeGetDamage(Eevent);
		double newValue = getNewValue(eventWrapper.getPlayer(), oldValue, "value");
		
		CompatibilityModifier.EntityDamage.safeSetDamage(newValue, Eevent);
		
		return TraitResults.True();
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Your Damage will be increased by a value or times an value.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof DamageIncreaseTrait)) return false;
		DamageIncreaseTrait otherTrait = (DamageIncreaseTrait) trait;
		
		return value >= otherTrait.value;
	}

	@TraitInfos(category="passive", traitName="DamageIncreaseTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		if(!(event instanceof EntityDamageByEntityEvent)) return false;
		
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		Entity entity = Eevent.getDamager();
		if(entity instanceof Arrow)
			entity = CompatibilityModifier.Shooter.getShooter((Arrow)entity);
		
		
		if(!(entity instanceof Player)) return false;
		return true;
	}
	
}
