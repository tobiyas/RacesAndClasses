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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.PotionArrowTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.friend.TargetType;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class PotionArrowTrait extends AbstractArrow {

	private PotionEffectType type = PotionEffectType.SLOW;

	private int amplifier = 1;
	
	private int duration = 10;
	
	private TargetType target = TargetType.ENEMY;

	
	
	public PotionArrowTrait(){
	}
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class, PlayerInteractEvent.class, EntityShootBowEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "PotionArrowTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "Effect: " + this.totalDamage + " Poison-Damage over " + duration + " seconds.";
	}
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "type", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "amplifier", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "target", classToExpect = String.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		duration = configMap.getAsInt("duration", 10);
		type = configMap.getAsPotionEffectType("type", PotionEffectType.SLOW);
		
		amplifier = configMap.getAsInt("amplifier",1);
		target = configMap.getAsTargetType("target", TargetType.ENEMY);
	}
	
	@Override
	protected boolean onShoot(EntityShootBowEvent event) {
		//Not needed
		return true;
	}

	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event) {
		Entity hitTarget = event.getEntity();
		if(!(hitTarget instanceof LivingEntity)) return false;
		
		if(!EnemyChecker.isApplyable(event.getDamager(), hitTarget, target)) return false;
		
		RaCPlayer shooter = RaCPlayerManager.get().getPlayer((Player)event.getDamager());
		int modAmp = modifyToPlayer(shooter, amplifier, "amplifier");
		
		PotionEffect effect = new PotionEffect(type, duration * 20, modAmp);
		((LivingEntity) hitTarget).addPotionEffect(effect);
		
		return true;
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}

	@Override
	protected String getArrowName() {
		return "Poison Arrow";
	}

	@TraitInfos(category="arrow", traitName="PotionArrowTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "When you hit an target with an arrow and the PotionArrowTrait is selected,");
		helpList.add(ChatColor.YELLOW + "the player will get the Potion effect of the Arrow.");
		return helpList;
	}
	
}
