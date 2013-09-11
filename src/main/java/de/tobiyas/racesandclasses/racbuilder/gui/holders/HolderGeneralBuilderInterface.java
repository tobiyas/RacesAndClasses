package de.tobiyas.racesandclasses.racbuilder.gui.holders;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.racbuilder.AbstractHolderBuilder;
import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.stats.DoubleSelectionInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.stats.StringSelectionInterface;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemMetaUtils;

public class HolderGeneralBuilderInterface extends BasicSelectionInterface {

	private static final String emptyName = "<EMPTY>";
	
	/**
	 * The Path to the Name of the Holder
	 */
	private static final String NAME_PATH = "name";
	
	/**
	 * The Path to the Health bonus.
	 */
	private static final String HEALTH_PATH = "health";
	
	/**
	 * The Path to the Callback Tag of the Holder
	 */
	private static final String TAG_PATH = "tag";
	
	
	/**
	 * The name selection item
	 */
	private final ItemStack nameSelector;
	
	/**
	 * The Health selector
	 */
	private final ItemStack healthSelector;
	
	/**
	 * The Tag selector Item
	 */
	private final ItemStack tagSelector;
	
	/**
	 * The Trait selector Item
	 */
	private final ItemStack traitSelector;
	
	
	/**
	 * The Map to put callback values into.
	 */
	protected final Map<String, Object> callbackMap;
	
	/**
	 * The Value modified to set on callback
	 */
	protected String currentModifiedValue = null;
	
	/**
	 * The Builder to build this holder
	 */
	protected final AbstractHolderBuilder builder;
	
	
	
	public HolderGeneralBuilderInterface(Player player,
			BasicSelectionInterface parent, AbstractHolderBuilder builder) {
		super(player, parent, "Controls", "Build Your Holder");
		
		
		this.callbackMap = new HashMap<String, Object>();
		this.builder = builder;
		
		nameSelector = generateItem(Material.BAKED_POTATO, ChatColor.RED + "Holder Name: ", emptyName);
		tagSelector = generateItem(Material.BAKED_POTATO, ChatColor.RED + "Holder Tag: ", emptyName);
		healthSelector = generateItem(Material.BAKED_POTATO, ChatColor.RED + "Health: ", "");

		traitSelector = generateItem(Material.ANVIL, ChatColor.RED + "Edit Traits", "");
		
		redraw();
	}
	
	/**
	 * Rebuilds the Items from the 
	 */
	protected void rebuildFromBuilder(){
		String name = builder.getName();
		if(name == null) name = emptyName;
		
		ItemMetaUtils.replaceLoreWith(nameSelector, "Holder Name: " + name);
		
		String tag = builder.getHolderTag();
		if(tag == null) tag = emptyName;
		ItemMetaUtils.replaceLoreWith(tagSelector, "Holder Tag: " + tag);
		
		double health = builder.getHealth();
		ItemMetaUtils.replaceLoreWith(healthSelector, "Holder health: " + health);
		
		ItemMetaUtils.replaceLoreWith(traitSelector, "Traits:");
		for(Trait trait : builder.getTraits()){
			ItemMetaUtils.addStringToLore(traitSelector, trait.getName());
		}
	}
	
	
	/**
	 * Redraws the GUI.
	 * Also rebuilds the items from the Builder
	 */
	protected void redraw(){
		rebuildFromBuilder();
		
		selectionInventory.setItem(2, nameSelector);
		selectionInventory.setItem(3, tagSelector);
		selectionInventory.setItem(5, healthSelector);
		selectionInventory.setItem(7, traitSelector);
	}

	
	@Override
	protected boolean onBackPressed() {
		return true;
	}

	@Override
	protected void onAcceptPressed() {
		builder.setReadyForBuilding(true);
	}

	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		if(nameSelector.equals(item)){
			editName();
			return;
		}
	
		if(tagSelector.equals(item)){
			editTag();
			return;
		}
		if(healthSelector.equals(item)){
			editHealth();
			return;
		}
		if(traitSelector.equals(item)){
			editTraits();
			return;
		}
	}

	/**
	 * Schedules opening of a String Selection
	 * to get a new Name.
	 */
	private void editName(){
		this.currentModifiedValue = NAME_PATH;
		this.callbackMap.put(currentModifiedValue, builder.getName());
		
		openNewView(new StringSelectionInterface(player, this, callbackMap, currentModifiedValue));
	}

	/**
	 * Schedules opening of a String Selection
	 * to get a new Tag.
	 */
	private void editTag(){
		this.currentModifiedValue = TAG_PATH;
		this.callbackMap.put(currentModifiedValue, builder.getName());
		
		openNewView(new StringSelectionInterface(player, this, callbackMap, currentModifiedValue));
	}
	
	/**
	 * Schedules opening of a Double Selection
	 * to get a new Health
	 */
	private void editHealth(){
		this.currentModifiedValue = HEALTH_PATH;
		this.callbackMap.put(currentModifiedValue, builder.getHealth());
		
		openNewView(new DoubleSelectionInterface(player, this, callbackMap, currentModifiedValue));
	}
	
	/**
	 * Schedules opening of the Trait edit window
	 * to edit Traits.
	 */
	private void editTraits(){
		this.currentModifiedValue = null;
		openNewView(new HolderBuildInterface(player, this, builder));
	}
	
	
	@Override
	protected void onControlItemPressed(ItemStack item) {
	}

	
	
	@Override
	protected void notifyReopened() {
		super.notifyReopened();
		if(currentModifiedValue == null) return;
		
		if(currentModifiedValue == NAME_PATH){
			String newName = (String) callbackMap.get(NAME_PATH);
			builder.setName(newName);
			redraw();
			return;
		}

		if(currentModifiedValue == TAG_PATH){
			String newTag = (String) callbackMap.get(TAG_PATH);
			builder.setHolderTag(newTag);
			redraw();
			return;
		}
		
		if(currentModifiedValue == HEALTH_PATH){
			double newHealth = (Double) callbackMap.get(HEALTH_PATH);
			builder.setHealth(newHealth);
			redraw();
			return;
		}
		
	}

	
	
}