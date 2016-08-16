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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.ExplosionTrait;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.CostType;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class ExplosionTrait extends AbstractMagicSpellTrait  {

	/**
	 * Tells to deal world damage if true
	 */
	private boolean explode = false;
	
	/**
	 * the range to explode
	 */
	private int range = 0;
	
	/**
	 * The damage to deal on explosion
	 */
	private double damage = 0;
	
	
	@TraitEventsUsed()
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "ExplosionTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return costType.name() + 
				(costType == CostType.ITEM ? 
						(" " + materialForCasting.name() + " ")
						: " " 
				)
				+ cost;
	}

	
	@TraitInfos(category="magic", traitName="ExplosionTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class ),
			@TraitConfigurationField(fieldName = "range", classToExpect = Integer.class ),
			@TraitConfigurationField(fieldName = "explode", classToExpect = Boolean.class, optional = true)
		})
	
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.damage = configMap.getAsDouble("damage");
		this.range = configMap.getAsInt("range");
		
		if(configMap.containsKey("explode")){
			this.explode = configMap.getAsBool("explode");
		}
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof ExplosionTrait)) return false;
		
		ExplosionTrait otherTrait = (ExplosionTrait) trait;
		return cost > otherTrait.cost;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait lets you explode and deal damage to everyone around you.");
		return helpList;
	}

	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		Location location = player.getPlayer().getEyeLocation();

		player.getPlayer().setNoDamageTicks(10);

		location.getWorld().createExplosion(location.getBlock().getRelative(BlockFace.NORTH).getLocation(), 0);
		location.getWorld().createExplosion(location.getBlock().getRelative(BlockFace.EAST).getLocation(), 0);
		location.getWorld().createExplosion(location.getBlock().getRelative(BlockFace.SOUTH).getLocation(), 0);
		location.getWorld().createExplosion(location.getBlock().getRelative(BlockFace.WEST).getLocation(), 0);
		
		double modDamage = modifyToPlayer(player, damage, "damage");
		
		if(explode){
			lastCaster = player.getUniqueId();
			if(location.getWorld().createExplosion(location, (float) modDamage)){
				result.setTriggered(true);
				lastCaster = null;
				return;
			}
			
			lastCaster = null;
		}
		
		List<LivingEntity> entities = getNearbyEntities(location, range);
		for(LivingEntity entity : entities){
			if(entity == player){
				continue;
			}
			
			EntityDamageByEntityEvent damageEvent = CompatibilityModifier.EntityDamageByEntity.
					safeCreateEvent(player.getPlayer(), entity, DamageCause.ENTITY_EXPLOSION, modDamage);
			
			lastCaster = player.getUniqueId();
			plugin.fireEventToBukkit(damageEvent);
			lastCaster = null;
			
			double newDamage = CompatibilityModifier.EntityDamage.safeGetDamage(damageEvent);
			if(!damageEvent.isCancelled() && newDamage > 0){
				de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.LivingEntity
					.safeDamageEntityByEntity(entity, player.getPlayer(), newDamage);
			}
		}	
		
		result.setTriggered(true);
	}
	
	
	
	private UUID lastCaster;
	
	
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		if(lastCaster == null) return;
		
		if(event.getEntity().getUniqueId().equals(lastCaster)) event.setCancelled(true);
	}
	
	
	/**
	 * Gets all Entities in a radius of the location.
	 * 
	 * Stolen from: 'https://forums.bukkit.org/threads/getnearbyentities-of-a-location.101499/#post-1341141'
	 * 
	 * @param location
	 * @param radius
	 * @return set of all Entities near
	 */
	public static List<LivingEntity>  getNearbyEntities(Location location, int radius){
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        List<LivingEntity> radiusEntities = new LinkedList<LivingEntity>();
        for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
            
        	for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                int x=(int) location.getX(),y=(int) location.getY(),z=(int) location.getZ();
                
                for (Entity e : new Location(location.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                	if(!(e instanceof LivingEntity)) continue;
                	
                    if (e.getLocation().distance(location) <= radius && e.getLocation().getBlock() != location.getBlock()){
                    	radiusEntities.add((LivingEntity) e);
                    }
                }
            }
        }
            
        return radiusEntities;
    }
	
}
