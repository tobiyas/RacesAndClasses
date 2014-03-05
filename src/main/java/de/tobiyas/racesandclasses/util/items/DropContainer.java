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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

public class DropContainer {

	private int expMin;
	private int expMax;
	private Random rand;
	
	private ArrayList<ItemContainer> items;
	
	public DropContainer(int expMin, int expMax){
		this.expMin = expMin;
		this.expMax = expMax;
		this.items = new ArrayList<ItemContainer>();
		this.rand = new Random();
	}
	
	public DropContainer(int exp){
		this.expMin = exp;
		this.expMax = -1;
		this.items = new ArrayList<ItemContainer>();
		this.rand = new Random();
	}
	
	public void addItem(int itemID, int damageValue, int minAmount, int maxAmount, double probability, int randomEnchants){
		ItemContainer container = new ItemContainer(itemID, damageValue, minAmount, maxAmount, probability, randomEnchants);
		items.add(container);
	}
	
	public void addItem(int itemID, int damageValue, int minAmount, int maxAmount, double probability){
		ItemContainer container = new ItemContainer(itemID, damageValue, minAmount, maxAmount, probability);
		items.add(container);
	}
	
	public void addItem(int itemID, int minAmount, int maxAmount, double probability){
		addItem(itemID, -1, minAmount, maxAmount, probability);
	}
	
	public void addItem(int itemID, int amount){
		addItem(itemID, amount, -1, 100);
	}
	
	public void addItem(int itemID, int minAmount, int maxAmount){
		addItem(itemID, -1, minAmount, maxAmount);
	}
	
	public void parseString(String itemString) throws Exception{
		String[] stringSplit = itemString.split(";");
		String tempItemID = stringSplit[0];
		int itemID = -1;
		int damageValue = -1;
		
		if(tempItemID.contains("-")){
			String[] tempSplit = tempItemID.split("-");
			itemID = Integer.valueOf(tempSplit[0]);
			damageValue = Integer.valueOf(tempSplit[1]);
		}else
			itemID = Integer.valueOf(tempItemID);
		
		int minAmount = -1;
		int maxAmount = -1;
		
		String amount = stringSplit[1];
		if(amount.contains("-")){
			String[] tempSplit = amount.split("-");
			minAmount = Integer.valueOf(tempSplit[0]);
			maxAmount = Integer.valueOf(tempSplit[1]);
		}else
			minAmount = Integer.valueOf(amount);

		double probability = 100;
		if(stringSplit.length >= 3)
			probability = Double.valueOf(stringSplit[2]);

		int randomEnchants = 0;
		if(stringSplit.length >= 4)
			randomEnchants = Integer.valueOf(stringSplit[3]);

		if(randomEnchants == 0)
			addItem(itemID, damageValue, minAmount, maxAmount, probability);
		else
			addItem(itemID, damageValue, minAmount, maxAmount, probability, randomEnchants);
	}
	
	
	public int getEXP() {
		if(expMax == -1)
			return expMin;
		
		int range = expMax - expMin;
		int exp = expMin + rand.nextInt(range + 1);
		return exp;
	}

	public List<ItemStack> getItems() {
		ArrayList<ItemStack> stack = new ArrayList<ItemStack>();
		for(ItemContainer container : items){
			ItemStack item = container.generateItemStack();
			if(item != null)
				stack.add(item);
		}
		
		return stack;
	}

}
