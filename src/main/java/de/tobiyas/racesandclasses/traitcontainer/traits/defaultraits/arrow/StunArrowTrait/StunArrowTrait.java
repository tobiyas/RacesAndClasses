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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.StunArrowTrait;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.StunAPI;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class StunArrowTrait extends AbstractArrow {
	
	/**
	 * The Chance that a Stun works.
	 */
	private double chance = 0.1;
	
	/**
	 * The Rand to check the Chance.
	 */
	private final SecureRandom rand = new SecureRandom();
	
	
	public StunArrowTrait(){
	}

	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "StunArrowTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "stun duration: " + duration + " seconds.";
	}

	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class),
			@TraitConfigurationField(fieldName = "chance", classToExpect = Double.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		duration = (Integer) configMap.get("duration");
		
		if(configMap.containsKey("chance")){
			chance = (Double) configMap.get("chance");			
		}
	}

	
	@Override
	protected boolean onShoot(EntityShootBowEvent event){
		return false;
	}
	
	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event){
		//early out for no damaging.
		if(event.isCancelled() 
				|| CompatibilityModifier.EntityDamage.safeGetDamage(event) == 0) 
			return false;
		
		
		Entity hitTarget = event.getEntity();
		if(!(hitTarget instanceof LivingEntity)) return false;
		
		//chance did not hit.
		if(rand.nextDouble() > chance) return false;
		
		
		Player shooter = null;
		if(event.getDamager() instanceof Arrow){
			Arrow arrow = (Arrow) event.getDamager();
			if(arrow.getShooter() instanceof Player){
				shooter = (Player) arrow.getShooter();
			}
		}else{
			if(event.getDamager() instanceof Player){
				shooter = (Player) event.getDamager();
			}
		}
		
		RaCPlayer racshooter = RaCPlayerManager.get().getPlayer((Player) shooter);
		int modDur = modifyToPlayer(racshooter, duration, "duration");
		
		boolean stunned = StunAPI.StunEntity.stunEntityForSeconds(shooter, hitTarget, modDur);
		if(stunned){
			if(shooter != null){
				String enemy = hitTarget instanceof Player ? 
						((Player)hitTarget).getName() : 
						hitTarget.getType().name();
				
				LanguageAPI.sendTranslatedMessage(shooter, Keys.trait_stun_arrow_success,
						"duration", String.valueOf(modDur), "target", enemy);
			}
		}
		
		return stunned;
	}

	@Override
	protected String getArrowName(){
		return "Stun Arrow";
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "If you hit an enemy with an arrow and choosen the Stun Arrow as current arrow,");
		helpList.add(ChatColor.YELLOW + "He will be stunned.");
		return helpList;
	}
	
	
	@TraitInfos(category="arrow", traitName="StunArrowTrait", visible=true)
	@Override
	public void importTrait() {
	}

}
