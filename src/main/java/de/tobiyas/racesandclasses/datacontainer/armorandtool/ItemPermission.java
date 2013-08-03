package de.tobiyas.racesandclasses.datacontainer.armorandtool;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

public class ItemPermission implements AbstractItemPermission {

	/**
	 * The Item to check against
	 */
	private final Material material;
	
	
	/**
	 * Creates an Item Permission for a specific Item.
	 * 
	 * @param material to check
	 */
	public ItemPermission(Material material) {
		this.material = material;
	}

	
	@Override
	public boolean hasPermission(ItemStack item) {
		return item.getType() == material;
	}

	
	@Override
	public boolean isAlreadyRegistered(ItemQuality quality) {
		return false;
	}

}
