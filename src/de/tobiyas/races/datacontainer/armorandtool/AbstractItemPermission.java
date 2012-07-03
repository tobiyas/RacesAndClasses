package de.tobiyas.races.datacontainer.armorandtool;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.util.items.ItemUtils.ItemQuality;

public interface AbstractItemPermission {

	public boolean hasPermission(ItemStack item);
	
	public boolean alreadyIsRegistered(ItemQuality quality);
}
