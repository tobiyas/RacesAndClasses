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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.AreaAirDropSpellTrait;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.util.damage.PreEntityDamageEvent;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.Vollotile;

public class AreaAirDropSpellTrait extends AbstractMagicSpellTrait implements Listener {

	/**
	 * The Damage to do.
	 */
	protected double damage = 1;
	
	protected int rainRange = 10;
	
	protected int explosionRange = 2;
	
	protected int amount = 5;
	
	protected int aboveTarget = 15;
	
	protected Material mat = Material.TNT;
	
	protected int matDamageValue = 0;
	
	protected ParticleContainer particles = null;

	protected ParticleContainer particlesFromSelf = null;
	
	
	public AreaAirDropSpellTrait() {
		Bukkit.getPluginManager().registerEvents(this, (Plugin)RacesAndClasses.getPlugin());
	}
	

	@Override
	public String getName() {
		return "AreaAirDropSpellTrait";
	}


	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class, optional = false),
			@TraitConfigurationField(fieldName = "rainRange", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "explosionRange", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "amount", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "aboveTarget", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "material", classToExpect = Material.class, optional = true),
			@TraitConfigurationField(fieldName = "materialDamageValue", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "particle", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "particleFromSelf", classToExpect = String.class, optional = true),
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("rainRange")){
			rainRange = configMap.getAsInt("rainRange");
		}
		
		if(configMap.containsKey("explosionRange")){
			explosionRange = configMap.getAsInt("explosionRange");
		}
		
		if(configMap.containsKey("amount")){
			amount = configMap.getAsInt("amount");
		}
		
		if(configMap.containsKey("aboveTarget")){
			aboveTarget = configMap.getAsInt("aboveTarget");
		}
		
		if(configMap.containsKey("material")){
			mat = configMap.getAsMaterial("material");
			if(!mat.isBlock()) mat = Material.GLOWSTONE;
		}
		
		if(configMap.containsKey("materialDamageValue")){
			matDamageValue = configMap.getAsInt("materialDamageValue");
		}
		
		if(configMap.containsKey("damage")){
			damage = configMap.getAsDouble("damage");
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
	

	@TraitInfos(category = "magic", traitName = "AreaAirDropSpellTrait", visible = true)
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
	protected String getPrettyConfigIntern() {
		return "Does damage on Stuff around.";
	}

	
	private final String DELETE_ON_DROP = "DELETE_ON_DROP";
	
	private Location getLookLocation(RaCPlayer player){
		BlockIterator blockIt = new BlockIterator(player.getPlayer());
		
		int i = 0;
		Location backup = null;
		Block current = blockIt.next();
		
		while(blockIt.hasNext()){
			current = blockIt.next();
			if(current.getType() != Material.AIR) return current.getLocation();
			
			i++;
			if(i == 10) backup = current.getLocation();
			if(i > 200) return backup;
		}
		
		return current.getLocation();
	}
	
	@EventHandler
	public void chunkUnload(ChunkUnloadEvent event){
		for(Entity entity : Arrays.asList(event.getChunk().getEntities())){
			if(entity.hasMetadata(DELETE_ON_DROP)) entity.remove();
		}
	}
	
	@EventHandler
	public void fallingBlockTransform(EntityChangeBlockEvent event){
		if(event.isCancelled()) return; //already handled!
		
		if(event.getEntity().hasMetadata(DELETE_ON_DROP)) {
			RaCPlayer player = (RaCPlayer) event.getEntity().getMetadata(DELETE_ON_DROP).get(0).value();
			if(player == null || !player.getTraits().contains(this)) return; //not the player's trait.
			
			//here we are sure this belongs to us!
			event.getEntity().remove();
			event.setCancelled(true);
			
			if(particles != null) Vollotile.get().sendOwnParticleEffectToAll(particles, event.getBlock().getLocation());
			
			//We got an exploded Block here!
			List<Entity> around = SearchEntity.inCircleAround(event.getEntity(), explosionRange);
			for(Entity entity : around){
				if(!EnemyChecker.areAllies(player.getPlayer(), entity)){
					explosionHitOnTarget(player, entity);
				}
			}
		}
	}
	
	
	public void explosionHitOnTarget(RaCPlayer damager, Entity targetEntity){		
		if(!(targetEntity instanceof LivingEntity)) return;
		if(damager.getPlayer() == targetEntity) return; //cant hurt yourself.
		
		LivingEntity target = (LivingEntity) targetEntity;
		double modDamage = modifyToPlayer(damager, this.damage, "damage");
		double damage = PreEntityDamageEvent.getRealDamage(damager.getPlayer(), target, DamageCause.CONTACT, modDamage);
		if(damage <= 0) return;
		
		target.setNoDamageTicks(0);
		de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.LivingEntity
			.safeDamageEntityByEntity((LivingEntity) target, damager.getPlayer(), damage);
		target.setNoDamageTicks(20);
		
		if(particles != null) Vollotile.get().sendOwnParticleEffectToAll(particles, target.getEyeLocation());
	}

	
	
	private List<Location> getRandomLocations(RaCPlayer player, Location loc){
		List<Location> locs = new LinkedList<Location>();
		if(rainRange <= 0) rainRange = 5;
		Random rand = new Random();
		
		int i = 0;
		int modAmount = modifyToPlayer(player, this.amount, "amount");
		
		while(locs.size() < modAmount){
			double xOffset = (rand.nextDouble() * rainRange * 2) - (rainRange);
			double zOffset = (rand.nextDouble() * rainRange * 2) - (rainRange);
			
			Location newLocation = loc.clone().add(xOffset, 0, zOffset);
			if(newLocation.distanceSquared(loc) <= rainRange * rainRange) locs.add(newLocation);
			
			i++;
			if(i>200) return locs; //safe out.
		}
		
		return locs;
	}


	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		Location lookLocation = getLookLocation(player);
		
		//middle point.
		Location middle = lookLocation.clone().add(0, aboveTarget, 0);
		
		List<Location> spawnLocs = getRandomLocations(player, middle);
		for(Location loc : spawnLocs){
			if(loc.getBlock().getType() != Material.AIR) continue;
			
			@SuppressWarnings("deprecation") //no alternatives to now.
			FallingBlock entity = loc.getWorld().spawnFallingBlock(loc, mat.getId(), (byte)matDamageValue);
			entity.setDropItem(false);
			
			entity.setMetadata(DELETE_ON_DROP, new FixedMetadataValue((Plugin) RacesAndClasses.getPlugin(), player));
		}
		
		if(particlesFromSelf != null) Vollotile.get()
			.sendOwnParticleEffectToAll(particlesFromSelf, player.getPlayer().getLocation());
		
		result.copyFrom(TraitResults.True());
	}
}
