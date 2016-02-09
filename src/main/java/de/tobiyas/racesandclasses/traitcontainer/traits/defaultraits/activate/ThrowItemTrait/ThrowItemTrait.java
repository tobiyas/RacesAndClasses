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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.ThrowItemTrait;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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

public class ThrowItemTrait extends AbstractMagicSpellTrait implements Listener {

	/**
	 * The Damage to do.
	 */
	protected double damage = 1;
	
	protected boolean addSmallOffset = false;
	
	protected Material mat = Material.WOOD_SWORD;
	
	protected double speed = 3;
	
	protected int matDamageValue = 0;
	
	protected ParticleContainer particles = null;

	protected ParticleContainer particlesFromSelf = null;
	
	protected final Set<Item> flying = new HashSet<Item>();
	
	
	public ThrowItemTrait() {
		Bukkit.getPluginManager().registerEvents(this, (Plugin)RacesAndClasses.getPlugin());
		new BukkitRunnable(){
			@Override
			public void run() {
				Iterator<Item> itemIt = flying.iterator();
				while(itemIt.hasNext()){
					Item item = itemIt.next();
					if(!item.isValid() 
							|| item.getVelocity().length() <= 0.1) { //laying still.
						item.remove();
						itemIt.remove();
						continue;
					}
					
					List<Entity> nearList = SearchEntity.inCircleAround(item, 2);
					for(Entity near : nearList){
						if(!(near instanceof LivingEntity)) continue;
						if(!item.hasMetadata(DELETE_ON_DROP)) continue;
						
						//check for near middle of character.
						if(near.getLocation().add(0,1,0).distanceSquared(item.getLocation()) > 1) continue;
						
						RaCPlayer thrower = (RaCPlayer) item.getMetadata(DELETE_ON_DROP).get(0).value();
						if(thrower.getPlayer() == near) continue;
						
						entityGotHitByItem(thrower, (LivingEntity) near);
						item.remove();
						itemIt.remove();
						break;
						
					}
				}
			}
		}.runTaskTimer((Plugin)RacesAndClasses.getPlugin(), 1, 1);
	}
	
	
	@Override
	public void deInit() {
		super.deInit();
		
		for(Item item : flying){
			if(item.isValid()) item.remove();
		}
	}
	

	@Override
	public String getName() {
		return "ThrowItemTrait";
	}


	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class, optional = false),
			@TraitConfigurationField(fieldName = "speed", classToExpect = Double.class, optional = true),
			@TraitConfigurationField(fieldName = "material", classToExpect = Material.class, optional = true),
			@TraitConfigurationField(fieldName = "materialDamageValue", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "particle", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "particleFromSelf", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "addSmallOffset", classToExpect = Boolean.class, optional = true),
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("material")){
			mat = configMap.getAsMaterial("material");
		}
		
		if(configMap.containsKey("addSmallOffset")){
			addSmallOffset = configMap.getAsBool("addSmallOffset");
		}
		
		if(configMap.containsKey("materialDamageValue")){
			matDamageValue = configMap.getAsInt("materialDamageValue");
		}
		
		if(configMap.containsKey("damage")){
			damage = configMap.getAsDouble("damage");
		}
		
		if(configMap.containsKey("speed")){
			speed = configMap.getAsDouble("speed");
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
		helpList.add(ChatColor.YELLOW + "This Trait Throws an Item in the Direction you are looking at.");
		return helpList;
	}
	

	@TraitInfos(category = "activate", traitName = "ThrowItemTrait", visible = true)
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
		return "Does damage on Stuff touching the Thrown Item.";
	}

	
	private final String DELETE_ON_DROP = "ITEM_DELETE_ON_DROP";
	private final Random rand = new Random();

	
	private void removeItemAfterX(final Item droppedItem, int time) {
		new BukkitRunnable(){
			@Override
			public void run() {
				if(droppedItem.isValid()){
					droppedItem.remove();
				}
			}
		}.runTaskLater((Plugin) RacesAndClasses.getPlugin(), 20 * time);
	}


	@EventHandler
	public void chunkUnload(ChunkUnloadEvent event){
		for(Entity entity : Arrays.asList(event.getChunk().getEntities())){
			if(entity.hasMetadata(DELETE_ON_DROP)) entity.remove();
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerPickup(PlayerPickupItemEvent event){
		Item item = event.getItem();
		if(item.hasMetadata(DELETE_ON_DROP)){
			RaCPlayer owner = (RaCPlayer) item.getMetadata(DELETE_ON_DROP).get(0).value();
			if(owner == null) return;
			
			event.setCancelled(true);
			if(owner.getPlayer() == event.getPlayer()) return;
			
			if(entityGotHitByItem(owner, event.getPlayer())){
				item.remove();
				flying.remove(item);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void hopperPickup(InventoryPickupItemEvent event){
		Item item = event.getItem();
		if(item.hasMetadata(DELETE_ON_DROP)){
			event.setCancelled(true);
		}
	}
	
	
	/**
	 * Indicates that a Hit has occured.
	 * 
	 * @param thrower that threw
	 * @param target that was hit.
	 * 
	 * @return true if item should be removed.
	 */
	private boolean entityGotHitByItem(RaCPlayer thrower, LivingEntity target){		
		if(thrower.getPlayer() == target) return false;
		if(!EnemyChecker.areEnemies(thrower.getPlayer(), target)) return false;
		
		double damage = PreEntityDamageEvent.getRealDamage(thrower.getPlayer(), target, DamageCause.CONTACT, modifyToPlayer(thrower, this.damage, "damage"));
		if(damage <= 0) return false;
		
		de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.LivingEntity
			.safeDamageEntityByEntity(target, thrower.getPlayer(), damage);
		
		if(particles != null) Vollotile.get().sendOwnParticleEffectToAll(particles, target.getEyeLocation());
		return true;
	}


	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		ItemStack item = new ItemStack(mat);
		item.setDurability((byte)matDamageValue);
		Item droppedItem = player.getLocation().getWorld().dropItem(player.getLocation().clone().add(0,1.5,0), item);
		
		droppedItem.setItemStack(item);
		
		Vector velocity = player.getPlayer().getLocation().getDirection().clone()
				.multiply(speed);
		if(addSmallOffset){
			Vector offset = new Vector();
			offset.setX(rand.nextDouble() * 0.1 - 0.05);
			offset.setY(rand.nextDouble() * 0.1 - 0.05);
			offset.setZ(rand.nextDouble() * 0.1 - 0.05);
			
			velocity = velocity.add(offset);
		}
		
		//starts in direction of player sight.
		droppedItem.setVelocity(velocity);
		droppedItem.setMetadata(DELETE_ON_DROP, new FixedMetadataValue((Plugin) RacesAndClasses.getPlugin(), player));
		
		//set pickup time to 1 to pick it up as soon as possible.
		droppedItem.setPickupDelay(1);
		removeItemAfterX(droppedItem, 5);
		
		flying.add(droppedItem);
		
		if(particlesFromSelf != null) Vollotile.get().sendOwnParticleEffectToAll(particlesFromSelf, 
				player.getLocation().clone().add(0,1,0));
		
		result.copyFrom(TraitResults.True());
	}
}
