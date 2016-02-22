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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.MightyArrowTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.EntityDamage;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class MightyArrowTrait extends AbstractArrow {

	private double additionalDamage = 1;
	
	
	public MightyArrowTrait(){
	}
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class, PlayerInteractEvent.class, EntityShootBowEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "MightyArrowTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "Additional damage: " + additionalDamage;
	}
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class, optional = true),
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("damage")){
			this.additionalDamage = configMap.getAsDouble("damage");
		}
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
		
		
		Entity shooterEntity = event.getDamager();
		if(shooterEntity instanceof Projectile) {
			shooterEntity = CompatibilityModifier.Shooter.getShooter((Projectile)shooterEntity);
		}
		
		if(shooterEntity.getType() != EntityType.PLAYER) return false;
		if(!EnemyChecker.areEnemies(event.getDamager(), hitTarget)) return false;
		
		RaCPlayer shooter = RaCPlayerManager.get().getPlayer((Player) shooterEntity);
		double modDamage = modifyToPlayer(shooter, additionalDamage, "damage");
		
		double newDamage = modDamage + EntityDamage.safeGetDamage(event);
		EntityDamage.safeSetDamage(newDamage, event);
		return false;
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}

	@Override
	protected String getArrowName() {
		return "MightyArrowTrait";
	}

	@TraitInfos(category="arrow", traitName="MightyArrowTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "When you hit an target with an arrow and the MightyArrowTrait is selected,");
		helpList.add(ChatColor.YELLOW + "the target will get More damage by this arrow.");
		return helpList;
	}
	
}
