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
package de.tobiyas.racesandclasses.util.entitysearch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;

import de.tobiyas.util.math.Bresenham;

public class SearchEntity {

	
	/**
	 * Searches the first {@link LivingEntity} in the line of sight.
	 * 
	 * @param maxDistance to search in
	 * @param toSearchFrom to search from
	 * 
	 * @return the Found entity or null if none found.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends LivingEntity> T inLineOfSight(int maxDistance, LivingEntity toSearchFrom){
		List<Entity> nearEntities = toSearchFrom.getNearbyEntities(maxDistance * 2, maxDistance * 2, maxDistance * 2);
		Iterator<Entity> entityIt = nearEntities.iterator();
		while(entityIt.hasNext()){
			Entity entityToCheck = entityIt.next();
			boolean remove = false;
			if(!(entityToCheck instanceof LivingEntity)){
				remove = true;
			}

			try{
				@SuppressWarnings({ "unused" })
				T t = (T) entityToCheck; //This is just a check if it is T type.
			}catch(ClassCastException exp){
				remove = true;
			}

			if(remove){
				entityIt.remove();
			}
		}
		
		Iterator<Block> blockIt = new BlockIterator(toSearchFrom, maxDistance);
		while(blockIt.hasNext()){
			Block block = blockIt.next();
			
			for(Entity entity : nearEntities){
				double distance = block.getLocation().distanceSquared(entity.getLocation());
				if(distance < 2){
					return (T) entity;
				}
				
				if(entity instanceof LivingEntity){
					double distanceEye = block.getLocation().distanceSquared(((LivingEntity)entity).getEyeLocation());
					if(distanceEye < 2){
						return (T) entity;
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Searches the first {@link LivingEntity} in the line of sight.
	 * 
	 * @param maxDistance to search in
	 * @param toSearchFrom to search from
	 * 
	 * @return the Found entity or null if none found.
	 */
	public static LivingEntity inLineOfSight(int maxDistance, LivingEntity toSearchFrom, EntityType toSearch){
		List<Entity> nearEntities = toSearchFrom.getNearbyEntities(maxDistance * 2, maxDistance * 2, maxDistance * 2);
		Iterator<Entity> entityIt = nearEntities.iterator();
		while(entityIt.hasNext()){
			Entity entityToCheck = entityIt.next();
			boolean remove = false;
			
			if(!(entityToCheck instanceof LivingEntity)) remove = true;
			if(entityToCheck.getType() != toSearch) remove = true;
			
			if(remove) entityIt.remove();
		}
		
		Iterator<Block> blockIt = new BlockIterator(toSearchFrom, maxDistance);
		while(blockIt.hasNext()){
			Block block = blockIt.next();
			
			for(Entity entity : nearEntities){
				double distance = block.getLocation().distanceSquared(entity.getLocation());
				if(distance < 2){
					return (LivingEntity) entity;
				}

				double distanceEye = block.getLocation().distanceSquared(((LivingEntity)entity).getEyeLocation());
				if(distanceEye < 2){
					return (LivingEntity) entity;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * Searches the first {@link LivingEntity} in the line of sight.
	 * 
	 * @param maxDistance to search in
	 * @param toSearchFrom to search from
	 * 
	 * @return the Found entity or null if none found.
	 */
	public static Set<LivingEntity> allInLineOfSight(int maxDistance, LivingEntity toSearchFrom){
		List<Entity> nearEntities = toSearchFrom.getNearbyEntities(maxDistance * 2, maxDistance * 2, maxDistance * 2);
		Iterator<Entity> entityIt = nearEntities.iterator();
		while(entityIt.hasNext()){
			Entity entityToCheck = entityIt.next();
			if(!(entityToCheck instanceof LivingEntity)) entityIt.remove();
		}
		
		Set<LivingEntity> targets = new HashSet<LivingEntity>();
		Iterator<Block> blockIt = new BlockIterator(toSearchFrom, maxDistance);
		
		int i = 0;
		while(blockIt.hasNext()){
			Block block = blockIt.next();
			
			for(Entity entity : nearEntities){
				double distance = block.getLocation().distanceSquared(entity.getLocation());
				if(distance <= 2){
					targets.add((LivingEntity) entity);
					continue;
				}

				if(entity instanceof LivingEntity){
					double distanceEye = block.getLocation().distanceSquared(((LivingEntity) entity).getEyeLocation());
					if(distanceEye <= 2){
						targets.add((LivingEntity) entity);
						continue;
					}
				}
			}
			
			//end.
			i++;
			if(i >= maxDistance) break;
		}
		
		return targets;
	}
	
	
	/**
	 * Returns all Entities in the Range of the Entity.
	 * 
	 * @param around
	 * @param range
	 * @return
	 */
	public static List<Entity> inCircleAround(Entity around, double range){
		List<Entity> entities = around.getNearbyEntities(range, range, range);
		double rangeSpauare = range * range;
		Location base = around.getLocation();
		
		Iterator<Entity> it = entities.iterator();
		while(it.hasNext()){
			if(it.next().getLocation().distanceSquared(base) > rangeSpauare) it.remove();
		}
		
		return entities;
	}
	
	
	
	/**
	 * Returns all Locations from Location 1 to location 2.
	 * 
	 * @param loc1
	 * @param loc2
	 * 
	 * @return
	 */
	public static Queue<Location> getAllOnWay(Location loc1, Location loc2){
		return Bresenham.line3D(loc1, loc2);
	}
	
}
