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
