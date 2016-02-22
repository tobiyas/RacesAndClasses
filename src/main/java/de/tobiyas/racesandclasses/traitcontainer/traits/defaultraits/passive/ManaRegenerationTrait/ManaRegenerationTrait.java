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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ManaRegenerationTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper.RegainResource;
import de.tobiyas.racesandclasses.eventprocessing.events.mana.ManaRegenerationEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class ManaRegenerationTrait extends AbstractBasicTrait {

	private int bukkitTaskID = -1;
	
	private int time;
	private double value;
	
	
	@TraitEventsUsed(registerdClasses = {ManaRegenerationEvent.class})
	@Override
	public void generalInit() {
		if(time > 0) setupBukkitManaRegenerationTask();
	}
	
	
	/**
	 * Setus up the Bukkit task.
	 */
	private void setupBukkitManaRegenerationTask(){
		if(time <= 0) return;
		
		if(bukkitTaskID > 0){
			Bukkit.getScheduler().cancelTask(bukkitTaskID);
		}
		
		if(bukkitTaskID < 0 || !Bukkit.getScheduler().isQueued(bukkitTaskID)){

			int tickTime = time * 20;			
			bukkitTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask((JavaPlugin)plugin, new Runnable() {
				
				@Override
				public void run() {
					for(AbstractTraitHolder holder : ManaRegenerationTrait.this.getTraitHolders()){
						for(RaCPlayer player : holder.getHolderManager().getAllPlayersOfHolder(holder)){
							if(player != null && player.isOnline()){
								double modValue = modifyToPlayer(player, value, "value");
								Bukkit.getPluginManager().callEvent(new ManaRegenerationEvent(player.getPlayer(), modValue));
							}
						}
					}
				}
			}, tickTime, tickTime);
		}
	}
	

	@Override
	public String getName() {
		return "ManaRegenerationTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "value: " + value + "every " + time + " seconds";
	}

	
	@TraitInfos(category="passive", traitName="ManaRegenerationTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(trait instanceof ManaRegenerationTrait){
			return value > ((ManaRegenerationTrait)trait).value;
		}
		return false;
	}

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		ManaRegenerationEvent manaRegEvent = (ManaRegenerationEvent) event;
		Player player = manaRegEvent.getPlayer();
		double amount = manaRegEvent.getAmount();
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		racPlayer.getManaManager().fillMana(amount);
		return TraitResults.True();
	}
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class),
			@TraitConfigurationField(fieldName = "time", classToExpect = Integer.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.value = (Double) configMap.get("value");
		this.time = (Integer) configMap.get("time");
		
		setupBukkitManaRegenerationTask();
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait changes (increases or decreases) the movement speed of a Player.");
		return helpList;
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		RegainResource resource = wrapper.getRegainResource();
		if(resource != RegainResource.MANA) return false;
		
		RaCPlayer player = wrapper.getPlayer();
		return player != null && player.isOnline() &&
				!player.getManaManager().isManaFull();
	}
}
