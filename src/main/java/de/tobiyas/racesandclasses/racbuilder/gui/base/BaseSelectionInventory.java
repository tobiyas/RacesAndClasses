package de.tobiyas.racesandclasses.racbuilder.gui.base;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.holdermanager.ClassSelectionInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.holdermanager.RaceSelectionInterface;

public class BaseSelectionInventory extends BasicSelectionInterface {

	/**
	 * The Item representing the Races
	 */
	private final ItemStack raceSelectionStack;
	
	/**
	 * The Item representing the Classes
	 */
	private final ItemStack classSelectionStack;
	
	
	public BaseSelectionInventory(Player player) {
		super(player, null, "Controls", "Select what to edit");
		
		raceSelectionStack = generateItem(Material.SKULL_ITEM, ChatColor.RED + "Races",
				ChatColor.LIGHT_PURPLE + "Edit your Races here");

		classSelectionStack = generateItem(Material.SKULL_ITEM, ChatColor.RED + "Classes",
				ChatColor.LIGHT_PURPLE + "Edit your Classes here");
		
		selectionInventory.setItem(3, raceSelectionStack);
		selectionInventory.setItem(4, classSelectionStack);
	}

	@Override
	protected boolean onBackPressed() {
		return true;
	}

	
	@Override
	protected void onAcceptPressed() {
		performSave();
	}

	
	
	/**
	 * Saves the made changes to the files.
	 */
	protected void performSave(){
		//TODO implement me
	}

	
	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		if(item.equals(this.classSelectionStack)){
			openNewView(new ClassSelectionInterface(player, this, plugin.getClassManager()));
			return;
		}

		if(item.equals(this.raceSelectionStack)){
			openNewView(new RaceSelectionInterface(player, this, plugin.getRaceManager()));
			return;
		}
	}

	
	@Override
	protected void onControlItemPressed(ItemStack item) {
	}
}