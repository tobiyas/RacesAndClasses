package de.tobiyas.races.datacontainer.armorandtool;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.util.items.ItemUtils;
import de.tobiyas.races.util.items.ItemUtils.ItemQuality;

public class MaterialArmorPermission implements AbstractItemPermission{

	private ItemQuality quality;
	
	public MaterialArmorPermission(ItemQuality quality){
		this.quality = quality;
	}

	@Override
	public boolean hasPermission(ItemStack item) {
		return ItemUtils.getItemValue(item) == quality;
	}

	@Override
	public boolean alreadyIsRegistered(ItemQuality quality) {
		return quality == this.quality;
	}
}
