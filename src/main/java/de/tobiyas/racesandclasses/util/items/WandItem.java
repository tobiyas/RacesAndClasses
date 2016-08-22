package de.tobiyas.racesandclasses.util.items;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.util.formating.ParseUtils;

public class WandItem {

	/**
	 * The Material to use.
	 */
	private final Material material;
	
	/**
	 * THe Damage value to search.
	 * -1 for any.
	 */
	private final short damage;
	
	/**
	 * The ItemName to search.
	 */
	private final String itemName;
	
	/**
	 * THe lore line to search.
	 */
	private final String loreLine;

	
	public WandItem(Material material, short damage, String itemName, String loreLine) {
		super();
		this.material = material;
		this.damage = damage;
		this.itemName = itemName == null ? "" : ChatColor.translateAlternateColorCodes('&', itemName);
		this.loreLine = loreLine == null ? "" : ChatColor.translateAlternateColorCodes('&', loreLine);
	}
	
	
	
	public Material getMaterial() {
		return material;
	}

	public short getDamage() {
		return damage;
	}

	public String getItemName() {
		return itemName;
	}

	public String getLoreLine() {
		return loreLine;
	}

	
	/**
	 * Generates a demo item. Not good to use!
	 * @return the item.
	 */
	public ItemStack generateItem(){
		ItemStack item = new ItemStack(material);
		if(damage >= 0) item.setDurability(damage);
		
		if(itemName.isEmpty() && loreLine.isEmpty()) return item;
		
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return item;
		
		meta.setDisplayName(itemName);
		meta.setLore(Arrays.asList(loreLine));
		item.setItemMeta(meta);
		
		return item;
	}
	

	/**
	 * If this is the item passed.
	 * @param item to check.
	 * @return true if is item.
	 */
	public boolean isItem(ItemStack item){
		if(item == null) return false;
		if(material != item.getType()) return false;
		if(damage >= 0 && item.getDurability() != damage) return false;
		
		//early out if no name / lore:
		if(itemName.isEmpty() && loreLine.isEmpty()) return true;
		
		//Problem fix for no Meta:
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return false;
		
		//Check for name:
		String name = meta.hasDisplayName() ? meta.getDisplayName() : "";
		if(!name.equals(itemName)) return false;

		//Check for display:
		if(!loreLine.isEmpty() && !meta.hasLore()) return false;
		
		boolean found = false;
		for(String line : meta.getLore()){
			if(loreLine.equals(line)) found = true;
		}
		
		if(!found) return false;
		
		return true;
	}
	
	
	
	/**
	 * Generates the 
	 * @param line to split.
	 * @return the generated item or null if not parseable.
	 */
	public static WandItem generateFrom(String line){
		if(line == null || line.isEmpty()) return null;
		
		String[] split = line.split("##");
		if(split.length == 0 || split[0].isEmpty()) return null;
		
		Material material = ParseUtils.parseMaterial(split, 0, null);
		if(material == null) return null;
		
		short damage = ParseUtils.parseShort(split, 1, (short) -1);
		String name = ParseUtils.parseString(split, 2, "");
		String displayLine = ParseUtils.parseString(split, 3, "");
		
		return new WandItem(material, damage, name, displayLine);
	}
	
	
}
