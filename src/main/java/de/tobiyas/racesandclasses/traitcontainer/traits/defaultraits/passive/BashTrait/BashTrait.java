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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.BashTrait;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.StunAPI;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.passive.AbstractPassiveTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class BashTrait extends AbstractPassiveTrait{
	
	/**
	 * The Duration in seconds.
	 */
	private double duration = 1;
	
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "BashTrait";
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		return "Stun Chance: " + value;
	}

	@TraitConfigurationNeeded(fields = {
					@TraitConfigurationField(fieldName = "chance", classToExpect = Double.class, optional = false),
					@TraitConfigurationField(fieldName = "duration", classToExpect = Double.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		value = (Double) configMap.get("chance");
		
		if(configMap.containsKey("duration")) {
			this.duration = (Double) configMap.get("duration");
		}
	}
	

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		double num = new Random().nextDouble() * 100;
		if(num <= modifyToPlayer(eventWrapper.getPlayer(), value, "chance")){
			Eevent.setCancelled(true);
			Player player = (Player) Eevent.getDamager();
			LivingEntity target = (LivingEntity) Eevent.getEntity();
			Entity damager = Eevent.getDamager();
			
			int ticks = (int) Math.floor(duration * 20d);
			StunAPI.StunEntity.stunEntityForTicks(damager, target, ticks);
			
			String targetName = target instanceof Player ? ((Player)target).getName() : target.getType().name().toLowerCase();
			LanguageAPI.sendTranslatedMessage(player, Keys.trait_bash_success, "name", targetName);
		}
		
		return TraitResults.True();
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Your have the chance to Stun an enemy for short time.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof BashTrait)) return false;
		BashTrait otherTrait = (BashTrait) trait;
		
		return value >= otherTrait.value;
	}
	
	@TraitInfos(category="passive", traitName="BashTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() != PlayerAction.DO_DAMAGE) return false;
		
		DamageCause cause = wrapper.getDamageCause();
		return cause == DamageCause.ENTITY_ATTACK;
	}

	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		return false;
	}
	
	
}
