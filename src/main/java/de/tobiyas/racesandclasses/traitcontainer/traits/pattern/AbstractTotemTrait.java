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
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
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
			Location loc = bEvent.getBlock().getLocation();
			
			for(TotemInfos totem : activedTotems.values()){
				if(loc.getWorld() == totem.topLocation.getWorld() &&
						loc.distanceSquared(totem.topLocation) < 0.5){
					//is part of a totem.
					removePlacedTotem(totem);
					bEvent.setCancelled(true);
					return result;
				}

				if(loc.getWorld() == totem.bottomLocation.getWorld() &&
						loc.distanceSquared(totem.bottomLocation) < 0.5){
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
	protected final Map<String, TotemInfos> activedTotems = new HashMap<String, TotemInfos>();
	

	@Override
	protected void magicSpellTriggered(Player player, TraitResults result) {
		String playerName = player.getName();
		Location location = getLocationNear(player);
		
		if(location == null){
			//no free space to place totem.
			result.setTriggered(false);
			return;
		}
		
		result.copyFrom(placeTotem(playerName, location));
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
	private Location getLocationNear(Player player) {
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
			@TraitConfigurationField(fieldName = "tickEvery", classToExpect = Integer.class, optional = true)
		}
	)
	@Override
	public void setConfiguration(Map<String, Object> configMap)
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
	}

	
	/**
	 * Places to the player.
	 * 
	 * @param playerUUID the player to place to
	 * @param location the location to place at
	 */
	protected TraitResults placeTotem(String playerName, Location location){
		Location bottomLocation = location.clone();
		Location topLocation = location.clone().add(0, 1, 0);
		
		TotemInfos infos = new TotemInfos();
		infos.bottomLocation = bottomLocation.clone();
		infos.topLocation = topLocation.clone();
		infos.playerName = playerName;
		
		
		bottomLocation.getBlock().setType(bottomMaterial);
		topLocation.getBlock().setType(topMaterial);
		
		activedTotems.put(playerName, infos);
		scheduleRemove(infos);
		return TraitResults.True();
	}
	
	
	/**
	 * Schedules the romve of a totem.
	 * 
	 * @param playerUUID to remove
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
	 * <br>If it is overriden, {@link #tickOn(TotemInfos, Player)} is NOT called any more.
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
			tickOn(infos, player);
		}
		
	}
	
	/**
	 * The totem ticks on the player passed.
	 * <br>This method is ONLY fired, if {@link #totemTick(TotemInfos)} is NOT Overriden!
	 * 
	 * @param infos of the totem.
	 * @param player to tick on.
	 */
	protected abstract void tickOn(TotemInfos infos, Player player);
	
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
		
		activedTotems.remove(infos.playerName);
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
		for(TotemInfos info : activedTotems.values()){
			Location top = info.topLocation;
			Location bottom = info.bottomLocation;
			
			if(location.distanceSquared(top) < 0.5 || location.distanceSquared(bottom) < 0.5) return true;
		}
		
		return false;
	}
	
	protected static class TotemInfos{
		protected Location bottomLocation;
		protected Location topLocation;
		
		protected int bukkitSchedulerID;
		protected String playerName;
	}
}
