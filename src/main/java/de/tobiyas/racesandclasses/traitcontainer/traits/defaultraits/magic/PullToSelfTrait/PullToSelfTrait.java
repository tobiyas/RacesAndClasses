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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.PullToSelfTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
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

public class PullToSelfTrait extends AbstractMagicSpellTrait  {

	
	public PullToSelfTrait() {
	}


	/**
	 * The Blocks the enemy is pushed away.
	 */
	private int range = 0;
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "PullToSelfTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "pulls enemy " + range + " blocks away to you.";
	}

	
	@TraitInfos(category="magic", traitName="PullToSelfTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof PullToSelfTrait)) return false;
		
		PullToSelfTrait otherTrait = (PullToSelfTrait) trait;
		return cost > otherTrait.cost;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait pulls Entities to oneself.");
		return helpList;
	}



	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField( fieldName = "range", classToExpect = Integer.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.range = configMap.getAsInt("range");
	}
	
	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		LivingEntity pullEntity = SearchEntity.inLineOfSight(range, player.getPlayer());		
		if(pullEntity != null){
			LanguageAPI.sendTranslatedMessage(player, Keys.success);
			
			Location newLoc = player.getLocation().add(player.getLocation().getDirection().normalize());
			pullEntity.teleport(newLoc);
			
			result.setTriggered(true);
			return;
		}
		
		result.setTriggered(false);
		return;
	}
	
}
