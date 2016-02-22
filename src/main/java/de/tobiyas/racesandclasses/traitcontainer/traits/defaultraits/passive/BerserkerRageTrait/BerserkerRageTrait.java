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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.BerserkerRageTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.MessageScheduleApi;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfig;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.passive.AbstractPassiveTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class BerserkerRageTrait extends AbstractPassiveTrait{
	
	private static int duration = 10;
	private static double activationLimit = 30;
	
	
	@TraitEventsUsed(registerdClasses = {EntityDamageEvent.class})
	@Override
	public void generalInit() {		
		TraitConfig config = plugin.getConfigManager().getTraitConfigManager().getConfigOfTrait(getName());
		if(config != null){
			duration = (Integer) config.getValue("trait.duration", 10);
			activationLimit = config.getDouble("trait.activationLimit", 30);
		}
	}

	@Override
	public String getName() {
		return "BerserkerRageTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "damage increase: " + operation + value + " for: " + duration + " seconds";
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
		//Handle activation
		if(event instanceof EntityDamageEvent){
			Player player = (Player)((EntityDamageEvent) event).getEntity();			
			
			activate(player);
			return TraitResults.False();
		}
		
		if(event instanceof EntityDamageByEntityEvent){			
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
			
			double oldDamage = CompatibilityModifier.EntityDamage.safeGetDamage(Eevent);
			double newValue = getNewValue(eventWrapper.getPlayer(), oldDamage, "value");
			
			CompatibilityModifier.EntityDamage.safeSetDamage(newValue, Eevent);
			return TraitResults.True();
		}
		
		return TraitResults.False();
	}
	
	
	private void activate(Player player){
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_toggled, "name", getDisplayName());		
		MessageScheduleApi.scheduleTranslateMessageToPlayer(player.getName(), duration, Keys.trait_faded, "name", getDisplayName());
	}
	
	
	private boolean checkIfActive(Player player){
		int remainingTime = CooldownApi.getCooldownOfPlayer(player.getName(), "trait." + getName());
		return (remainingTime - cooldownTime) > 0;
	}
	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof BerserkerRageTrait)) return false;
		BerserkerRageTrait otherTrait = (BerserkerRageTrait) trait;
		
		return value >= otherTrait.value;
	}

	@TraitInfos(category="passive", traitName="BerserkerRageTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait activates when your health drops below" + activationLimit + "% health.");
		helpList.add(ChatColor.YELLOW + "For a short time your damage is increased. This can only happen every " 
				+ "X" + " seconds");
		return helpList;
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		if(event instanceof EntityDamageEvent){
			Entity entity = ((EntityDamageEvent) event).getEntity();
			
			if(entity.getType() != EntityType.PLAYER) return false;
			Player player = (Player) entity;
			
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			double maxHealth = racPlayer.getMaxHealth();
			double currentHealth = racPlayer.getHealth();
			double healthPercent = 100 * currentHealth / maxHealth;
			if(healthPercent > activationLimit) return false;
			if(!checkIfActive(player)) return false;
			
			return true;
		}
		
		if(event instanceof EntityDamageByEntityEvent){			
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
			Entity entity = Eevent.getDamager();
			if(entity instanceof Arrow) {
				entity = CompatibilityModifier.Shooter.getShooter((Arrow) entity);
			}
			
			if(entity == null || entity.getType() != EntityType.PLAYER) return false;
			Player player = (Player) entity;
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			
			if(TraitHolderCombinder.checkContainer(racPlayer, this)){
				if(!checkIfActive(player)) return false;
				
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		return false;
	}
	
}
