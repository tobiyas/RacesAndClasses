package de.tobiyas.racesandclasses.APIs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.listeners.equipement.Listener_raceClassRestrictionOnItems;

public class ItemRestrictionAPI {

	/**
	 * Adds the Restriction for the Race on the item.
	 * 
	 * @param item to add to.
	 * @param raceName to add
	 */
	public static void addRaceRestriction(ItemStack item, String raceName){
		if(raceName == null) return;
		RaceContainer container = RaceAPI.getRaceByName(raceName);
		if(container == null) return; //nothing found.
		
		String pre =  ChatColor.RED + Listener_raceClassRestrictionOnItems.RACE_RESTRICTION_PRE.get(0);
		String value = ChatColor.AQUA + "" + container.getDisplayName();
		
		addStringToLore(item, pre, value);
	}
	

	
	
	/**
	 * Adds the Restriction for the Class on the item.
	 * 
	 * @param item to add to.
	 * @param className to add
	 */
	public static void addClassRestriction(ItemStack item, String className){
		if(className == null) return;
		ClassContainer container = ClassAPI.getClassByName(className);
		if(container == null) return; //nothing found.
		
		String pre =  ChatColor.RED + Listener_raceClassRestrictionOnItems.CLASS_RESTRICTION_PRE.get(0);
		String value = ChatColor.AQUA + "" + container.getDisplayName();
		
		addStringToLore(item, pre, value);
	}

	/**
	 * Adds the Restriction for the Level on the item.
	 * 
	 * @param item to add to.
	 * @param level to add
	 */
	public static void addLevelRestriction(ItemStack item, int level){
		if(level <= 0) return;
		
		String pre =  ChatColor.RED + Listener_raceClassRestrictionOnItems.LEVEL_RESTRICTION_PRE.get(0);
		String value = ChatColor.AQUA + "" + level;
		
		addStringToLore(item, pre, value);
	}
	
	
	/**
	 * gets the level restriction on the Item
	 * 
	 * @param item to get from.
	 */
	public static int getLevelRestiction(ItemStack item){
		if(item == null) return 0;
		
		if(!item.hasItemMeta()) return 0;
		
		ItemMeta meta = item.getItemMeta();
		if(!meta.hasLore()) return 0;
		
		for(String loreString : meta.getLore()){
			loreString = loreString.toLowerCase();
			loreString = ChatColor.stripColor(loreString);
		
		
			for(String recog : Listener_raceClassRestrictionOnItems.LEVEL_RESTRICTION_PRE){
				if(loreString.startsWith(recog)){
					String needed = loreString.split(": ")[1];
					try{
						return Integer.parseInt(needed);
					}catch(NumberFormatException exp){}
				}
			}
		}
		
		return 0;
	}
	
	
	/**
	 * Adds the Pre to the Lore.
	 * 
	 * @param item
	 * @param pre
	 * @param value
	 */
	private static void addStringToLore(ItemStack item, String pre, String value){
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new LinkedList<String>();
		removeOld(lore, pre);
		
		lore.add(pre + value);
		meta.setLore(lore);
		
		item.setItemMeta(meta);
	}
	
	/**
	 * Checks if the Lore alread contains this.
	 * 
	 * @param lore to edit.
	 * @param pre to search.
	 * 
	 * @return
	 */
	private static void removeOld(List<String> lore, String pre){
		pre = ChatColor.stripColor(pre);
		pre = pre.toLowerCase();
		
		Iterator<String> it = lore.iterator();
		while(it.hasNext()){
			String toCheck = it.next();
			toCheck = ChatColor.stripColor(toCheck);
			toCheck = toCheck.toLowerCase();
			
			if(toCheck.startsWith(pre)){
				it.remove();
			}
		}
	}
	
}
