package de.tobiyas.racesandclasses.datacontainer.armorandtool;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.util.items.ItemUtils;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

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
	public boolean isAlreadyRegistered(ItemQuality quality) {
		return quality == this.quality;
	}
	
	@Override
	public String toString(){
		return quality.name();
	}
}
