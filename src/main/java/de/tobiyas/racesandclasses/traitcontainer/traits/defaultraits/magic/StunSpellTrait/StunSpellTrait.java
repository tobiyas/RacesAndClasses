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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.StunSpellTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.StunAPI;
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
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.Vollotile;


public class StunSpellTrait extends AbstractMagicSpellTrait {

	/**
	 * The duration to use.
	 */
	private int duration;
	
	/**
	 * The max distance
	 */
	private double range = 5;
	
	
	private ParticleContainer ownParticle = null;
	private ParticleContainer targetParticle = null;
	
	
	
	@TraitInfos(category="magic", traitName="StunSpellTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@TraitEventsUsed()
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "StunSpellTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return "stuns for " + duration + " seconds";
	}

	@TraitConfigurationNeeded(fields = {
		@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class, optional = true),
		@TraitConfigurationField(fieldName = "range", classToExpect = Double.class, optional = true),
		@TraitConfigurationField(fieldName = "targetParticle", classToExpect = String.class, optional = true),
		@TraitConfigurationField(fieldName = "ownParticle", classToExpect = String.class, optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("duration")){
			duration = configMap.getAsInt("duration");
		}
		
		if(configMap.containsKey("range")){
			range = configMap.getAsDouble("range");
		}
		
		if(configMap.containsKey("targetParticle")){
			targetParticle = configMap.getAsParticleContainer("targetParticle");
		}
		
		if(configMap.containsKey("ownParticle")){
			ownParticle = configMap.getAsParticleContainer("ownParticle");
		}
	}


	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait stuns your opponent.");
		return helpList;
	}

	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		Entity target = SearchEntity.inLineOfSight((int) range, player.getPlayer());
		if(target == null) {
			result.copyFrom(TraitResults.False());
			return;
		}
		
		String targetName = "";
		
		int modDur = modifyToPlayer(player, duration*20, "duration");
		if(target instanceof Player){
			StunAPI.StunPlayer.stunPlayerForTicks((Player)target, modDur);
			targetName = ((Player) target).getDisplayName();
		}else{
			StunAPI.StunEntity.stunEntityForTicks(target, modDur);			
			targetName = (target instanceof LivingEntity)
					? ((LivingEntity) target).getCustomName() == null 
						?((LivingEntity) target).getCustomName()
						: target.getType().name()
					: target.getType().name();
		}
		
		if(ownParticle != null) Vollotile.get().sendOwnParticleEffectToAll(ownParticle, player.getLocation());
		if(targetParticle != null) Vollotile.get().sendOwnParticleEffectToAll(targetParticle, target.getLocation());

		player.sendTranslatedMessage(Keys.stun_success, "target", targetName);
		result.copyFrom(TraitResults.True());
	}

}
