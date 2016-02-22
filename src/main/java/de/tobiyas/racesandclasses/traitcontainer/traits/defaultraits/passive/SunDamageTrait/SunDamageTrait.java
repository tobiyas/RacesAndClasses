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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.SunDamageTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.TickEverySecondsTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class SunDamageTrait extends TickEverySecondsTrait {

	
	/**
	 * The Damage done if set correct
	 */
	private double damage = 0;
	
	/**
	 * If the player needs to be standing in the Sun
	 */
	private boolean standing = true;
	
	/**
	 * If the trait can kill yourself.
	 */
	private boolean canKill = false;
	

	@Override
	public String getName() {
		return "SunDamageTrait";
	}


	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class),
			@TraitConfigurationField(fieldName = "standing", classToExpect = Boolean.class, optional = true),
			@TraitConfigurationField(fieldName = "canKill", classToExpect = Boolean.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		configMap.put("onlyOnDay", true);
		super.setConfiguration(configMap);
		
		damage = (Double) configMap.get("damage");
		
		if(configMap.containsKey("standing")){
			standing = (Boolean) configMap.get("standing");
		}
		
		if(configMap.containsKey("canKill")){
			canKill = (Boolean) configMap.get("canKill");
		}
		
		onlyOnDay = true;
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait does damage when standing in the sun.");
		return helpList;
	}
	

	@TraitInfos(category = "passive", traitName = "SunDamageTrait", visible = true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(!standing) return true;
		
		//check if standing in sun.
		RaCPlayer player = wrapper.getPlayer();
		int playerYLocation = player.getLocation().getBlockY();
		int highestLocation = player.getWorld().getHighestBlockYAt(player.getLocation());
		if(playerYLocation < highestLocation) return false;

		return true;
	}

	@Override
	protected boolean tickDoneForPlayer(RaCPlayer player) {
		if(!canBeTriggered(EventWrapperFactory.buildOnlyWithplayer(player.getPlayer()))) return false;
		
		EntityDamageEvent damageEvent = CompatibilityModifier.EntityDamage.safeCreateEvent(player.getPlayer(), DamageCause.FIRE, damage);
		plugin.fireEventToBukkit(damageEvent);
		
		if(damageEvent.isCancelled() || damageEvent.getDamage() <= 0){
			return false;
		}
		
		double newValue = CompatibilityModifier.BukkitPlayer.safeGetHealth(player.getPlayer()) - damageEvent.getDamage();
		if(canKill || newValue >= 0){
			CompatibilityModifier.BukkitPlayer.safeDamage(CompatibilityModifier.EntityDamage.safeGetDamage(damageEvent), player.getPlayer());
		}
		return true;
	}

	@Override
	protected String getPrettyConfigurationPre() {
		return "Damage: " + damage;
	}
}
