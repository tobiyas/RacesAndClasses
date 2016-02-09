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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.LongArrowTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class LongArrowTrait extends AbstractArrow {
	
	/**
	 * The Speed the Arrow should be multiplied with
	 */
	private double speed;
	
	
	public LongArrowTrait(){
	}

	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "LongArrowTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "Arrow speed * " + speed;
	}

	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "speed", classToExpect = Double.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		speed = (Double) configMap.get("speed");
	}

	
	@Override
	protected boolean onShoot(EntityShootBowEvent event){
		Projectile arrow = (Projectile) event.getProjectile();
		Vector oldVelocity = arrow.getVelocity();
		Vector newVelocity = oldVelocity.multiply(speed);
		arrow.setVelocity(newVelocity);
		return true;
	}
	
	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event){
		return false;
	}

	@Override
	protected String getArrowName(){
		return "Long Range Arrow";
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "You can shoot your arrows with more force.");
		return helpList;
	}
	
	
	@TraitInfos(category="arrow", traitName="LongArrowTrait", visible=true)
	@Override
	public void importTrait() {
	}

}
