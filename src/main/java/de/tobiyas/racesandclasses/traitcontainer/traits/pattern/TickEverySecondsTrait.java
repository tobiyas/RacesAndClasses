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
package de.tobiyas.racesandclasses.traitcontainer.traits.pattern;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public abstract class TickEverySecondsTrait extends AbstractBasicTrait {

	
private int schedulerTaskId = -1;
	
	/**
	 * The Seconds when this is fired.
	 */
	protected int seconds = 1;
	
	
	@TraitEventsUsed(registerdClasses = {  })
	@Override
	public void generalInit() {
		schedulerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask((JavaPlugin)plugin, new Runnable() {
			
			@Override
			public void run() {
				for(OfflinePlayer player : holder.getHolderManager().getAllPlayersOfHolder(holder)){
					if(player == null || !player.isOnline()) return;
					EventWrapper fakeEventWrapper = EventWrapperFactory.buildOnlyWithplayer(player.getPlayer());
					if(!checkRestrictions(fakeEventWrapper) || !canBeTriggered(fakeEventWrapper)) {
						restrictionsFailed(player.getPlayer());
						return;
					}
						
					if(tickDoneForPlayer(player.getPlayer())){
						plugin.getStatistics().traitTriggered(TickEverySecondsTrait.this);
					}
				}
				
				
			}
		}, seconds * 20, seconds * 20);
	}
	
	/**
	 * Notifies that the restrictions failed.
	 * <br>NOTICE: To be overriden
	 * 
	 * @param player to check
	 */
	protected void restrictionsFailed(Player player) {}

	/**
	 * Is called when the Tick for the Player is on it's way.
	 * 
	 * @param player The tick for the Player is done.
	 * 
	 * @return true if it triggered, false otherwise.
	 */
	protected abstract boolean tickDoneForPlayer(Player player);
	
	
	@Override
	public void deInit(){
		Bukkit.getScheduler().cancelTask(schedulerTaskId);
	}


	@Override
	protected String getPrettyConfigIntern() {
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
		
		return " every: " + seconds + " sec for " + reason;
	}
	
	/**
	 * Returns the Pre before the restrictions come.
	 * @return
	 */
	protected abstract String getPrettyConfigurationPre();

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "seconds", classToExpect = Integer.class, optional = false)
		})
	@Override
	public void setConfiguration(Map<String, Object> configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		seconds = (Integer) configMap.get("seconds");
	}

	@Override	
	public TraitResults trigger(EventWrapper eventWrapper) {
		//Not needed
		return new TraitResults();
	}

	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if (!(trait instanceof TickEverySecondsTrait))
			return false;
		TickEverySecondsTrait otherTrait = (TickEverySecondsTrait) trait;

		return seconds >= otherTrait.seconds;
	}
	
	@Override
	public boolean isStackable(){
		return true;
	}


	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Player player = wrapper.getPlayer();
		int lightFromSky = player.getLocation().getBlock().getLightFromSky();
		if(onlyOnDay){ //TODO fixme
			if(lightFromSky > 2){
				return false;
			}
		}

		return true;
	}
}
