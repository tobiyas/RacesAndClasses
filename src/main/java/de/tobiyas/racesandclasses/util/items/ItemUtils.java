package de.tobiyas.racesandclasses.util.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {

	public enum ItemQuality{
		None(-1),
		Leather(0),
		Iron(1),
		Gold(2),
		Diamond(3),
		Chain(4);
		
		private int value;
		
		ItemQuality(int value){
			this.value = value;
		}
		
		public int getValue(){
			return value;
		}
		
	}
	
	public static ItemQuality getItemValue(ItemStack stack){
		switch(stack.getTypeId()){
			case 298 :
			case 299 :
			case 300 :
			case 301 : return ItemQuality.Leather;
			
			case 302 :
			case 303 :
			case 304 :
			case 305 : return ItemQuality.Chain;
			
			case 306 :
			case 307 :
			case 308 :
			case 309 : return ItemQuality.Iron;
			
			case 310 :
			case 311 :
			case 312 :
			case 313 : return ItemQuality.Diamond;
			
			case 314 :
			case 315 :
			case 316 :
			case 317 : return ItemQuality.Gold;
		
			default: return ItemQuality.None;
		}
	}
	
	public static int getArmorValueOfItem(ItemStack stack){
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
	}
	
	
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
	 * 
	 * 
	 * @param player
	 * @param slot
	 * @return
	 */
	public static ItemStack getItemInArmorSlotOfPlayer(Player player, ArmorSlot slot){
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
			return null;
			
		default:
			return null;

		}
	}
	
}
