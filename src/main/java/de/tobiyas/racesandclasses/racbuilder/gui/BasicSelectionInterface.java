package de.tobiyas.racesandclasses.racbuilder.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import de.tobiyas.racesandclasses.RacesAndClasses;

public abstract class BasicSelectionInterface extends ItemGeneratorInterface implements Listener{	
	
	/**
	 * Indicates that a new InventoryOpening is going on.
	 */
	protected boolean isOpeningNewInv = false;
	
	/**
	 * Plugin to call Scheduling from
	 */
	protected final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * An Item placed at ItemSlot ((3 * 9) / 2) to register an Back Click.
	 */
	protected final ItemStack BACK_OBJECT;
	
	/**
	 * An Item placed at ItemSlot (((3 * 9) / 2) -1 ) to register an Accepted Click.
	 */
	protected final ItemStack ACCEPT_OBJECT;
	
	
	/**
	 * The Parent interface for the Selection.
	 * Null if ROOT.
	 */
	protected BasicSelectionInterface parent;
	
	/**
	 * The Player constructed for
	 */
	protected final Player player;
	
	/**
	 * The Inventory to show
	 */
	protected final Inventory selectionInventory;
	
	/**
	 * The Inventory to show the Controls
	 */
	protected final Inventory controlInventory;
	
	
	/**
	 * Creates a Selection Interface.
	 * The control interface is fixed at the bottom.
	 * 
	 * parent is the Parent object to open.
	 * If No Parent is found (parent == null), the Interface is closed.
	 *  
	 * @param player
	 * @param parent
	 * @param controlInventoryName
	 * @param selectionInventoryName
	 */
	public BasicSelectionInterface(Player player, BasicSelectionInterface parent, 
			String controlInventoryName, String selectionInventoryName) {
		this.player = player;
		this.parent = parent;
		
		if(selectionInventoryName.length() > 32) selectionInventoryName = selectionInventoryName.substring(0, 32);
		if(controlInventoryName.length() > 32) controlInventoryName = controlInventoryName.substring(0, 32);
		
		selectionInventory = Bukkit.createInventory(player, 3 * 9, selectionInventoryName);
		controlInventory = Bukkit.createInventory(player, 4 * 9, controlInventoryName);
		
		BACK_OBJECT = generateBackItem();
		ACCEPT_OBJECT = generateAcceptItem();
		
		
		controlInventory.setItem(0, new ItemStack(Material.WOOD));
		controlInventory.setItem(0, ACCEPT_OBJECT);
		controlInventory.setItem(8, BACK_OBJECT);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	/**
	 * Creates a Selection Interface.
	 * The control interface is fixed at the bottom.
	 * 
	 * parent is the Parent object to open.
	 * If No Parent is found (parent == null), the Interface is closed.
	 *  
	 * @param player
	 * @param parent
	 */
	public BasicSelectionInterface(Player player, BasicSelectionInterface parent) {
		this(player, parent, "Control", "Data");
	}
	

	@Override
	public Inventory getTopInventory() {
		return selectionInventory;
	}

	@Override
	public Inventory getBottomInventory() {
		return controlInventory;
	}

	@Override
	public HumanEntity getPlayer() {
		return player;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}
	
	
	
	@EventHandler
	public void onInterfaceInteract(InventoryClickEvent event){
		if(event.getView() != this) return;
		
		ItemStack itemClicked = event.getCurrentItem();
		if(itemClicked == null || itemClicked.getType() == Material.AIR) return;

		
		event.setCancelled(true);
		if(event.getClick() != ClickType.RIGHT) return;
		
		if(this.ACCEPT_OBJECT.equals(itemClicked)){
			onAcceptPressed();
			return;
		}
		
		if(this.BACK_OBJECT.equals(itemClicked)){
			if(onBackPressed()) scheduleCloseOfInventory();
			return;
		}
		
		for(ItemStack item : controlInventory.getContents()){
			if(item != null && item.getType() != Material.AIR 
					&& item.equals(itemClicked)){
				
				onControlItemPressed(item);	
				return;
			}
		}

		for(ItemStack item : selectionInventory.getContents()){
			if( item != null && item.getType() != Material.AIR 
					&& item.equals(itemClicked)){
				
				onSelectionItemPressed(item);					
				return;
			}
		}
	}
	
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(event.getView() != this) return;
		if(isOpeningNewInv){
			//We get a close from the new Window opening.
			isOpeningNewInv = false;
			return;
		}
		
		openParent();
		
		HandlerList.unregisterAll(this);
	}


	/**
	 * This is called when the BackItem is pressed.
	 * 
	 * To prevent the View from Returning to last Inventory, 
	 * return false.
	 * 
	 * Returning True indicates returning to parent is okay.
	 */
	protected abstract boolean onBackPressed();
	
	
	/**
	 * This is called when the Accept Item is pressed.
	 */
	protected abstract void onAcceptPressed();
	
	
	/**
	 * This is called when the Player presses an Selection Item (upper inventory)
	 * @param item that was clicked.
	 */
	protected abstract void onSelectionItemPressed(ItemStack item); 
	
	/**
	 * This is called when the Player presses an Control Item (lower inventory)
	 * 
	 * HINT: The Accept / Abort buttons are NOT passed here. They are handled in:
	 * {@link #onBackPressed()} and {@link #onAcceptPressed()}.
	 * 
	 * @param item that was clicked.
	 */
	protected abstract void onControlItemPressed(ItemStack item); 
	
	
	/**
	 * Opens the Parent view.
	 */
	private void scheduleCloseOfInventory(){
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				player.closeInventory();
			}
		}, 2);
	}
	
	
	/**
	 * Opens the Parent view.
	 */
	private void scheduleOpeningOfParent() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if(parent != null){				
					player.openInventory(parent);
					parent.notifyReopened();
				}else{
					player.updateInventory();
				}
			}
		}, 2);
	}
	
	
	/**
	 * This method is called when the Parent is reopened.
	 */
	protected void notifyReopened(){}
	
	
	
	/**
	 * Returns to Parent view
	 */
	private void openParent(){
		scheduleOpeningOfParent();
	}
	
	
	/**
	 * Closes the current view.
	 * On close is called afterwards to return to parent.
	 */
	protected void closeAndReturnToParent(){
		scheduleCloseOfInventory();
	}
	
	
	/**
	 * Safely opens a new View.
	 * This is needed to prevent backPressing bugs.
	 * 
	 * @param newView
	 */
	protected void openNewView(final BasicSelectionInterface newView){
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				BasicSelectionInterface.this.isOpeningNewInv = true;
				player.openInventory(newView);
			}
		}, 1);
	}
	
	
	/**
	 * Generates an Back Item.
	 * 
	 * @return
	 */
	private ItemStack generateBackItem(){
		ItemStack item = new Wool(DyeColor.RED).toItemStack();
		
		ItemMeta meta = item.getItemMeta();
		if(parent == null){
			meta.setDisplayName(ChatColor.RED + "Exit");			
		}else{
			meta.setDisplayName(ChatColor.RED + "ABORT");			
		}
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	
	/**
	 * Generates an Accept Item.
	 * 
	 * @return
	 */
	private ItemStack generateAcceptItem(){
		ItemStack item = new Wool(DyeColor.GREEN).toItemStack();
		
		ItemMeta meta = item.getItemMeta();
		
		if(parent == null){
			meta.setDisplayName(ChatColor.RED + "Save");			
		}else{
			meta.setDisplayName(ChatColor.GREEN + "Accept");
		}
		
		item.setItemMeta(meta);
		
		return item;
	}
}
