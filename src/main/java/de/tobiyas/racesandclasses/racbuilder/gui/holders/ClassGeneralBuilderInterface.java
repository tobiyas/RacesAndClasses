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
package de.tobiyas.racesandclasses.racbuilder.gui.holders;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.racbuilder.AbstractHolderBuilder;
import de.tobiyas.racesandclasses.racbuilder.ClassBuilder;
import de.tobiyas.racesandclasses.util.items.ItemMetaUtils;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;
import de.tobiyas.util.inventorymenu.stats.OperatorSelectionInterface;

public class ClassGeneralBuilderInterface extends HolderGeneralBuilderInterface {

	/**
	 * The Config Map path to the health_operator
	 */
	private static final String HEALTH_OPERATOR_PATH = "health_operator";
	
	/**
	 * The healthOperator as Selector Item
	 */
	private final ItemStack healthOperationSelector;
	
	public ClassGeneralBuilderInterface(Player player,
			BasicSelectionInterface parent, AbstractHolderBuilder builder, RacesAndClasses plugin) {
		super(player, parent, builder, plugin);

		this.healthOperationSelector = generateItem(Material.BAKED_POTATO, ChatColor.RED + "Health Operator", 
				ChatColor.LIGHT_PURPLE + "Value : ");
	}
	
	
	@Override
	protected void rebuildFromBuilder() {
		super.rebuildFromBuilder();
		
		ClassBuilder classBuilder = (ClassBuilder) builder;
		String operation = classBuilder.getHealthOperation();
		ItemMetaUtils.replaceLoreWith(healthOperationSelector, "Value: " + operation);
	}


	@Override
	protected void redraw() {
		super.redraw();
	}


	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		super.onSelectionItemPressed(item);
		if(healthOperationSelector.equals(item)){
			editHealthOperation();
			return;
		}
	}


	/**
	 * Schedules opening of a String Selection
	 * to get a new Name.
	 */
	private void editHealthOperation(){
		this.currentModifiedValue = HEALTH_OPERATOR_PATH;
		
		ClassBuilder classBuilder = (ClassBuilder) builder;
		this.callbackMap.put(currentModifiedValue, classBuilder.getHealthOperation());
		
		openNewView(new OperatorSelectionInterface(player, this, callbackMap, currentModifiedValue, plugin));
	}

}
