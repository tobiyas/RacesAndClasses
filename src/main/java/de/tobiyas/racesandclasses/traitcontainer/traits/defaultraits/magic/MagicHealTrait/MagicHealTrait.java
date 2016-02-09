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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.MagicHealTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealOtherEntityEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;


public class MagicHealTrait extends AbstractMagicSpellTrait {

	/**
	 * The value to heal
	 */
	private double value;
	
	/**
	 * The max distance
	 */
	private int blocks = 3;
	
	
	@TraitInfos(category="magic", traitName="MagicHealTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@TraitEventsUsed(registerdClasses = {PlayerInteractEntityEvent.class, PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "MagicHealTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return "heals: " + value + " max Distance: " + blocks;
	}

	@TraitConfigurationNeeded(fields = {
		@TraitConfigurationField(fieldName = "value", classToExpect = Double.class),
		@TraitConfigurationField(fieldName = "blocks", classToExpect = Integer.class, optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		value = (Double) configMap.get("value");
		
		if(configMap.containsKey("blocks")){
			blocks = (Integer) configMap.get("blocks");
		}
	}


	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof MagicHealTrait)) return false;
		MagicHealTrait otherTrait = (MagicHealTrait) trait;
		
		return value >= otherTrait.value;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait lets you heal others or yourself for a certain value.");
		return helpList;
	}

	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		Player target = null;
		if(player.isSneaking()){
			target = player.getPlayer();
		}else{
			target = (Player) SearchEntity.inLineOfSight(blocks, player.getPlayer(), EntityType.PLAYER);
		}
		
		if(target == null){
			LanguageAPI.sendTranslatedMessage(player, Keys.no_taget_found);
			result.setTriggered(false);
			return;
		}
		
		if(target.getLocation().distanceSquared(player.getLocation()) > (blocks * blocks)){
			LanguageAPI.sendTranslatedMessage(player, Keys.too_far_away);
			result.setTriggered(false);
			return;
		}
		
		if(CompatibilityModifier.BukkitPlayer.isFullyHealed(target)){
			LanguageAPI.sendTranslatedMessage(player, Keys.trait_heal_target_full);
			result.setTriggered(false);
			return;
		}
		
		double modValue = modifyToPlayer(player, value, "value");
		EntityHealOtherEntityEvent event = new EntityHealOtherEntityEvent(target, modValue, RegainReason.MAGIC, player.getPlayer());
		plugin.fireEventToBukkit(event);
		
		if(event.isCancelled()) {
			LanguageAPI.sendTranslatedMessage(player, Keys.trait_failed);			
			result.setTriggered(false);
			return;
		}
		
		double newValue = event.getAmount();
		CompatibilityModifier.BukkitPlayer.safeHeal(newValue, target);
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_healed_target_success, 
				"target", target.getName());
		
		if(player != target){
			LanguageAPI.sendTranslatedMessage(target, Keys.trait_healed_other_success, 
					"healer", player.getName());
		}
		
		result.setTriggered(true);
		return;
	}

}
