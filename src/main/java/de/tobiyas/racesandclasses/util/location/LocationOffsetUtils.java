package de.tobiyas.racesandclasses.util.location;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class LocationOffsetUtils {

	
	/**
	 * Returns a random Location around the one passed.
	 * 
	 * @param location to use
	 * 
	 * @return the random location.
	 */
	public static Location getRandomAround(Location location){
		Block base = location.getBlock();
		if(base.getRelative(BlockFace.EAST).getType() == Material.AIR) return base.getRelative(BlockFace.EAST).getLocation();
		if(base.getRelative(BlockFace.SOUTH).getType() == Material.AIR) return base.getRelative(BlockFace.SOUTH).getLocation();
		if(base.getRelative(BlockFace.WEST).getType() == Material.AIR) return base.getRelative(BlockFace.WEST).getLocation();
		if(base.getRelative(BlockFace.NORTH).getType() == Material.AIR) return base.getRelative(BlockFace.NORTH).getLocation();
		
		return location;
	}
	
}
