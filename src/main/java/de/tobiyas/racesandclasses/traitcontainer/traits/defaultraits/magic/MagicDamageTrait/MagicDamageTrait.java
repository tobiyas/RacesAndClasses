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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.MagicDamageTrait;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.Vollotile;

public class MagicDamageTrait extends AbstractMagicSpellTrait  {

	
	public MagicDamageTrait() {
	}


	/**
	 * The Blocks the enemy is pushed away.
	 */
	private int range = 4;
	
	private double damage = 3;
	
	private boolean showLine = true;
	
	private PotionEffectType potionType = null;
	private int potionAmplifier = 0;
	private int potionDuration = 0;
	
	
	
	private ParticleContainer targetParticles = new ParticleContainer(de.tobiyas.racesandclasses.vollotile.ParticleEffects.EXPLODE, 1, 0);
	
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "MagicDamageTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "Does damage " + range + " blocks away to you.";
	}

	
	@TraitInfos(category="magic", traitName="MagicDamageTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof MagicDamageTrait)) return false;
		
		MagicDamageTrait otherTrait = (MagicDamageTrait) trait;
		return cost > otherTrait.cost;
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Does direct magic damage.");
		return helpList;
	}


	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField( fieldName = "range", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField( fieldName = "damage", classToExpect = Double.class, optional = true),
			@TraitConfigurationField( fieldName = "targetParticles", classToExpect = String.class, optional = true),
			@TraitConfigurationField( fieldName = "showLine", classToExpect = Boolean.class, optional = true),

			@TraitConfigurationField( fieldName = "potionAmplifier", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField( fieldName = "potionType", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField( fieldName = "potionDuration", classToExpect = Integer.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		//General stuff:
		this.range = configMap.getAsInt("range",4);
		this.damage = configMap.getAsDouble("damage", 3);
		this.targetParticles = configMap.getAsParticleContainer("targetParticles", targetParticles);
		
		//potions.
		this.potionAmplifier = configMap.getAsInt("potionAmplifier", 0);
		this.potionDuration = configMap.getAsInt("potionDuration", 20 * 10);
		this.potionType = configMap.getAsPotionEffectType("potionType", null);
	}
	
	
	@Override
	protected TraitRestriction checkForFurtherRestrictions(EventWrapper wrapper) {
		final LivingEntity targetEntity = SearchEntity.inLineOfSight(80, wrapper.getPlayer().getPlayer());
		if(targetEntity == null) {
			return TraitRestriction.NoTarget;
		}
		
		if(targetEntity.getLocation().distanceSquared(wrapper.getPlayer().getLocation()) > range * range){
			return TraitRestriction.OutOfRange;
		}
		
		if(EnemyChecker.areAllies(wrapper.getPlayer().getPlayer(), targetEntity)){
			return TraitRestriction.TargetFriendly;
		}
		
		return super.checkForFurtherRestrictions(wrapper);
	}
	
	
	@Override
	protected void magicSpellTriggered(final RaCPlayer player, TraitResults result) {
		final LivingEntity targetEntity = SearchEntity.inLineOfSight(range, player.getPlayer());
		
		if(targetEntity != null && 
				EnemyChecker.areEnemies(player.getPlayer(), targetEntity)){
			LanguageAPI.sendTranslatedMessage(player, Keys.success);
			
			final double modDamage = modifyToPlayer(player, damage, "damage");
			BukkitRunnable runnable = new BukkitRunnable(){
				@Override
				public void run(){
					if(targetEntity.isDead() || !targetEntity.isValid()) return;
					CompatibilityModifier.LivingEntity.safeDamageEntityByEntity((LivingEntity)targetEntity, player.getPlayer(), modDamage, DamageCause.MAGIC);

					if(potionAmplifier <= 0 || potionType == null || potionDuration <= 0) return;
					int modAmp = modifyToPlayer(player, potionAmplifier, "potionAmplifier");
					PotionEffect effect = new PotionEffect(potionType, potionDuration, modAmp);
					targetEntity.addPotionEffect(effect);
				}
			};
			
			
			if(targetParticles != null) {
				if(showLine){
					int maxTimeToTake = 20;
					final Queue<Location> onTheWay = SearchEntity.getAllOnWay(player.getLocation().add(0,1,0),
							targetEntity.getEyeLocation());
					
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
									targetParticles, 
									next);
						}
					}.runTaskTimer((Plugin)plugin, ticks, ticks);
					runnable.runTaskLater((Plugin)RacesAndClasses.getPlugin(), 20);
					
					
				}else {
					Vollotile.get().sendOwnParticleEffectToAll(targetParticles, targetEntity.getLocation());
					runnable.run();
				}
			}
			
			result.setTriggered(true);
			return;
		}
		
		result.setTriggered(false);
		return;
	}
	
}
