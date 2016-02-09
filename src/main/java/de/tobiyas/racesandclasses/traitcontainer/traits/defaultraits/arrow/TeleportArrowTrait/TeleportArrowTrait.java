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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.TeleportArrowTrait;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class TeleportArrowTrait extends AbstractArrow {
	
	private HashMap<Arrow, Player> shootedArrows = new HashMap<Arrow, Player>();

	public TeleportArrowTrait(){
	}
	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit(){
	}
	

	@Override
	public String getName() {
		return "TeleportArrowTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "Cooldown: " + cooldownTime + " seconds";
	}

	@TraitConfigurationNeeded
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
	}

	@Override
	protected boolean onShoot(EntityShootBowEvent event) {
		LivingEntity shooter = event.getEntity();
		if(!(shooter instanceof Player)) return false;
		if(!(event.getProjectile() instanceof Arrow)) return false;
		
		Arrow arrow = (Arrow) event.getProjectile();
		Player player = (Player) event.getEntity();
		
		shootedArrows.put(arrow, player);
		
		LanguageAPI.sendTranslatedMessage(player, Keys.launched_something, 
				"name", getDisplayName());
		return true;
	}

	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event) {
		//Not needed
		return false;
	}
	
	@Override
	protected boolean onHitLocation(ProjectileHitEvent event){
		Arrow arrow = (Arrow) event.getEntity();
		
		Player player = shootedArrows.get(arrow);
		if(player == null) return false;
		shootedArrows.remove(arrow);
		
		Location newLocation = arrow.getLocation();
		Location oldLocation = player.getLocation(); 
		enderEffectOnLocation(oldLocation);
		
		player.teleport(newLocation);
		return true;
	}
	
	private void enderEffectOnLocation(Location loc){
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
		
		loc.setY(loc.getY() + 1);
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
		
		loc.setY(loc.getY() + 2);
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
	}
	

	@Override
	protected String getArrowName() {
		return "Teleport Arrow";
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "When this arrow hits a target/block,");
		helpList.add(ChatColor.YELLOW + "the player will be teleportet there.");
		helpList.add(ChatColor.YELLOW + "Don't forget, that this trait has uplink.");
		return helpList;
	}
	
	@TraitInfos(category="arrow", traitName="TeleportArrowTrait", visible=true)
	@Override
	public void importTrait() {
	}

}
