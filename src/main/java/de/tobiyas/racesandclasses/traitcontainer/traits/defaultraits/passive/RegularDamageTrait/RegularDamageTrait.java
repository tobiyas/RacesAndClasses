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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.RegularDamageTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class RegularDamageTrait extends AbstractBasicTrait {

	
	private int schedulerTaskId = -1;
	
	/**
	 * The Seconds when this is fired.
	 */
	private int seconds = 1;
	
	/**
	 * The Damage done if set correct
	 */
	private double damage = 0;
	
	
	@TraitEventsUsed(registerdClasses = {  })
	@Override
	public void generalInit() {
		schedulerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask((JavaPlugin)plugin, new Runnable() {
			
			@Override
			public void run() {
				for(AbstractTraitHolder holder : RegularDamageTrait.this.getTraitHolders()){
					for(RaCPlayer player : holder.getHolderManager().getAllPlayersOfHolder(holder)){
						EventWrapper wrapper = EventWrapperFactory.buildOnlyWithplayer(player.getPlayer());
						if(player != null && wrapper != null
								&& checkRestrictions(wrapper) != TraitRestriction.None
								&& canBeTriggered(wrapper)){
							
							EntityDamageEvent damageEvent = 
									CompatibilityModifier.EntityDamage.safeCreateEvent(player.getPlayer(), DamageCause.MAGIC, damage);
							
							Bukkit.getPluginManager().callEvent(damageEvent);
							if(!damageEvent.isCancelled()){
								player.getPlayer().damage(CompatibilityModifier.EntityDamage.safeGetDamage(damageEvent));							
							}
							
							plugin.getStatistics().traitTriggered(RegularDamageTrait.this);
							
						}
					}
				}
				
				
			}
		}, seconds * 20, seconds * 20);
	}
	
	@Override
	public void deInit(){
		Bukkit.getScheduler().cancelTask(schedulerTaskId);
	}

	@Override
	public String getName() {
		return "RegularDamageTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		String reason = "Nothing";
		if(onlyInLava){
			reason = "in Lava";
		}

		if(onlyInWater){
			reason = "in Water";
		}

		if(onlyOnLand){
			reason = "on Land";
		}

		if(onlyOnDay && !onlyInNight){
			reason = "in NightShine";
		}
		
		if(onlyInNight && !onlyOnDay){
			reason = "on DayLight";
		}
		
		return "Damage: " + damage + " every: " + seconds + " sec for " + reason;
	}

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "seconds", classToExpect = Integer.class), 
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		seconds = (Integer) configMap.get("seconds");
		damage = (Double) configMap.get("damage");
	}

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   
		//Not needed
		return TraitResults.True();
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait does damage when the Preconditions are correct.");
		return helpList;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if (!(trait instanceof RegularDamageTrait))
			return false;
		RegularDamageTrait otherTrait = (RegularDamageTrait) trait;

		return damage >= otherTrait.damage;
	}
	
	@Override
	public boolean isStackable(){
		return true;
	}

	@TraitInfos(category = "passive", traitName = "RegularDamageTrait", visible = true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		int lightFromSky = wrapper.getPlayer().getLocation().getBlock().getLightFromSky();
		if(onlyOnDay){ //TODO fixme
			if(lightFromSky > 2){
				return false;
			}
		}

		return true;
	}
}
