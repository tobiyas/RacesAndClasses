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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.SprintTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectTypeWrapper;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.MessageScheduleApi;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfig;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class SprintTrait extends AbstractBasicTrait {

	private int value;
	private int duration;
	
	private static Material itemIDInHand = Material.APPLE;
	
	/**
	 * The Set of sprinting people.
	 */
	private final Set<String> sprinting = new HashSet<String>();
	
	
	@SuppressWarnings("deprecation")
	@TraitEventsUsed(registerdClasses = {PlayerToggleSprintEvent.class})
	@Override
	public void generalInit() {
		TraitConfig config = plugin.getConfigManager().getTraitConfigManager().getConfigOfTrait(getName());
		if(config != null){
			itemIDInHand = Material.getMaterial((Integer) config.getValue("trait.iteminhand", Material.APPLE.getId()));
		}
	}
	
	
	@Override
	public String getName(){
		return "SprintTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "level(" + value + ") for " + duration + "seconds";
	}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class),
			@TraitConfigurationField(fieldName = "value", classToExpect = Integer.class),
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		duration = (Integer) configMap.get("duration");
		value = (Integer) configMap.get("value");
	}

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		if(!(event instanceof PlayerToggleSprintEvent)) return TraitResults.False();
		PlayerToggleSprintEvent Eevent = (PlayerToggleSprintEvent) event;
		if(!Eevent.isSprinting()){
			return TraitResults.False();
		}
		
		final Player player = Eevent.getPlayer();
		if(player.getItemInHand().getType() != itemIDInHand) return TraitResults.False();
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_toggled, "name", getDisplayName());
		int modDur = modifyToPlayer(eventWrapper.getPlayer(), duration, "duration");
		player.addPotionEffect(PotionEffectTypeWrapper.SPEED.createEffect(modDur * 20, value - 1), true);
		sprinting.add(player.getName());
		
		MessageScheduleApi.scheduleTranslateMessageToPlayer(player.getName(), modDur, Keys.trait_faded,
				"name", getDisplayName());

		scheduleRemovalOfName(player.getName());
		return TraitResults.True();
	}
	
	/**
	 * Removes the Player from the Sprinting list.
	 * 
	 * @param name to remove
	 */
	private void scheduleRemovalOfName(final String name) {
		Bukkit.getScheduler().scheduleSyncDelayedTask((JavaPlugin)plugin, new Runnable(){

			@Override
			public void run() {
				sprinting.remove(name);
			}
			
		}, duration * 20);
	}


	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait lets you sprint (move faster) for a short time.");
		helpList.add(ChatColor.YELLOW + "It can be used by toggleing sprint with a " + ChatColor.LIGHT_PURPLE 
				+ itemIDInHand.name() + ChatColor.YELLOW + " in hands.");
		
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof SprintTrait)) return false;
		SprintTrait otherTrait = (SprintTrait) trait;
		
		return value >= otherTrait.value;
	}


	@TraitInfos(category="activate", traitName="SprintTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		if(!(event instanceof PlayerToggleSprintEvent)) return false;
		PlayerToggleSprintEvent Eevent = (PlayerToggleSprintEvent) event;
		if(!Eevent.isSprinting()){
			return false;
		}
		
		if(sprinting.contains(Eevent.getPlayer().getName())){
			return false;
		}
		
		Player player = Eevent.getPlayer();
		if(player.getItemInHand().getType() != itemIDInHand) return false;
		
		return true;
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
