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

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemContainer {

	private Material itemMaterial;
	private int damageValue;
	private int minAmount;
	private int maxAmount;
	private double probability;
	private int randomEnchants;
	
	private Random rand;
	
	public ItemContainer(int itemID, int damageValue, int minAmount, int maxAmount, double probability){
		this(itemID, damageValue, minAmount, maxAmount, probability, 0);
	}
	
	public ItemContainer(int itemID, int damageValue, int minAmount, int maxAmount, double probability, int randomEnchants){
		this.itemMaterial = Material.getMaterial(itemID);
		this.damageValue = damageValue;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.probability = probability;
		this.randomEnchants = randomEnchants;
		
		this.rand = new Random();
	}
	
	public ItemStack generateItemStack(){
		int amount = generateAmount();
		if(amount == 0)
			return null;
		
		ItemStack item = generateItem(amount);
		return item;
	}
	
	private int generateAmount(){
		int rawAmount = minAmount;
		if(maxAmount != -1)
			rawAmount = minAmount + rand.nextInt(maxAmount - minAmount + 1);
		
		int realAmount = 0;
		for(int i = 0; i < rawAmount; i++)
			if(rand.nextDouble() * 100 <= probability)
				realAmount++;
		
		return realAmount;
	}
	
	private ItemStack generateItem(int amount){
		ItemStack item = null;
		if(damageValue == -1)
			item = new ItemStack(itemMaterial, amount);
		else	
			item = new ItemStack(itemMaterial, amount, (short) damageValue);
		
		if(randomEnchants != 0)
			enchantItem(item);
		
		return item;	
	}
	
	private void enchantItem(ItemStack item){
		Enchantment[] enchants = Enchantment.values();
		for(int i = 0; i < randomEnchants; i++){
			int randEnchant = rand.nextInt(enchants.length);
			Enchantment enchant = enchants[randEnchant];
			
			int maxLevel = enchant.getMaxLevel();
			int minLevel = enchant.getStartLevel();
			int range = maxLevel - minLevel;
			int realLevel = minLevel + rand.nextInt(range + 1);
			
			if(!enchant.canEnchantItem(item))
				continue;
			
			item.addEnchantment(enchant, realLevel);
		}
	}
}
