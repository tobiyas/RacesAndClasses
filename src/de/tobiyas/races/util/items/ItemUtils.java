package de.tobiyas.races.util.items;

import org.bukkit.inventory.ItemStack;

public class ItemUtils {

	public enum ItemQuality{
		None(-1),
		Leather(0),
		Iron(1),
		Gold(2),
		Diamond(3),
		Fire(4);
		
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
			case 305 : return ItemQuality.Fire;
			
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
	
}
