package de.tobiyas.racesandclasses.addins.kits;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

public class Kit {

	/**
	 * The Set of items in the Kit.
	 */
	private final Set<ItemStack> items = new HashSet<ItemStack>();
	
	/**
	 * The Cooldown in seconds.
	 */
	private final int cooldown;
	
	/**
	 * The name of the Kit.
	 */
	private final String kitName;
	
	
	public Kit(Set<ItemStack> items, int cooldown, String kitName) {
		this.items.addAll(items);
		this.cooldown = cooldown;
		this.kitName = kitName;
	}

	/**
	 * All items in the Kit.
	 * 
	 * @return All items in the kit. NO COPY.
	 */
	public Set<ItemStack> getItems() {
		return items;
	}


	/**
	 * The Cooldown of the Kit.
	 * 
	 * @return The Cooldown of the Kit.
	 */
	public int getCooldown() {
		return cooldown;
	}

	/**
	 * The Name of the Kit.
	 * 
	 * @return Kit name.
	 */
	public String getKitName() {
		return kitName;
	}
	
}
