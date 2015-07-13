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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.*;

public class ItemUtils {

	public static class ItemQuality{
		
		public static final ItemQuality None = new ItemQuality("none");
		public static final ItemQuality Leather = new ItemQuality("leather", new Material[]{ LEATHER_BOOTS, LEATHER_LEGGINGS, LEATHER_CHESTPLATE, LEATHER_HELMET });
		public static final ItemQuality Iron = new ItemQuality("iron", new Material[]{ IRON_BOOTS, IRON_LEGGINGS, IRON_CHESTPLATE, IRON_HELMET });
		public static final ItemQuality Gold = new ItemQuality("gold", new Material[]{ GOLD_BOOTS, GOLD_LEGGINGS, GOLD_CHESTPLATE, GOLD_HELMET });
		public static final ItemQuality Diamond = new ItemQuality("diamond", new Material[]{ DIAMOND_BOOTS, DIAMOND_LEGGINGS, DIAMOND_CHESTPLATE, DIAMOND_HELMET });
		public static final ItemQuality Chain = new ItemQuality("chain", new Material[]{ CHAINMAIL_BOOTS, CHAINMAIL_LEGGINGS, CHAINMAIL_CHESTPLATE, CHAINMAIL_HELMET });
		
		
		private static final List<ItemQuality> allValues = new LinkedList<ItemQuality>();
		
		
		static{
			allValues.add(None);
			allValues.add(Leather);
			allValues.add(Iron);
			allValues.add(Gold);
			allValues.add(Diamond);
			allValues.add(Chain);
		}
		
		
		private String name;
		private final Collection<Material> mats;
		
		private ItemQuality(String name){
			this(name, new Material[]{});
		}
		
		private ItemQuality(String name, Material[] materials){
			this(name, Arrays.asList(materials));
		}
		
		private ItemQuality(String name, Collection<Material> materials){
			this.name = name;
			this.mats = materials;
		}
		
		public String getName() {
			return name;
		}
		
		
		public Collection<Material> getMats() {
			return new HashSet<Material>(mats);
		}
		
		
		public static Collection<ItemQuality> values(){
			return new HashSet<ItemQuality>(allValues);
		}
		
		
		/**
		 * Reparses the ItemStuff.
		 */
		public static void reparse(Map<String,String> qualityMap){
			allValues.clear();
			
			for(Map.Entry<String,String> entry : qualityMap.entrySet()){
				String key = entry.getKey().toLowerCase();
				String toParse = entry.getValue();
				
				String[] vals = toParse.split(Pattern.quote(","));
				Set<Material> mats = new HashSet<Material>();
				
				if(vals != null && vals.length != 0){
					for(String val : vals){
						Material mat = Material.matchMaterial(val);
						if(mat != null) mats.add(mat);
					}
				}
				
				if(mats.size() > 0) allValues.add(new ItemQuality(key, mats));
			}
			
			allValues.add(None);
		}
		

		/**
		 * Resets the ItemQualities.
		 * 
		 * @param toSet to set.
		 */
		public static void set(Map<String, Set<Material>> toSet) {
			allValues.clear();
			for(Map.Entry<String, Set<Material>> entry : toSet.entrySet()){
				ItemQuality quality = new ItemQuality(entry.getKey(), entry.getValue());
				allValues.add(quality);
			}
			
			allValues.add(None);
		}
		

		/**
		 * Parses the ItemQuality from a String.
		 * 
		 * @param armorString to parse.
		 * 
		 * @return the parsed ArmorString
		 */
		public static Collection<ItemQuality> parse(String armorString) {			
			Set<ItemQuality> set = new HashSet<ItemQuality>();
			if(armorString == null || armorString.isEmpty()) return set;
			
			if(armorString.equalsIgnoreCase("all")) return values();
			
			String[] parts = armorString.split(Pattern.quote(","));
			if(parts != null && parts.length > 0){
				for(String part : parts){
					ItemQuality quality = parseSingle(part);
					if(quality != null) set.add(quality);
				}				
			}
			
			return set;
		}
		
		
		private static ItemQuality parseSingle(String key){
			key = key.toLowerCase();
			
			for(ItemQuality quality : allValues){
				if(key.equals(quality.getName())){
					return quality;
				}
			}
			
			return null;
		}

		/**
		 * Loads the Default.
		 */
		public static void loadDefault() {
			allValues.clear();
			
			allValues.add(Chain);
			allValues.add(Diamond);
			allValues.add(Gold);
			allValues.add(Iron);
			allValues.add(Leather);
			
			allValues.add(None);
		}
		
	}
	
	
	/**
	 * Gets the Item Quality of a piece of armor
	 * 
	 * @param stack to check
	 * @return
	 */
	public static ItemQuality getItemValue(ItemStack stack){
		if(stack == null) return ItemQuality.None;
		
		Material type = stack.getType();
		
		for(ItemQuality quality : ItemQuality.values()){
			if(quality.getMats().contains(type)) return quality;
		}
		
		return ItemQuality.None;
	}
	
	
	/**
	 * Returns the robustness of an Item.
	 * This is only applied for Armor.
	 * 
	 * @param stack to check
	 * @return int representation of robustness
	 */
	/*public static int getArmorValueOfItem(ItemStack stack){
		if(stack == null || stack.getType() == Material.AIR) return 0;
		
		switch(stack.getType()){
			//LeatherArmor stuff
			case LEATHER_BOOTS: return 1;
			case LEATHER_HELMET: return 1;
			case LEATHER_LEGGINGS: return 2;
			case LEATHER_CHESTPLATE: return 3;
			
			//GoldArmor stuff
			case GOLD_BOOTS: return 1;
			case GOLD_HELMET: return 2;
			case GOLD_LEGGINGS: return 3;
			case GOLD_CHESTPLATE: return 5;
			
			//ChainArmor stuff
			case CHAINMAIL_BOOTS: return 1;
			case CHAINMAIL_HELMET: return 2;
			case CHAINMAIL_LEGGINGS: return 4;
			case CHAINMAIL_CHESTPLATE: return 5;
			
			//IronArmor stuff
			case IRON_BOOTS: return 2;
			case IRON_HELMET: return 2;
			case IRON_LEGGINGS: return 5;
			case IRON_CHESTPLATE: return 6;
			
			//DiamondArmor stuff
			case DIAMOND_BOOTS: return 3;
			case DIAMOND_HELMET: return 3;
			case DIAMOND_LEGGINGS: return 6;
			case DIAMOND_CHESTPLATE: return 8;
			
			default: return 0;
		}
	}*/
	
	
	/**
	 * Linked to the Armor slots in Minecraft.
	 */
	public enum ArmorSlot{
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS,
		NONE;
	}
	
	/**
	 * Returns the armor slot the item can be equipped to.
	 * Returns -1 if NO armor item.
	 * 
	 * @param item
	 * @return
	 */
	public static ArmorSlot getItemSlotEquiping(ItemStack item){
		if(item == null){
			return ArmorSlot.NONE;
		}
		
		switch(item.getType()){
			//Helmets:	
			case LEATHER_HELMET:
			case GOLD_HELMET:
			case CHAINMAIL_HELMET:
			case IRON_HELMET:
			case DIAMOND_HELMET: return ArmorSlot.HELMET;
			
			//Chest:	
			case LEATHER_CHESTPLATE:
			case GOLD_CHESTPLATE:
			case CHAINMAIL_CHESTPLATE:
			case IRON_CHESTPLATE:
			case DIAMOND_CHESTPLATE: return ArmorSlot.CHESTPLATE;
			
			//Legs:	
			case LEATHER_LEGGINGS:
			case GOLD_LEGGINGS:
			case CHAINMAIL_LEGGINGS:
			case IRON_LEGGINGS:
			case DIAMOND_LEGGINGS: return ArmorSlot.LEGGINGS;
			
			//feet:	
			case LEATHER_BOOTS:
			case GOLD_BOOTS:
			case CHAINMAIL_BOOTS:
			case IRON_BOOTS:
			case DIAMOND_BOOTS: return ArmorSlot.BOOTS;
		
		
			default: return ArmorSlot.NONE;
		}
	}
	
	
	/**
	 * Gets the ItemStack in the Armor Inventory slot of a Player.
	 * Returns null if {@link ItemStack#NONE} is passed.
	 * 
	 * @param player to check
	 * @param slot to check
	 * 
	 * @return the itemStack in the slot
	 */
	public static ItemStack getItemInArmorSlotOfPlayer(Player player, ArmorSlot slot){
		if(slot == null){
			return null;
		}
		
		switch (slot) {
			case BOOTS:
				return player.getInventory().getBoots();
			
			case CHESTPLATE:
				return player.getInventory().getChestplate();
			
			case HELMET:
				return player.getInventory().getHelmet();
			
			case LEGGINGS:
				return player.getInventory().getLeggings();
			
			case NONE:
			default:
				return null;
				
		}
	}
	
}
