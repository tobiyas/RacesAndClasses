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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemMetaUtils;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigParser;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;
import de.tobiyas.util.inventorymenu.stats.StatType;
import de.tobiyas.util.inventorymenu.stats.StatsSelectionInterfaceFactory;

public class TraitBuilderInterface extends BasicSelectionInterface {

	/**
	 * The ConfigTotal map to fill
	 */
	private final List<TraitConfigOptionContainer> configList;
	
	/**
	 * A temporary config Map to transport stuff back
	 */
	private final Map<String, Object> tempConfigList = new HashMap<String, Object>();
	
	/**
	 * The Field to check on back
	 */
	private TraitConfigOptionContainer optionToCheck;
	
	
	public TraitBuilderInterface(Player player, BasicSelectionInterface parent, String traitName,
			List<TraitConfigOptionContainer> configList, RacesAndClasses plugin) {
		super(player, parent, "Controls", "Trait: " + traitName, plugin);
		
		this.configList = configList;
		
		try{
			Class<? extends Trait> traitClass = TraitsList.getClassOfTrait(traitName);
			
			List<TraitConfigurationField> config = TraitConfigParser.getAllTraitConfigFieldsOfTrait(traitClass);
			for(TraitConfigurationField field : config){
				
				boolean skip = false;
				for(TraitConfigOptionContainer container : this.configList){
					if(container.getName().equalsIgnoreCase(field.fieldName())) {
						skip = true;
					}
				}
				
				if(skip) continue;
				
				ItemStack item = generateItem(Material.ANVIL, ChatColor.RED + field.fieldName(), new LinkedList<String>());
				if(field.optional()){
					ItemMetaUtils.addStringToLore(item, ChatColor.YELLOW + "is optional.");
				}
				
				TraitConfigOptionContainer newContainer = new TraitConfigOptionContainer(field.fieldName(), 
						StatType.getTypeFromClass(field.classToExpect()), item);
				this.configList.add(newContainer);
			}			
		}catch(Exception exp){}
		
		redraw();
	}
	
	
	/**
	 * Redraws the Interface
	 */
	private void redraw(){
		selectionInventory.clear();
		
		for(TraitConfigOptionContainer option : configList){
			selectionInventory.addItem(option.getItem());
		}
	}
	

	@Override
	protected boolean onBackPressed() {
		return true;
	}

	@Override
	protected void onAcceptPressed() {
		//Nothing needed. All is saved to the List passed.
		closeAndReturnToParent();
	}

	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		for(TraitConfigOptionContainer traitItem : configList){
			if(traitItem.getItem().equals(item)){
				String key = traitItem.getName();
				tempConfigList.put(key, traitItem.getValue());
				optionToCheck = traitItem;
				
				openNewView(
						StatsSelectionInterfaceFactory.buildInterface(
								traitItem.getType(), player, this, tempConfigList, key, plugin)
						);
				
				return;
			}
		}
	}

	@Override
	protected void onControlItemPressed(ItemStack item) {
	}


	@Override
	protected void notifyReopened() {
		super.notifyReopened();
		
		if(optionToCheck == null) return;
		
		
		Object newValue = tempConfigList.get(this.optionToCheck.getName());
		if(newValue != null){
			optionToCheck.setValue(newValue);
			redraw();
		}
	}

	
	
}
