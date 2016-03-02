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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.DodgeTrait;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
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

public class DodgeTrait extends AbstractPassiveTrait{
	
	
	@TraitEventsUsed(registerdClasses = {EntityDamageEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "DodgeTrait";
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		return "dodgeChance: " + value;
	}

	@TraitConfigurationNeeded(fields = {
					@TraitConfigurationField(fieldName = "chance", classToExpect = Double.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		value = configMap.getAsDouble("chance");
	}
	

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		double num = new Random().nextDouble() * 100;
		if(num <= modifyToPlayer(eventWrapper.getPlayer(), value, "chance")){
			Eevent.setCancelled(true);
			Player player = (Player) Eevent.getEntity();
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 10, 1);
			
			LanguageAPI.sendTranslatedMessage(player, Keys.trait_dodged);
		}
		
		return TraitResults.True();
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Your have the chance to Dodge an attack.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof DodgeTrait)) return false;
		DodgeTrait otherTrait = (DodgeTrait) trait;
		
		return value >= otherTrait.value;
	}
	
	@TraitInfos(category="passive", traitName="DodgeTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() != PlayerAction.TAKE_DAMAGE) return false;
		
		DamageCause cause = wrapper.getDamageCause();
		return (cause == DamageCause.ENTITY_ATTACK
				|| cause == DamageCause.PROJECTILE);
	}

	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		return false;
	}
	
	
}
