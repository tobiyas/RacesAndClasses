/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.util.items;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemMetaUtils {

	/**
	 * Sets the DisplayName of the Item passed
	 * If the String is longer than 32 symbols, it is trimmed.
	 * 
	 * @param item to set
	 * @param newDisplayName to set
	 */
	public static void setDisplayNameOfItem(ItemStack item, String newDisplayName){
		if(newDisplayName.length() > 32) newDisplayName = newDisplayName.substring(0, 32);
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(newDisplayName);
		item.setItemMeta(meta);
	}
	
	
	/**
	 * Adds a String to the Lore of an Item
	 * 
	 * @param item to add to
	 * @param toAdd to add
	 */
	public static void addStringToLore(ItemStack item, String toAdd){
		ItemMeta meta = item.getItemMeta();
		List<String> lore = null;
		if(meta.hasLore()){
			lore = meta.getLore();
		}else{
			lore = new LinkedList<String>();
		}
		
		lore.addAll(formatToLore(toAdd));
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	
	
	/**
	 * Formats the unformatted Configuration to a multi line Lore.
	 * 
	 * @param unformated
	 * @return
	 */
	public static List<String> formatToLore(String unformated){
		String[] words = unformated.split(" ");
		
		List<String> lore = new LinkedList<String>();
		String currentLine = ChatColor.YELLOW + " -" + words[0];
		for(int i = 1; i < words.length; i++){
			String currentWord = words[i];
			
			if(currentLine.length() + words.length + 1 > 29){
				lore.add(currentLine);
				currentLine = ChatColor.YELLOW + "  " + currentWord;
			}else{
				currentLine += " " + currentWord;
			}
		}
		if(currentLine.length() > 0){
			lore.add(currentLine);
		}
		
		return lore;
	}
	
	/**
	 * Replaces the Lore with the one passed
	 * 
	 * @param item
	 * @param newLore
	 */
	public static void replaceLoreWith(ItemStack item, String newLore){
		List<String> newLoreList = formatToLore(newLore);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(newLoreList);
		item.setItemMeta(meta);
	}
	
	
	/**
	 * Removes the Lore text from the passed
	 * 
	 * @param item
	 */
	public static void removeLore(ItemStack item){
		List<String> emptyList = new LinkedList<String>();
		
		ItemMeta meta = item.getItemMeta();
		meta.setLore(emptyList);
		item.setItemMeta(meta);
	}
}
