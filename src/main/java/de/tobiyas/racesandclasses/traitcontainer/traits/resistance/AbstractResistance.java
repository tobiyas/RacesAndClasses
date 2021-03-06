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
package de.tobiyas.racesandclasses.traitcontainer.traits.resistance;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.ResistanceInterface;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.util.traitutil.TraitStringUtils;

public abstract class AbstractResistance extends AbstractBasicTrait implements ResistanceInterface {

	protected List<DamageCause> resistances;
	
	protected double value;
	protected String operation = "";
	
	
	@Override
	public abstract String getName();
	

	@Override
	protected String getPrettyConfigIntern(){
		return operation + " " + value;
	}

	@TraitConfigurationNeeded(fields = {
		@TraitConfigurationField(fieldName = "operation", classToExpect = String.class), 
		@TraitConfigurationField(fieldName = "value", classToExpect = Double.class)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		operation = configMap.getAsString("operation");
		value = configMap.getAsDouble("value");
	}

	@Override	
	public TraitResults trigger(EventWrapper eventWrapper) {
		Event event = eventWrapper.getEvent();
		
		TraitResults result = new TraitResults();
		if(!(event instanceof EntityDamageEvent)) return result.setTriggered(false);
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		Entity entity = Eevent.getEntity();
		if(!(entity instanceof Player)) return result.setTriggered(false);
		if(TraitHolderCombinder.checkContainer(eventWrapper.getPlayer(), this)){
			if(getResistanceTypes().contains(Eevent.getCause())){
				
				//If there is damage * 0, cancel the Event to show no damage effect.
				if(instantCancle()){
					CompatibilityModifier.EntityDamage.safeSetDamage(0, Eevent);
					Eevent.setCancelled(true);
					return result;
				}
				
				double oldDmg = CompatibilityModifier.EntityDamage.safeGetDamage(Eevent);
				double modified = modifyToPlayer(eventWrapper.getPlayer(), value, "absorb");
				double newDmg = TraitStringUtils.getNewValue(oldDmg, operation, modified);
				
				CompatibilityModifier.EntityDamage.safeSetDamage(newDmg, Eevent);
				return result;
			}
		}
		
		return result.setTriggered(false);
	}
	
	/**
	 * Checks if the event should get an instant cancle.
	 * <br>This is when we have '*' as operator and 0 as value.
	 */
	private boolean instantCancle(){
		return operation.equals("*") && value == 0;
	}
	
	@Override
	public List<DamageCause> getResistanceTypes() {
		return resistances;
	}
	
	@Override
	public boolean isBetterThan(Trait trait){
		if(trait.getClass() != this.getClass()) return false;
		AbstractResistance otherTrait = (AbstractResistance) trait;
		
		return value >= otherTrait.value;
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		PlayerAction action = wrapper.getPlayerAction();
		if(action != PlayerAction.TAKE_DAMAGE) return false;

		DamageCause cause = wrapper.getDamageCause();
		if(getResistanceTypes().contains(cause)){
			return true;
		}
		
		return false;
	}
	
	
}
