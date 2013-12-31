package de.tobiyas.racesandclasses.datacontainer.armorandtool;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

public class AllItemsPermission implements AbstractItemPermission {

	@Override
	public boolean hasPermission(ItemStack item) {
		return true;
	}

	@Override
	public boolean isAlreadyRegistered(ItemQuality quality) {
		return true;
	}

}
