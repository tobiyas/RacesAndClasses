package de.tobiyas.racesandclasses.racbuilder.gui;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ItemGeneratorInterface extends InventoryView {	
	
	/**
	 * Simply generates an Item with the passed args.
	 * 
	 * @param material
	 * @param name
	 * @param lore
	 * @return
	 */
	protected ItemStack generateItem(Material material, String name, List<String> lore){
		return generateItem(material, (short) 0, name, lore);
	}
	
	/**
	 * Simply generates an Item with the passed args.
	 * 
	 * @param material
	 * @param name
	 * @param lore
	 * @return
	 */
	protected ItemStack generateItem(Material material, String name, String oneLineLore){
		return generateItem(material, (short)0, name, oneLineLore);
	}
	
	
	/**
	 * Simply generates an Item with the passed args.
	 * 
	 * @param material
	 * @param name
	 * @param lore
	 * @return
	 */
	protected ItemStack generateItem(Material material, short damageValue, String name, List<String> lore){
		ItemStack item = new ItemStack(material, 1, damageValue);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	/**
	 * Simply generates an Item with the passed args.
	 * 
	 * @param material
	 * @param name
	 * @param lore
	 * @return
	 */
	protected ItemStack generateItem(Material material, short damageValue, String name, String oneLineLore){
		List<String> lore = new LinkedList<String>();
		lore.add(oneLineLore);
		return generateItem(material, damageValue, name, lore);
	}

}
