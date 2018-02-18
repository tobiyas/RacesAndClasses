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

import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public abstract class TickEverySecondsTrait extends AbstractBasicTrait {

	
	private BukkitTask schedulerTask;
	
	/**
	 * The Seconds when this is fired.
	 */
	protected int seconds = 1;
	
	
	@TraitEventsUsed(registerdClasses = {  })
	@Override
	public void generalInit() {
		schedulerTask = new DebugBukkitRunnable(getName()) {
			@Override public void runIntern() { tickEverySecondsMethod(); }
		}.runTaskTimer(plugin, seconds * 20, seconds * 20);
	}
	
	
	/**
	 * The actual Tick method.
	 */
	private final void tickEverySecondsMethod() {
		tickEverySecondsAditionalBefore();
		
		for(AbstractTraitHolder holder : holders){
			for(RaCPlayer player : holder.getHolderManager().getAllPlayersOfHolder(holder)){
				if(player == null || !player.isOnline()) continue;
				
				//If use the skill system, check if player has this skill!
				if(!permanentSkill && plugin.getConfigManager().getGeneralConfig().isConfig_useSkillSystem()){
					if(player.getSkillTreeManager().getLevel( this ) <= 0) continue;
				}
				
				EventWrapper fakeEventWrapper = EventWrapperFactory.buildOnlyWithplayer(player.getPlayer());
				if(checkRestrictions(fakeEventWrapper) != TraitRestriction.None || !canBeTriggered(fakeEventWrapper)) {
					restrictionsFailed(player);
					continue;
				}
				
				if(tickDoneForPlayer(player)){
					plugin.getStatistics().traitTriggered(TickEverySecondsTrait.this);
				}
			}
		}
		
		tickEverySecondsAditionalAfter();
	}
	
	
	/**
	 * Method to be overwritten.
	 * This is called before ticking the Entries.
	 */
	protected void tickEverySecondsAditionalBefore() {}
	
	/**
	 * Method to be overwritten.
	 * This is called after ticking the Entries.
	 */
	protected void tickEverySecondsAditionalAfter() {}
	
	
	
	/**
	 * Notifies that the restrictions failed.
	 * <br>NOTICE: To be overriden
	 * 
	 * @param player to check
	 */
	protected void restrictionsFailed(RaCPlayer player) {};

	/**
	 * Is called when the Tick for the Player is on it's way.
	 * 
	 * @param player The tick for the Player is done.
	 * 
	 * @return true if it triggered, false otherwise.
	 */
	protected abstract boolean tickDoneForPlayer(RaCPlayer player);
	
	
	@Override
	public void deInit(){
		super.deInit();
		
		if(schedulerTask != null) schedulerTask.cancel();
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
			reason = "on DayLight";			
		}
		
		if(onlyInNight && !onlyOnDay){
			reason = "in NightShine";
		}
		
		return getPrettyConfigurationPre() + " every: " + seconds + " sec for " + reason;
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
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		seconds = configMap.getAsInt("seconds");
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
		RaCPlayer player = wrapper.getPlayer();
		int level = player.getLocation().getBlockY();
		
		//Does not trigger outside of the World.
		if(level < 0 || level >= 256) return false;
		
		int lightFromSky = player.getLocation().getBlock().getLightFromSky();
		if(onlyOnDay){ //TODO fixme
			if(lightFromSky > 2){
				return false;
			}
		}

		return true;
	}
}
