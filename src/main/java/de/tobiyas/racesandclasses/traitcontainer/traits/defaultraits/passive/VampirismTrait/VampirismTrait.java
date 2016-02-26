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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.VampirismTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
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

public class VampirismTrait extends AbstractPassiveTrait{
	
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "VampirismTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return ((int)(value * 100)) + "% regain from Damage";
	}

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		value = configMap.getAsDouble("value");
	}
	
	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		double damage = CompatibilityModifier.EntityDamage.safeGetDamage(Eevent);
		
		boolean isArrow = eventWrapper.isArrowInvolved();
		Player damager = (Player) (isArrow ? 
				((Projectile)Eevent.getDamager()).getShooter() : 
				Eevent.getDamager());
		
		double regain = damage * (modifyToPlayer(eventWrapper.getPlayer(), value, "value"));
		EntityRegainHealthEvent healEvent = new EntityRegainHealthEvent(damager, regain, RegainReason.MAGIC);
		regain = CompatibilityModifier.EntityRegainHealth.safeGetAmount(healEvent);
		
		double before = CompatibilityModifier.BukkitPlayer.safeGetHealth(damager);
		if(regain > 0){
			CompatibilityModifier.BukkitPlayer.safeHeal(regain, damager);
		}else{
			CompatibilityModifier.BukkitPlayer.safeDamage(-regain, damager);
		}
		
		double after = CompatibilityModifier.BukkitPlayer.safeGetHealth(damager);
		if(Math.abs(before - after) > 0.001 && Eevent.getEntity() instanceof LivingEntity){
			LivingEntity damagee = (LivingEntity) Eevent.getEntity();

			damagee.getWorld().playEffect(damagee.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
		}
		
		return TraitResults.True();
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof VampirismTrait)) return false;
		VampirismTrait otherTrait = (VampirismTrait) trait;
		
		return value >= otherTrait.value;
	}

	@TraitInfos(category="passive", traitName="VampirismTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait heals you by percentage of damage you do.");
		return helpList;
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() != PlayerAction.DO_DAMAGE) return false;		
		return true;
	}
	
	
}
