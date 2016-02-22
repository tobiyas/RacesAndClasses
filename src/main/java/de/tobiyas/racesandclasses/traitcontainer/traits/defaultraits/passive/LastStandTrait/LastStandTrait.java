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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.LastStandTrait;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfig;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
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
import de.tobiyas.racesandclasses.util.traitutil.TraitPriority;

public class LastStandTrait extends AbstractPassiveTrait  {
	
	private static double activationLimit = 30;
	private double value = 5;
	private final DecimalFormat format = new DecimalFormat("0.0");
	
	public LastStandTrait(){
	}
	
	
	@TraitEventsUsed(registerdClasses = {EntityDamageEvent.class}, traitPriority = TraitPriority.lowest)
	@Override
	public void generalInit() {
		TraitConfig config = plugin.getConfigManager().getTraitConfigManager().getConfigOfTrait(getName());
		if(config != null){
			activationLimit = config.getDouble("trait.activationLimit", 30);
		}	
	}

	@Override
	public String getName() {
		return "LastStandTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return " uplink-Time: " + cooldownTime + " secs, heals: " + format.format(value);
	}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class, optional = true),
			@TraitConfigurationField(fieldName = "activationLimit", classToExpect = Double.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("value")){
			value = configMap.getAsDouble("value");
		}
	}

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		Entity entity = Eevent.getEntity();
		
		Player player = (Player) entity;
		RaCPlayer racPlayer = eventWrapper.getPlayer();
		
		double health = racPlayer.getHealth() - Eevent.getDamage();
		double maxHealth = racPlayer.getMaxHealth();
		double percent = 100 * health / maxHealth;
		
		if(percent <= activationLimit){
			double modValue = modifyToPlayer(racPlayer, value, "value");
			
			EntityHealEvent ehEvent = new EntityHealEvent(player, modValue, RegainReason.MAGIC);
			TraitEventManager.fireEvent(ehEvent);
			
			double amount = CompatibilityModifier.EntityHeal.safeGetAmount(ehEvent);
			
			if(!ehEvent.isCancelled() && amount > 0){
				double healthBefore = CompatibilityModifier.BukkitPlayer.safeGetHealth(player);
				double newHealth = healthBefore + amount;
				
				if(newHealth > maxHealth){
					newHealth = maxHealth;
					amount = newHealth - healthBefore;
				}
				
				CompatibilityModifier.BukkitPlayer.safeSetHealth(newHealth, player);
				
				LanguageAPI.sendTranslatedMessage(player, Keys.trait_laststand_success, 
						"name", getDisplayName(), 
						"value", format.format(amount));
				
				Location loc = player.getLocation();
				player.getWorld().playEffect(loc, Effect.POTION_BREAK, 1);

				return TraitResults.True();
			}
		}

		return TraitResults.False();
	}
	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof LastStandTrait)) return false;
		LastStandTrait otherTrait = (LastStandTrait) trait;
		
		return value >= otherTrait.value;
	}
	
	@TraitInfos(category="passive", traitName="LastStandTrait", visible=true)
	@Override
	public void importTrait() {
	}


	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait heals you if you drop below " + activationLimit + "% health.");
		helpList.add(ChatColor.YELLOW + "This can only happen every X seconds.");
		return helpList;
	}


	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		Entity entity = Eevent.getEntity();
		
		if(!(entity instanceof Player)) return false;
		RaCPlayer racPlayer = wrapper.getPlayer();
		
		double health = racPlayer.getHealth() - Eevent.getDamage();
		double maxHealth = racPlayer.getMaxHealth();
		double percent = 100 * health / maxHealth;
		
		if(percent <= activationLimit){
			return true;
		}

		return false;
	}


	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		//Not needed
		return false;
	}
	
	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		return false;
	}
	
}
