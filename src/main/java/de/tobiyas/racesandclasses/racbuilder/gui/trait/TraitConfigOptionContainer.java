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
package de.tobiyas.racesandclasses.racbuilder.gui.trait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.util.inventorymenu.stats.StatType;

public class TraitConfigOptionContainer {

	/**
	 * The Name of the Config Option
	 */
	private final String name;
	
	/**
	 * The Type of the Config Option
	 */
	private final StatType type;
	
	/**
	 * The Value set for the Confic Option
	 */
	private Object value;
	
	/**
	 * The Item representing the ConfigOption
	 */
	private final ItemStack item;
	
	

	/**
	 * Creates a container for a Trait Config Option
	 * 
	 * @param name
	 */
	public TraitConfigOptionContainer(String name, StatType type, ItemStack item) {
		this.name = name;
		this.type = type;
		
		this.value = null;
		this.item = item;
	}


	public Object getValue() {
		return value;
	}


	/**
	 * Sets the Value + changes the Lore of the Item to the correct Value
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		if(value != null){
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new LinkedList<String>();
			lore.add(String.valueOf(value));
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		
		this.value = value;
	}


	public String getName() {
		return name;
	}


	public StatType getType() {
		return type;
	}

	/**
	 * Checks if the Value is set or not.
	 * 
	 * @return true if set, false if value == null
	 */
	public boolean isValueSet(){
		return value != null;
	}


	public ItemStack getItem() {
		return item;
	}
	
	
	/**
	 * Checks if the passed Item is the hold Item
	 * 
	 * @param item
	 * @return
	 */
	public boolean isClicked(ItemStack item){
		return this.item.equals(item);
	}
	
	
	@Override
	public String toString(){
		return "name: " + name + " type: " + type.name() + " value: " + value;
	}
}
