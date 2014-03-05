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
package de.tobiyas.racesandclasses.racbuilder.gui.holdermanager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.racbuilder.AbstractHolderBuilder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemMetaUtils;

public class HolderItemContainer {

	private final ItemStack itemRepresentation;
	
	private final AbstractHolderBuilder builder;
		
	
	public HolderItemContainer(AbstractHolderBuilder builder) {
		this.builder = builder;
		
		ItemStack item = new ItemStack(Material.PAPER);
		this.itemRepresentation = item;
	}

	/**
	 * Rebuilds the ItemRepresentation for the Item from the Builder.
	 */
	public void rebuildItem() {
		ItemMetaUtils.setDisplayNameOfItem(itemRepresentation, ChatColor.RED + builder.getName());
		
		for(Trait trait : builder.getTraits()){
			ItemMetaUtils.addStringToLore(itemRepresentation, trait.getName());
		}
		
	}

	public ItemStack getItemRepresentation() {
		return itemRepresentation;
	}



	public AbstractHolderBuilder getBuilder() {
		return builder;
	}
	
}
