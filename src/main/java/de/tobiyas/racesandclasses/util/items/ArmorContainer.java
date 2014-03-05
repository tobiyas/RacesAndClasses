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
package de.tobiyas.racesandclasses.util.items;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArmorContainer {

	private final ItemStack helmet;
	private final ItemStack chestPlate;
	private final ItemStack leggins;
	private final ItemStack boots;
	
	/**
	 * Caches the players Armory
	 * @param playerInv
	 */
	public ArmorContainer(PlayerInventory playerInv) {
		helmet = airOrClone(playerInv.getHelmet());
		chestPlate = airOrClone(playerInv.getChestplate());
		leggins = airOrClone(playerInv.getLeggings());
		boots = airOrClone(playerInv.getBoots());
	}

	/**
	 * clones an item.
	 * If item is null {@link Material.AIR} is taken
	 * 
	 * @param item
	 * @return
	 */
	private ItemStack airOrClone(ItemStack item){
		if(item == null){
			return new ItemStack(Material.AIR);
		}
		
		return item.clone();
	}
	
	/**
	 * Checks if the Items cached are the same as the player Inv.
	 * 
	 * Returns empty list if still the same.
	 * Returns all items changed.
	 * 
	 * @param playerInv
	 * @return
	 */
	public List<ItemStack> stillSame(PlayerInventory playerInv){
		List<ItemStack> changedItems = new LinkedList<ItemStack>();
		
		ItemStack helmet = airOrClone(playerInv.getHelmet());
		ItemStack chestPlate = airOrClone(playerInv.getChestplate());
		ItemStack leggins = airOrClone(playerInv.getLeggings());
		ItemStack boots = airOrClone(playerInv.getBoots());
		
		if(this.helmet.getType() != helmet.getType()){
			changedItems.add(helmet);
		}
		
		if(this.chestPlate.getType() != chestPlate.getType()){
			changedItems.add(chestPlate);
		}
		
		if(this.leggins.getType() != leggins.getType()){
			changedItems.add(leggins);
		}
		
		if(this.boots.getType() != boots.getType()){
			changedItems.add(boots);
		}
		
		return changedItems;
	}
}
