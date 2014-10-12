package de.tobiyas.racesandclasses.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class MCPrettyName {

	
	public static String getPrettyName(Material material, short damage, String lang){
		return de.tobiyas.util.naming.MCPrettyName.getPrettyName(material, damage, lang);
	}
	
	public static String getPrettyName(Material material){
		return getPrettyName(material, (short)0, "de_DE");
	}
	
	public static String getPrettyName(EntityType entity, String lang){
		return de.tobiyas.util.naming.MCPrettyName.getPrettyName(entity, lang);
	}
	
	public static String getPrettyName(EntityType entity){
		return getPrettyName(entity, "de_DE");
	}
	
	public static String getPrettyName(ItemStack item, String lang){
		return de.tobiyas.util.naming.MCPrettyName.getPrettyName(item, lang);
	}
	
	public static String getPrettyName(ItemStack item){
		return getPrettyName(item, "de_DE");
	}
}
