package de.tobiyas.racesandclasses.util.entitysearch;

import java.util.Iterator;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;

public class SearchEntity {

	
	/**
	 * Searches the first {@link LivingEntity} in the line of sight.
	 * 
	 * @param maxDistance to search in
	 * @param toSearchFrom to search from
	 * 
	 * @return the Found entity or null if none found.
	 */
	public static LivingEntity inLineOfSight(int maxDistance, LivingEntity toSearchFrom){
		List<Entity> nearEntities = toSearchFrom.getNearbyEntities(maxDistance * 2, maxDistance * 2, maxDistance * 2);
		Iterator<Entity> entityIt = nearEntities.iterator();
		while(entityIt.hasNext()){
			if(!(entityIt.next() instanceof LivingEntity)){
				entityIt.remove();
			}
		}
		
		Iterator<Block> blockIt = new BlockIterator(toSearchFrom, maxDistance);
		while(blockIt.hasNext()){
			Block block = blockIt.next();
			
			for(Entity entity : nearEntities){
				double distance = block.getLocation().distanceSquared(entity.getLocation());
				if(distance < 1){
					return (LivingEntity) entity;
				}
			}
		}
		
		return null;
	}
	
}
