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


	public Set<ItemStack> getItems() {
		return items;
	}


	public int getCooldown() {
		return cooldown;
	}


	public String getKitName() {
		return kitName;
	}
	
}
