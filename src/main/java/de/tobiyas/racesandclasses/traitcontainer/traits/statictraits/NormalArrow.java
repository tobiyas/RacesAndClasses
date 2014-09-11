/*******************************************************************************
 * Copyright 2014 Tobias Welther
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
package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;

public class NormalArrow extends AbstractArrow {
	
	public NormalArrow(){
	}
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class, PlayerInteractEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "Normal Arrow";
	}

	@Override
	protected String getPrettyConfigIntern() {
		return "Just a normal Arrow";
	}

	@TraitConfigurationNeeded
	@Override
	public void setConfiguration(TraitConfiguration configMap) {
	}

	@Override
	protected boolean onShoot(EntityShootBowEvent event) {
		return false;
	}

	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event) {
		return false;
	}

	@Override
	protected String getArrowName() {
		return "Normal Arrow";
	}


	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}
	
	@TraitInfos(category="arrow", traitName="NormalArrowTrait", visible=false)
	@Override
	public void importTrait() {
	}
	
	
	@Override
	public boolean isVisible() {
		return false;
	}

}
