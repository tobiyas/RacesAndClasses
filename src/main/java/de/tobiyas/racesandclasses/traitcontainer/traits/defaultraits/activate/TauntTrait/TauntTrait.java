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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.TauntTrait;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.Plugin;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.MessageScheduleApi;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class TauntTrait extends AbstractMagicSpellTrait implements Listener {

	/**
	 * The Map of the Taunted Entities.
	 */
	private Map<String, List<Entity>> tauntMap = new HashMap<String, List<Entity>>();
	
	/**
	 * the amount of seconds taunted.
	 */
	private int seconds = 0;
	
	/**
	 * The distance to the enemy.
	 */
	private int range = 0;
	
	
	@TraitEventsUsed
	@Override
	public void generalInit() {
		Bukkit.getPluginManager().registerEvents(this, (Plugin) plugin);
	}
	
	
	@Override
	public String getName(){
		return "TauntTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "taunting for " + seconds + " seconds.";
	}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "seconds", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "range", classToExpect = Integer.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		seconds = configMap.getAsInt("seconds", 3);
		range = configMap.getAsInt("range", 5);
	}

	
	
	/**
	 * Removes the Dead Entities.
	 */
	private void removeDead(){
		for(List<Entity> playerTaunts : tauntMap.values()){
			Iterator<Entity> it = playerTaunts.iterator();
			while(it.hasNext()){
				Entity subject = it.next();
				if(subject == null || subject.isDead()){
					it.remove();
				}
			}
		}
	}
	
	
	private void scheduleTauntRemove(final RaCPlayer player, final Creature target) {
		int modDur = modifyToPlayer(player, seconds * 20, "seconds");
		
		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)plugin, new Runnable(){
			@Override
			public void run() {
				List<Entity> taunts = tauntMap.get(player.getName());
				taunts.remove(target);
				
				removeDead();
			}
		}, modDur);
	}

	
	@EventHandler
	public void OnTargetChange(EntityTargetEvent event){		
		Entity target = event.getEntity();
		for(List<Entity> list : tauntMap.values()){
			for(Entity entity : list){
				if(entity.equals(target)){
					event.setCancelled(true);
				}
			}
		}
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait lets you Taunt an enemy.");
		
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof TauntTrait)) return false;
		TauntTrait otherTrait = (TauntTrait) trait;
		
		return seconds >= otherTrait.seconds;
	}


	@TraitInfos(category="activate", traitName="TauntTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}

	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		//Not needed
		return false;
	}
	
	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		return true;
	}


	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		LivingEntity targetToCheck = SearchEntity.inLineOfSight(range, player.getPlayer());
		
		if(targetToCheck == null
				||!player.isOnline()
				||!(targetToCheck instanceof Creature)
				||!targetToCheck.isValid()  ) {
			
			result.copyFrom(TraitResults.False());
			return;
		}
		
		Creature target = (Creature) targetToCheck;
		String targetName = target.getType().name();
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_taunt_success, 
				"target", targetName);
		player.sendMessage(ChatColor.LIGHT_PURPLE + target.getType().name() + ChatColor.GREEN + " taunted.");
		
		target.setTarget(player.getPlayer());
		
		List<Entity> taunts = tauntMap.containsKey(player.getName()) 
				? tauntMap.get(player.getName()) 
				: new LinkedList<Entity>();
		
		taunts.add(target);
		tauntMap.put(player.getName(), taunts);
		
		MessageScheduleApi.scheduleTranslateMessageToPlayer(player.getName(), modifyToPlayer(player, seconds, "seconds"), Keys.trait_taunt_fade,
				"target", targetName);
		
		scheduleTauntRemove(player, target);
		result.copyFrom(TraitResults.True());
		return;
	}
	
}
