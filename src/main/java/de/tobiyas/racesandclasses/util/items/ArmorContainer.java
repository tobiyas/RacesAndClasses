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
