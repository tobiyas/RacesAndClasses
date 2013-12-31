package de.tobiyas.racesandclasses.racbuilder.gui.holders;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.racbuilder.AbstractHolderBuilder;
import de.tobiyas.racesandclasses.racbuilder.gui.trait.TraitBuilderInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.trait.TraitConfigOptionContainer;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemMetaUtils;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;
import de.tobiyas.util.inventorymenu.elements.ScrollableItems;
import de.tobiyas.util.inventorymenu.stats.StatType;

public class HolderBuildInterface extends BasicSelectionInterface {

	/**
	 * The Traits the holder already has.
	 */
	protected ScrollableItems holderTraits;
	
	/**
	 * The Traits the holder can still use.
	 */
	protected ScrollableItems possibleTraits;
	
	/**
	 * The builder to build this Holder
	 */
	protected final AbstractHolderBuilder builder;
	
	
	/**
	 * The List to check after returning
	 */
	protected List<TraitConfigOptionContainer> currentTraitEditingList;
	
	/**
	 * The Name currently editing
	 */
	protected String currentTraitName;
	

	
	public HolderBuildInterface(Player player, BasicSelectionInterface parent, AbstractHolderBuilder builder, RacesAndClasses plugin) {
		super(player, parent, plugin);
	
		this.builder = builder;
		holderTraits = new ScrollableItems(selectionInventory, 0, 8, 0);
		possibleTraits = new ScrollableItems(controlInventory, 0, 8, 1);
		
		populateFromBuilder();
	}
	
	/**
	 * populates the Lists from the Builder
	 */
	@SuppressWarnings("unchecked")
	private void populateFromBuilder(){
		possibleTraits.clear();
		holderTraits.clear();
		
		List<ItemStack> itemsToAdd = new LinkedList<ItemStack>();
		for(Trait trait : builder.getTraits()){
			ItemStack traitItem = new ItemStack(Material.SKULL_ITEM);
			ItemMetaUtils.setDisplayNameOfItem(traitItem, ChatColor.RED + trait.getName());
			
			List<String> helpList = new LinkedList<String>();
			try{
				Class<? extends Trait> clazz = TraitsList.getClassOfTrait(trait.getName());
				if(clazz == null){
					break;
				}
				
				helpList = (List<String>) clazz.getMethod("getHelpForTrait").invoke(clazz);
			}catch(Exception exp){
				helpList.add("No help found.");
			}
			
			for(String line : helpList){
				ItemMetaUtils.addStringToLore(traitItem, line);
			}
			
			ItemMetaUtils.addStringToLore(traitItem, "Config:");
			ItemMetaUtils.addStringToLore(traitItem, trait.getPrettyConfiguration());
			itemsToAdd.add(traitItem);
		}
		holderTraits.addItems(itemsToAdd);
		
		
		itemsToAdd = new LinkedList<ItemStack>();
		List<String> alreadyPresentInList = new LinkedList<String>();
		for(String traitName : TraitsList.getAllVisibleTraits()){
			boolean skip = false;
			
			for(ItemStack item : holderTraits.getAllItems()){
				if(item.getItemMeta().hasDisplayName() && 
						item.getItemMeta().getDisplayName().contains(traitName)){
					
					skip = true;
					break;
				}
			}
				
			if(skip) continue;
			if(alreadyPresentInList.contains(traitName)){
				continue;
			}
			
			List<String> helpList = new LinkedList<String>();
			try{
				Class<? extends Trait> clazz = TraitsList.getClassOfTrait(traitName);
				if(clazz == null){
					break;
				}
				
				helpList = (List<String>) clazz.getMethod("getHelpForTrait").invoke(clazz);
			}catch(Exception exp){
				helpList.add("No help found.");
			}
			
			
			ItemStack traitItem = new ItemStack(Material.SKULL_ITEM);
			ItemMetaUtils.setDisplayNameOfItem(traitItem, ChatColor.RED + traitName);
			
			for(String helpString : helpList){
				ItemMetaUtils.addStringToLore(traitItem, helpString);
			}
			
			itemsToAdd.add(traitItem);
			alreadyPresentInList.add(traitName);
			
		}
		possibleTraits.addItems(itemsToAdd);
	}
	
	
	@Override
	protected boolean onBackPressed() {
		return true;
	}

	@Override
	protected void onAcceptPressed() {
		performSave();
		closeAndReturnToParent();
	}
	
	
	/**
	 * Saves the builder
	 */
	private void performSave(){
		builder.setReadyForBuilding(true);
	}

	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		if(holderTraits.checkScrollButtons(item)) return;
		
		ItemMeta meta = item.getItemMeta();
		if(meta.hasDisplayName()){
			String traitName = meta.getDisplayName();
			traitName = traitName.substring(2, traitName.length());
			
			for(Trait trait : builder.getTraits()){
				if(trait.getName().equalsIgnoreCase(traitName)){
					currentTraitEditingList = loadFromTrait(trait);
					currentTraitName = trait.getName();
					
					openNewView(new TraitBuilderInterface(player, this, trait.getName(), currentTraitEditingList, (RacesAndClasses) plugin));
					return;
				}
			}
		}
	}
	
	
	/**
	 * Builds the List of TraitOptions from the passed Trait
	 * 
	 * @param trait
	 * @return
	 */
	private List<TraitConfigOptionContainer> loadFromTrait(Trait trait){
		List<TraitConfigOptionContainer> configOptions = new LinkedList<TraitConfigOptionContainer>();
		
		if(!(trait instanceof AbstractBasicTrait) ) return configOptions;
		
		Map<String, Object> traitconfigMap = trait.getCurrentconfig();
		if(traitconfigMap == null) return configOptions;
		
		for(String key : traitconfigMap.keySet()){
			Object value = traitconfigMap.get(key);
			
			ItemStack item = generateItem(Material.ANVIL, key, value.toString());
			
			TraitConfigOptionContainer newOptions = new TraitConfigOptionContainer(key, StatType.getTypeFromClass(value.getClass()), item);
			newOptions.setValue(value);
			
			configOptions.add(newOptions);
		}
		
		return configOptions;
	}
	

	@Override
	protected void onControlItemPressed(ItemStack item) {
		if(possibleTraits.checkScrollButtons(item)) return;
		
		ItemMeta meta = item.getItemMeta();
		if(meta.hasDisplayName()){
			String traitName = meta.getDisplayName();
			traitName = traitName.substring(2, traitName.length());
			
			currentTraitName = traitName;
			currentTraitEditingList = new LinkedList<TraitConfigOptionContainer>();
			openNewView(new TraitBuilderInterface(player, this, traitName, currentTraitEditingList, (RacesAndClasses) plugin));
		}
	}

	@Override
	protected void notifyReopened() {
		super.notifyReopened();
		
		Map<String, Object> configMap = new HashMap<String, Object>();
		for(TraitConfigOptionContainer container : currentTraitEditingList){
			if(!container.isValueSet()){
				return;
			}
			
			configMap.put(container.getName(), container.getValue());
		}
		
		builder.addTrait(currentTraitName, configMap);
		populateFromBuilder();
	}

	
	
}
