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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.SpecificRegenerationTrait;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.TickEverySecondsTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class SpecificRegenerationTrait extends TickEverySecondsTrait {

	
	/**
	 * The Damage done if set correct
	 */
	private double heal = 0;
	
	
	@TraitEventsUsed(registerdClasses = {  })
	@Override
	public void generalInit() {
		super.generalInit();
	}
	
	@Override
	public void deInit(){
		super.deInit();
	}

	@Override
	public String getName() {
		return "SpecificRegenerationTrait";
	}


	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "health", classToExpect = Double.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		heal = configMap.getAsDouble("health");
	}


	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait heals when the Preconditions are correct.");
		return helpList;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if (!(trait instanceof SpecificRegenerationTrait))
			return false;
		SpecificRegenerationTrait otherTrait = (SpecificRegenerationTrait) trait;

		return heal >= otherTrait.heal;
	}
	
	@Override
	public boolean isStackable(){
		return true;
	}

	@TraitInfos(category = "passive", traitName = "SpecificRegenerationTrait", visible = true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		RaCPlayer player = wrapper.getPlayer();
		
		int lightFromSky = player.getLocation().getBlock().getLightFromSky();
		if(onlyOnDay){ //TODO fixme
			if(lightFromSky > 2){
				return false;
			}
		}

		return true;
	}

	@Override
	protected boolean tickDoneForPlayer(RaCPlayer player) {
        double modHeal = modifyToPlayer(player, heal, "health");
        EntityHealEvent regainHealthEvent = 
                CompatibilityModifier.EntityHeal.safeGenerate(player.getPlayer(), modHeal, RegainReason.REGEN);
        
        Bukkit.getPluginManager().callEvent(regainHealthEvent);
        if(!regainHealthEvent.isCancelled()){
            double newHealValue = CompatibilityModifier.EntityRegainHealth.safeGetAmount(regainHealthEvent);
            CompatibilityModifier.BukkitPlayer.safeHeal(newHealValue, player.getPlayer());
        }

        return true;
	}
	
	
	private final DecimalFormat format = new DecimalFormat("0.#");

	@Override
	protected String getPrettyConfigurationPre() {
		return "Heals " + format.format(heal);
	}
}
