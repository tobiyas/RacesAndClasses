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

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.racbuilder.AbstractHolderBuilder;
import de.tobiyas.racesandclasses.racbuilder.gui.holders.HolderGeneralBuilderInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.holders.HolderSelectionState;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;

public abstract class HolderSelectionInterface extends
		BasicSelectionInterface {
	
	private final int currentStateField = 32;
	
	/**
	 * The Add Button
	 */
	protected final ItemStack addHolderSelector;
	
	/**
	 * The Edit Button
	 */
	protected final ItemStack editHolderSelector;
	
	/**
	 * The Remove Button
	 */
	protected final ItemStack removeHolderSelector;
	
	/**
	 * The Item indicating the current State
	 */
	protected final ItemStack currentStateIndicator;
	
	
	/**
	 * The Builder to check if something new has arrived.
	 */
	protected AbstractHolderBuilder builder;
	
	
	/**
	 * The HolderManager to get all Holders from
	 */
	protected final AbstractHolderManager holderManager;
	
	
	/**
	 * An Link between Holder -> Item
	 */
	protected final List<HolderItemContainer> holderContainers;
	
	
	/**
	 * The current state of the GUI.
	 */
	protected HolderSelectionState currentState = HolderSelectionState.EDIT;
	
	
	
	/**
	 * Creates a Selection Inventory to Select A Holder.
	 * 
	 * @param player
	 * @param parent
	 * @param holderManager
	 */
	public HolderSelectionInterface(Player player,
			BasicSelectionInterface parent, AbstractHolderManager holderManager, RacesAndClasses plugin) {
		super(player, parent, "Controls", holderManager.getContainerTypeAsString(), plugin);
		
		
		this.holderManager = holderManager;
		this.holderContainers = new LinkedList<HolderItemContainer>();
		
		
		String holderTypeName = holderManager.getContainerTypeAsString();
		
		this.addHolderSelector = generateItem(Material.DIAMOND, ChatColor.GREEN + "Add " + holderTypeName, 
				ChatColor.LIGHT_PURPLE + "Adds a new " + holderTypeName);
		
		this.editHolderSelector = generateItem(Material.SPONGE, ChatColor.YELLOW + "Edit " + holderTypeName, 
				ChatColor.LIGHT_PURPLE + "Edits an existing " + holderTypeName);

		this.removeHolderSelector = generateItem(Material.FIRE, ChatColor.RED + "Remove " + holderTypeName, 
				ChatColor.LIGHT_PURPLE + "Removes an existing " + holderTypeName);
		
		this.currentStateIndicator = generateItem(Material.SPONGE, ChatColor.AQUA + "Current Operation", new LinkedList<String>());
		
		
		this.controlInventory.setItem(2, this.addHolderSelector);
		this.controlInventory.setItem(5, this.editHolderSelector);
		this.controlInventory.setItem(6, this.removeHolderSelector);
		
		this.controlInventory.setItem(currentStateField, this.currentStateIndicator);
		
		createHolders();
		redrawHolderList();
	}
	
	
	/**
	 * Creates an Item and adds it to the Selection Inventory. 
	 */
	private void createHolders(){
		for(String holderName : this.holderManager.listAllVisibleHolders()){
			AbstractTraitHolder holder = holderManager.getHolderByName(holderName);
			AbstractHolderBuilder builder = generateHolderBuilderFor(holder);
			if(holder != null){
				HolderItemContainer container = new HolderItemContainer(builder);
				container.getBuilder().setReadyForBuilding(true);
				this.holderContainers.add(container);
			}
		}
	}
	
	
	/**
	 * Redraws the Holder from the list internally of holders.
	 */
	private void redrawHolderList() {
		selectionInventory.clear();
		
		List<HolderItemContainer> toRemove = new LinkedList<HolderItemContainer>();
		
		for(HolderItemContainer container: holderContainers){
			if(!container.getBuilder().isReadyForBuilding()){
				toRemove.add(container);
				continue;
			}
			
			if(container != null){
				container.rebuildItem();
				this.selectionInventory.addItem(container.getItemRepresentation());
			}
		}
		
		for(HolderItemContainer remove : toRemove){
			holderContainers.remove(remove);
		}
	}
	

	@Override
	protected boolean onBackPressed() {
		return true;
	}

	@Override
	protected void onAcceptPressed() {
		saveBuilders();
	}
	
	/**
	 * 
	 */
	private void saveBuilders(){
		for( HolderItemContainer holder : holderContainers){
			AbstractHolderBuilder builder = holder.getBuilder();
			builder.saveToFile();
		}
	}

	
	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		for(HolderItemContainer holder: holderContainers){
			if(holder.getItemRepresentation().equals(item) && holder.getBuilder().isReadyForBuilding()){
				holderPressed(holder);
			}
		}
	}

	
	@Override
	protected void onControlItemPressed(ItemStack item) {
		if(item.equals(this.currentStateIndicator)){
			//indicator does not do anything
			return;
		}
		
		if(item.equals(addHolderSelector)){
			scheduleAdd();
			return;
		}

		if(item.equals(editHolderSelector)){
			changeState(HolderSelectionState.EDIT);
			return;
		}
		
		if(item.equals(removeHolderSelector)){
			changeState(HolderSelectionState.REMOVE);
			return;
		}
	}

	
	/**
	 * Changes the currentState to the one wanted
	 * 
	 * @param newState
	 */
	private void changeState(HolderSelectionState newState) {
		if(newState == currentState) return;
		currentState = newState;
		
		switch (newState) {
			case REMOVE: currentStateIndicator.setType(removeHolderSelector.getType()); break;
			case EDIT: currentStateIndicator.setType(editHolderSelector.getType()); break;
		}
		
		controlInventory.setItem(currentStateField, null);
		controlInventory.setItem(currentStateField, currentStateIndicator);
	}


	/**
	 * This is called when an Holder is pressed.
	 * 
	 * @param holder that is pressed.
	 */
	private void holderPressed(HolderItemContainer container){
		switch(currentState){
			case EDIT: editHolder(container); break;
			case REMOVE: removeHolder(container); break;
		}
	}
	
	/**
	 * Opens an Edit Frame for the current Holder
	 * 
	 * @param holder
	 */
	private void editHolder(HolderItemContainer holder){
		builder = holder.getBuilder();
		openNewView(new HolderGeneralBuilderInterface(player, this, builder, (RacesAndClasses) plugin));
	}
	
	/**
	 * Removes the Race
	 * 
	 * @param holder
	 */
	private void removeHolder(HolderItemContainer holder){
		holderContainers.remove(holder);
		redrawHolderList();
	}


	/**
	 * Indicates that the Add Button is clicked
	 */
	private void scheduleAdd() {
		builder = generateNewHolderBuilder("<No Name>");
		HolderItemContainer container = new HolderItemContainer(builder);
		holderContainers.add(container);
		
		openNewView(new HolderGeneralBuilderInterface(player, this, builder, (RacesAndClasses) plugin));
	}

	
	/**
	 * Generates a new HolderBuilder
	 * 
	 * @param name
	 * @return
	 */
	protected abstract AbstractHolderBuilder generateNewHolderBuilder(String name);

	
	/**
	 * Generates a {@link AbstractHolderBuilder} for the Holder passed.
	 * 
	 * @param holder
	 * @return
	 */
	protected abstract AbstractHolderBuilder generateHolderBuilderFor(AbstractTraitHolder holder);
	
	
	
	@Override
	protected void notifyReopened() {
		if(builder != null && builder.isReadyForBuilding()){
			redrawHolderList();
			builder = null;
		}
	}
	
}
