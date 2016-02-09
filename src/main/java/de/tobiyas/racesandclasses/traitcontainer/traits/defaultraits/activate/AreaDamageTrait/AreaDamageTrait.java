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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.AreaDamageTrait;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractActivateAETrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.damage.PreEntityDamageEvent;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.Vollotile;

public class AreaDamageTrait extends AbstractActivateAETrait {

	/**
	 * The Damage to do.
	 */
	protected double damage = 1;
	
	protected DamageCause cause = DamageCause.FIRE;
	
	protected ParticleContainer particles = new ParticleContainer(de.tobiyas.racesandclasses.vollotile.ParticleEffects.FIREWORKS_SPARK, 30, 0);
	
	protected ParticleContainer particlesFromSelf = null;
	
	protected boolean showLine = true;
	
	
	public AreaDamageTrait() {
	}
	

	@Override
	public String getName() {
		return "AreaDamageTrait";
	}


	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class, optional = false),
			@TraitConfigurationField(fieldName = "cause", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "particle", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "particleFromSelf", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "showLine", classToExpect = Boolean.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("damage")){
			damage = configMap.getAsDouble("damage");
		}
		
		if(configMap.containsKey("showLine")){
			showLine = configMap.getAsBool("showLine");
		}
		
		if(configMap.containsKey("cause")){
			String stringCause = configMap.getAsString("cause");
			DamageCause realCause = DamageCause.FIRE;
			try{ realCause = DamageCause.valueOf(stringCause.toUpperCase()); }catch(Throwable exp){}
			
			cause = realCause;
		}
		

		if(configMap.containsKey("particle")){
			particles = configMap.getAsParticleContainer("particle");
		}
		
		if(configMap.containsKey("particleFromSelf")){
			particlesFromSelf = configMap.getAsParticleContainer("particleFromSelf");
		}
		
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This Trait Does an AOE and hits everyone in range.");
		return helpList;
	}
	

	@TraitInfos(category = "activate", traitName = "AreaDamageTrait", visible = true)
	@Override
	public void importTrait() {
	}


	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}


	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit() {
	}
	

	@Override
	protected boolean triggerOnEntity(final RaCPlayer player, final Entity otherEntity) {
		if(!(otherEntity instanceof LivingEntity)) return false;
		
		double modDamage = modifyToPlayer(player, this.damage, "damage");
		final double damage = PreEntityDamageEvent.getRealDamage(player.getPlayer(), otherEntity, DamageCause.CONTACT, modDamage);
		if(damage <= 0) return false;
		
		BukkitRunnable runnable = new BukkitRunnable(){
			@Override
			public void run(){
				CompatibilityModifier.LivingEntity.safeDamageEntityByEntity((LivingEntity)otherEntity, player.getPlayer(), damage);				
			}
		};
		
		int later = 0;
		if(particles != null) {
			if(showLine){
				int maxTimeToTake = 20;
				final Queue<Location> onTheWay = SearchEntity.getAllOnWay(player.getLocation(), otherEntity.getLocation());
				int ticks = maxTimeToTake / onTheWay.size();
				
				new BukkitRunnable(){
					@Override
					public void run() {
						if(onTheWay.isEmpty()) {
							this.cancel();
							return;
						}
						
						Location next = onTheWay.poll();
						Vollotile.get().sendOwnParticleEffectToAll(
								particles,
								next);
					}
				}.runTaskTimer((Plugin)plugin, ticks, ticks);
				later = 20;
				
			}else {
				Vollotile.get().sendOwnParticleEffectToAll(particles, otherEntity.getLocation());
			}
		}
		
		runnable.runTaskLater(plugin, later);
		
		if(particlesFromSelf != null) Vollotile.get().sendOwnParticleEffectToAll(particlesFromSelf, player.getLocation());
		return true;
	}


	@Override
	protected String getPrettyConfigIntern() {
		return "Does " + damage + " damage on Enemies around you.";
	}

}
