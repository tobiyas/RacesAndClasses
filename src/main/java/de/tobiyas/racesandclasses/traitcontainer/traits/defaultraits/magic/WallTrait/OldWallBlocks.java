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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.WallTrait;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;

public class OldWallBlocks {
	
	private Map<Location, BlockData> blockList = new HashMap<Location, BlockData>();
	
	
	private final Material newMaterial;
	
	private class BlockData{
		Material material;
		byte data;
	}
			
	public OldWallBlocks(Location from, Location to, Material newMaterial){
		this.newMaterial = newMaterial;
		
		generateBlockList(from, to);
	}
	
	/**
	 * Removes the old wall
	 */
	@SuppressWarnings("deprecation")
	public void removeWall() {
		for(Map.Entry<Location, BlockData> entry : blockList.entrySet()){
			BlockData data = entry.getValue();
			Location location = entry.getKey();
			
			location.getBlock().setType(data.material);
			location.getBlock().setData(data.data);
		}
	}

	@SuppressWarnings("deprecation")
	private void generateBlockList(Location from, Location to){
		
		int smaller = from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY();
		int larger = from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY();
		
		for(int i = smaller; i <= larger; i++){
			for(Location location : getLocationsBetween(from, to, i)){
				if(!(location.getBlock().getState() instanceof InventoryHolder)){
					if(location.getBlock().getType() != Material.AIR) continue;
					
					BlockData data = new BlockData();
					data.data = location.getBlock().getData();
					data.material = location.getBlock().getType();
					
					blockList.put(location, data);
					
					location.getBlock().setType(newMaterial);
				}
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public void remove(Block block){
		for(Location loc : blockList.keySet()){
			if(loc.getBlock().equals(block)){
				BlockData data = blockList.get(loc);
				block.setTypeIdAndData(data.material.getId(), data.data, true);
				
				blockList.remove(loc);
				return;
			}
		}
	}
	

	private List<Location> getLocationsBetween(Location from, Location to, int height) {
		List<Location> locations = new LinkedList<Location>();
		
		if(from.getBlockX() == to.getBlockX()){
			int smaller = from.getBlockZ() < to.getBlockZ() ? from.getBlockZ() : to.getBlockZ();
			int larger = from.getBlockZ() > to.getBlockZ() ? from.getBlockZ() : to.getBlockZ();
			
			for(int i = smaller; i <= larger; i++){
				Location newLocation = from.clone();
				newLocation.setZ(i);
				newLocation.setY(height);
				
				locations.add(newLocation);
			}
		} else if(from.getBlockZ() == to.getBlockZ()){
			int smaller = from.getBlockX() < to.getBlockX() ? from.getBlockX() : to.getBlockX();
			int larger = from.getBlockX() > to.getBlockX() ? from.getBlockX() : to.getBlockX();
			
			for(int i = smaller; i <= larger; i++){
				Location newLocation = from.clone();
				newLocation.setX(i);
				newLocation.setY(height);
				
				locations.add(newLocation);
			}
		}else if(from.getBlockY() == to.getBlockY()){
			int smallerX = from.getBlockX() < to.getBlockX() ? from.getBlockX() : to.getBlockX();
			int largerX = from.getBlockX() > to.getBlockX() ? from.getBlockX() : to.getBlockX();
			
			for(int i = smallerX; i <= largerX; i++){
				
				int smallerY = from.getBlockZ() < to.getBlockZ() ? from.getBlockZ() : to.getBlockZ();
				int largerY = from.getBlockZ() > to.getBlockZ() ? from.getBlockZ() : to.getBlockZ();
				
				for(int j = smallerY; j <= largerY; j++){
					
					Location newLocation = from.clone();
					newLocation.setX(i);
					newLocation.setZ(j);
					newLocation.setY(height);
					
					locations.add(newLocation);
				}
			}
		}
		
		return locations;
	}
	
	public boolean contains(Location blockLocation){
		Block block = blockLocation.getBlock();
		
		for(Location location : blockList.keySet()){
			if(block.equals(location.getBlock())){
				return true;
			}
		}
		
		return false;
	}
}
