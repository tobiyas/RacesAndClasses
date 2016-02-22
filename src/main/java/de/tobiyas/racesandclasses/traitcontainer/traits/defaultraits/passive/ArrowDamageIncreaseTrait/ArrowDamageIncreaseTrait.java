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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ArrowDamageIncreaseTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
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

public class ArrowDamageIncreaseTrait extends AbstractPassiveTrait {
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "ArrowDamageIncreaseTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return "damage: " + operation + " " + value;
	}

	
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "operation", classToExpect = String.class, optional = false), 
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class, optional = false)
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
		if(!(event instanceof EntityDamageByEntityEvent)) return TraitResults.False();
		
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		if(Eevent.getDamager().getType() != EntityType.ARROW) return TraitResults.False();
		
		Arrow arrow = (Arrow) Eevent.getDamager();
		LivingEntity shooter = CompatibilityModifier.Shooter.getShooter(arrow);
		if(shooter == null 
				|| shooter.getType() != EntityType.PLAYER) return TraitResults.False();
		
		Player playerShooter = (Player) shooter;
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(playerShooter);
		if(TraitHolderCombinder.checkContainer(racPlayer, this)){
			double oldDamage = CompatibilityModifier.EntityDamage.safeGetDamage(Eevent);
			double newValue = getNewValue(racPlayer, oldDamage, "value");
			
			CompatibilityModifier.EntityDamage.safeSetDamage(newValue, Eevent);
			
			return TraitResults.True();
		}
		return TraitResults.False();
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}

	
	@TraitInfos(category="passive", traitName="ArrowDamageIncreaseTrait", visible=true)
	@Override
	public void importTrait(){
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait increases the damage of your Arrows.");
		return helpList;
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() != PlayerAction.DO_DAMAGE) return false;
		if(!wrapper.isArrowInvolved()) return false;

		return true;
	}

}
