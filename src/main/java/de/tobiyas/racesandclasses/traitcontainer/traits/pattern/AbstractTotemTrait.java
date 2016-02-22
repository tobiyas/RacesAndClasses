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
package de.tobiyas.racesandclasses.traitcontainer.traits.pattern;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.friend.TargetType;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public abstract class AbstractTotemTrait extends AbstractMagicSpellTrait{

	/**
	 * The Duration the totem lasts.
	 */
	protected int duration = 5;
	
	/**
	 * The Range of the Totem.
	 */
	protected int range = 10;
	
	/**
	 * The Target of this totem.
	 */
	protected TargetType target = TargetType.ALL;
	
	
	@TraitEventsUsed(bypassClasses = {BlockBreakEvent.class})
	@Override
	public void generalInit() {
	}
	
	
	/**
	 * Removes the totem if block of totem is destroyed.
	 */

	@Override
	protected TraitResults otherEventTriggered(EventWrapper eventWrapper, TraitResults result){
		result = TraitResults.False();
		
		if(eventWrapper.getEvent() instanceof BlockBreakEvent){
			BlockBreakEvent bEvent = (BlockBreakEvent) eventWrapper.getEvent();
			Block block = bEvent.getBlock();
			
			for(TotemInfos totem : activedTotems.values()) {
				if(block.equals(totem.topLocation.getBlock())){
					//is part of a totem.
					removePlacedTotem(totem);
					bEvent.setCancelled(true);
					return result;
				}

				if(block.equals(totem.bottomLocation.getBlock())){
					//is part of a totem.
					removePlacedTotem(totem);
					bEvent.setCancelled(true);
					return result;
				}
			}
		}
		
		return result;
	}



	/**
	 * The ticks every .
	 */
	protected int tickEvery = 1;
	
	/**
	 * The Material for the Top block
	 */
	protected Material topMaterial = Material.SKULL;
	
	/**
	 * The Material for the bottom block
	 */
	protected Material bottomMaterial = Material.FENCE;
	
	
	/**
	 * The Map of currently active Totems.
	 * <br>We assume a player can only have a totem once at a Time.
	 */
	protected final Map<RaCPlayer, TotemInfos> activedTotems = new HashMap<RaCPlayer, TotemInfos>();
	

	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		Location location = getLocationNear(player);
		
		if(location == null){
			//no free space to place totem.
			result.setTriggered(false);
			return;
		}
		
		result.copyFrom(placeTotem(player, location));
	}

	
	/**
	 * A random to use.
	 */
	private final Random rand = new Random();
	
	
	/**
	 * Returns a location near the player.
	 * 
	 * @param player to search for
	 * 
	 * @return a location near the player.
	 */
	private Location getLocationNear(RaCPlayer player) {
		Location base = player.getLocation().clone();

		List<Location> free = new LinkedList<Location>();
		for(int x = -2; x <= 2; x ++){
			for(int z = -2; z <= 2; z++){
				Location toCheck = base.clone().add(x, 0, z);
				if(isFree(toCheck.clone())) free.add(toCheck);
			}
		}
		
		//do some randomness among the totem.
		return free.isEmpty() ? null : free.get(rand.nextInt(free.size()));
	}
	
	/**
	 * Checks if the Location is free to place something on.
	 * <br>This also involves the location 1 block above.
	 * 
	 * @param location to check
	 * 
	 * @return true if is free.
	 */
	private boolean isFree(Location location){
		//check if bottom block is blocked.
		if(location.getBlock().getType() != Material.AIR) return false;
		
		//check if upper block is blocked.
		if(location.add(0, 1, 0).getBlock().getType() != Material.AIR) return false;
		
		return true;
	}


	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "bottomBlock", classToExpect = Material.class, optional = true),			
			@TraitConfigurationField(fieldName = "upperBlock", classToExpect = Material.class, optional = true),
			@TraitConfigurationField(fieldName = "range", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "tickEvery", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "target", classToExpect = String.class, optional = true)
		}
	)
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("duration")){
			duration = (Integer) configMap.get("duration");
		}

		if(configMap.containsKey("bottomBlock")){
			bottomMaterial = (Material) configMap.get("bootomBlock");
		}
		
		if(configMap.containsKey("upperBlock")){
			topMaterial = (Material) configMap.get("upperBlock");
		}
		
		if(configMap.containsKey("tickEvery")){
			tickEvery = (Integer) configMap.get("tickEvery");
		}
		
		if(configMap.containsKey("range")){
			range = (Integer) configMap.get("range");
		}
		
		if(configMap.containsKey("target")){
			String target = configMap.getAsString("target").toLowerCase();
			if(target.startsWith("all")) this.target = TargetType.ALL;
			if(target.startsWith("fr") || target.startsWith("ally")) this.target = TargetType.FRIEND;
			if(target.startsWith("e") || target.startsWith("fe")) this.target = TargetType.ENEMY;
		}
	}

	
	/**
	 * Places to the player.
	 * 
	 * @param player the player to place to
	 * @param location the location to place at
	 */
	protected TraitResults placeTotem(RaCPlayer player, Location location){
		Location bottomLocation = location.clone();
		Location topLocation = location.clone().add(0, 1, 0);
		
		TotemInfos infos = new TotemInfos();
		infos.bottomLocation = bottomLocation.clone();
		infos.topLocation = topLocation.clone();
		infos.owner = player;
		
		
		bottomLocation.getBlock().setType(bottomMaterial);
		topLocation.getBlock().setType(topMaterial);
		
		activedTotems.put(player, infos);
		scheduleRemove(infos);
		return TraitResults.True();
	}
	
	
	/**
	 * Schedules the romve of a totem.
	 * 
	 * @param player to remove
	 */
	protected void scheduleRemove(final TotemInfos infos){
		final int bukkitSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				totemTick(infos);
			}
		}, 0, tickEvery * 20);
		
		infos.bukkitSchedulerID = bukkitSchedulerID;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				removePlacedTotem(infos);
			}
		}, 20 * duration);
	}
	
	/**
	 * The Totem ticks.
	 * <br>This method CAN be overriden.
	 * <br>If it is overriden, {@link #tickOnPlayer(TotemInfos, Player)} is NOT called any more.
	 * 
	 * @param infos to the totem.
	 */
	protected void totemTick(TotemInfos infos){
		Location loc = infos.topLocation;
		List<Player> near = new LinkedList<Player>();
		int squaredRange = range * range;
		
		for(Player player : loc.getWorld().getPlayers()){
			if(player.getLocation().distanceSquared(loc) <= squaredRange){
				near.add(player);
			}
		}
		
		//tick on all players near.
		for(Player player : near){
			if(target == TargetType.ENEMY){
				if(!EnemyChecker.areEnemies(infos.getOwner().getPlayer(), player)) continue;
			}

			if(target == TargetType.FRIEND){
				if(!EnemyChecker.areAllies(infos.getOwner().getPlayer(), player)) continue;
			}
			
			tickOnPlayer(infos, player);
		}
		
		//now to the Monster ticks.
		List<LivingEntity> entities = new LinkedList<LivingEntity>();
		for(Entity entity : loc.getWorld().getEntities()){
			if(!(entity instanceof LivingEntity)) continue;
			if(entity.getLocation().distanceSquared(loc) <= squaredRange){
				entities.add((LivingEntity) entity);
			}
		}
		
		for(LivingEntity entity : entities){
			if(target == TargetType.ENEMY){
				if(!EnemyChecker.areEnemies(infos.getOwner().getPlayer(), entity)) continue;
			}

			if(target == TargetType.FRIEND){
				if(!EnemyChecker.areAllies(infos.getOwner().getPlayer(), entity)) continue;
			}
			
			tickOnNonPlayer(infos, entity);
		}
		
	}
	
	/**
	 * The totem ticks on the player passed.
	 * <br>This method is ONLY fired, if {@link #totemTick(TotemInfos)} is NOT Overriden!
	 * 
	 * @param infos of the totem.
	 * @param player to tick on.
	 */
	protected abstract void tickOnPlayer(TotemInfos infos, Player player);
	
	
	/**
	 * The Totem ticks on a NO player target.
	 * 
	 * @param infos of the totem
	 * @param entity to tick on.
	 */
	protected abstract void tickOnNonPlayer(TotemInfos infos, LivingEntity entity);
	
	
	/**
	 * Removes a placed Totem.
	 * <br>A totem is defined as a 
	 * 
	 * @param location to remove from.
	 */
	protected void removePlacedTotem(TotemInfos infos){
		Location bottomLocation = infos.bottomLocation;
		Location topLocation = infos.topLocation;
		if(bottomLocation == null || topLocation == null) return;
		
		bottomLocation.getBlock().setType(Material.AIR);
		topLocation.getBlock().setType(Material.AIR);
		
		activedTotems.remove(infos.owner);
		Bukkit.getScheduler().cancelTask(infos.bukkitSchedulerID);
	}
	
	
	/**
	 * checks if the location is a totem.
	 * 
	 * @param location to check
	 * 
	 * @return true if it is
	 */
	protected boolean isTotem(Location location){
		Block block = location.getBlock();
		for(TotemInfos info : activedTotems.values()){
			Location top = info.topLocation;
			Location bottom = info.bottomLocation;
			
			if(top.getBlock() == block || bottom.getBlock() == block) return true;
		}
		
		return false;
	}
	
	protected static class TotemInfos{
		protected Location bottomLocation;
		protected Location topLocation;
		
		protected int bukkitSchedulerID;
		protected RaCPlayer owner;
		
		
		public Location getBottomLocation() {
			return bottomLocation.clone();
		}
		public Location getTopLocation() {
			return topLocation.clone();
		}
		public int getBukkitSchedulerID() {
			return bukkitSchedulerID;
		}
		public RaCPlayer getOwner() {
			return owner;
		}
		
		
	}
}
