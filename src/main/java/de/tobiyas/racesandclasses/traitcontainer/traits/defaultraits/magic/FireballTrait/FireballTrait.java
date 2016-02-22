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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.FireballTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.damage.PreEntityDamageEvent;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.schedule.DebugBukkitRunnable;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.helper.ParticleHelper;

public class FireballTrait extends AbstractMagicSpellTrait  {

	/**
	 * the damage this does.
	 */
	private double damage = 3;
	
	/**
	 * the maximal range of the fireball.
	 */
	private double maxRange = 20;
	

	@Override
	public String getName() {
		return "FireballTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return costType.name() + (materialForCasting == null ? " " : (" " + materialForCasting.name() + " ")) + cost;
	}

	
	@TraitInfos(category="activate", traitName="FireballTrait", visible=true)
	@Override
	public void importTrait() {}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(classToExpect = Double.class, fieldName = "damage", optional = true),
			@TraitConfigurationField(classToExpect = Double.class, fieldName = "range", optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		damage = configMap.getAsDouble("damage", 3);
		maxRange = configMap.getAsDouble("range", 20);
	}
	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof FireballTrait)) return false;
		
		FireballTrait otherTrait = (FireballTrait) trait;
		return cost > otherTrait.cost;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait fires a Fireball.");
		return helpList;
	}

	
	private final String META_KEY = "RAC_PLAYER_META";
	private static boolean inDamageFunction = false;

	
	@EventHandler
	public void fireballHit(ProjectileHitEvent event){
		if(event.getEntity().hasMetadata(META_KEY)){
			try{
				inDamageFunction = true;
				RaCPlayer shooter = (RaCPlayer) event.getEntity().getMetadata(META_KEY).get(0).value();
				if(!TraitHolderCombinder.checkContainer(shooter, this)) return;
				
				//Show explosion and Remove the entity!
				ParticleHelper.sendParticleEffectToAll(
						ParticleEffects.HUGE_EXPLOSION, 
						event.getEntity().getLocation(), new Vector(0,0.1,0), 0, 1);
				event.getEntity().removeMetadata(META_KEY, plugin);
				event.getEntity().remove();
				
				double modDamge = modifyToPlayer(shooter, damage, "damage");
				
				List<Entity> nearEntities = SearchEntity.inCircleAround(event.getEntity(), 4);
				for(Entity near : nearEntities){
					//don't do damage to yourself!
					if(near == shooter.getPlayer()) continue;
					
					if(near instanceof LivingEntity){
						LivingEntity nearLiving = (LivingEntity) near;
						if(!EnemyChecker.areAllies(shooter.getPlayer(), near)){
							double realDamage = PreEntityDamageEvent.getRealDamage(shooter.getPlayer(), near, DamageCause.MAGIC, modDamge);
							de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.LivingEntity
								.safeDamageEntityByEntity(nearLiving, shooter.getPlayer(), realDamage);
						}
					}
				}
			}finally{ inDamageFunction = false; }
		}
	}
	
	
	@EventHandler
	public void entityDamageByFireball(EntityDamageByEntityEvent event){
		if(inDamageFunction) return;
		if(event.isCancelled()) return;
		
		Entity damager = event.getDamager();
		if(damager.getType() == EntityType.FIREBALL && damager.hasMetadata(META_KEY)){
			event.setCancelled(true);
		}
	}
	
	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		Vector viewDirection = player.getLocation().getDirection();
		if(viewDirection == null){
			LanguageAPI.sendTranslatedMessage(player, Keys.no_taget_found);
			result.setTriggered(false);
			return;
		}
		
		Fireball fireball = player.getPlayer().launchProjectile(Fireball.class);
		fireball.setVelocity(viewDirection);
		fireball.setMetadata(META_KEY, new FixedMetadataValue((Plugin) plugin, player));
		startFireballRangeShortener(fireball);
		
		LanguageAPI.sendTranslatedMessage(player, Keys.launched_something, "name", "Fireball");
		result.setTriggered(true);
		return;
	}
	
	/**
	 * Starts a fireball Remover ticker.
	 * @param fireball to remove.
	 */
	private void startFireballRangeShortener(final Fireball fireball){
		new DebugBukkitRunnable("FireballRangeChecker") {
			private final Location start = fireball.getLocation();
			
			@Override
			protected void runIntern() {
				boolean sameWorld = start.getWorld() == fireball.getWorld();
				double distSquare = !sameWorld ? 133700 : fireball.getLocation().distanceSquared(start);
				double maxDistSquare = maxRange * maxRange;
				
				if(!fireball.isValid() 
						|| fireball.isDead() 
						|| distSquare > maxDistSquare){
					
					if(!fireball.isDead()) fireballHit(new ProjectileHitEvent(fireball));
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 10, 10);
	}
	
}
