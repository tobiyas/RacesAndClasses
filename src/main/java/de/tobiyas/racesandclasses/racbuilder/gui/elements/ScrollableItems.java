package de.tobiyas.racesandclasses.racbuilder.gui.elements;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

public class ScrollableItems {

	/**
	 * This is the button to 
	 */
	private ItemStack upButton;
	
	/**
	 * The Item representing the down button
	 */
	private ItemStack downButton;
	
	
	/**
	 * The Inventory to spawn
	 */
	private final Inventory inventory;
	
	/**
	 * The position from where to create.
	 * Min: 0
	 */
	private final int fromColumn;
	
	/**
	 * The position to where to create
	 * maximum: 8
	 */
	private final int toColumn;
	
	
	/**
	 * This is the list to scroll through
	 */
	private final List<ItemStack> itemsToScroll;
	
	/**
	 * The List of all Visible Items
	 */
	private final List<ItemStack> visibleItems;

	/**
	 * This indicates the current Row where we are at the moment
	 */
	private int currentRow = 0;
	
	
	/**
	 * The min row to write to.
	 */
	private final int fromRow;
	
	
	
	public ScrollableItems(Inventory inventory, int fromColumn, int toColumn, int fromRow) {
		this.inventory = inventory;
		this.fromColumn = fromColumn;
		this.toColumn = toColumn;
		this.fromRow = fromRow;
		
		visibleItems = new LinkedList<ItemStack>();
		
		itemsToScroll = new LinkedList<ItemStack>();

		upButton = new Wool(DyeColor.WHITE).toItemStack();
		ItemMeta meta = upButton.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Scroll UP");
		upButton.setItemMeta(meta);

		downButton = new Wool(DyeColor.WHITE).toItemStack();
		meta = downButton.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Scroll DOWN");
		downButton.setItemMeta(meta);
		
		
		redrawCurrent();
	}
	
	
	/**
	 * Redraws the current configuration of the view
	 */
	private void redrawCurrent(){
		int height = inventory.getSize() / 9;
		int width = (toColumn - fromColumn + 1) - 1;
		if(width <= 1) return;
		
		int totalRows = (int) Math.ceil((double)itemsToScroll.size() / (double)width);
		
		boolean showUpButton = currentRow != 0;
		boolean showDownButton = totalRows > (currentRow + height);
		
		int startIndex = (currentRow * 9) + fromColumn - 1;
		if(startIndex < 0) startIndex = 0;

		visibleItems.clear();
		
		int index = width * currentRow;
		if(!itemsToScroll.isEmpty()){
			for(int i_height = fromRow; i_height < height; i_height++){
				for(int i_width = fromColumn; i_width < width; i_width++){
					inventory.setItem((i_height * 9) + i_width,  null);
					if(index >= itemsToScroll.size()) continue;
					
					ItemStack item = itemsToScroll.get(index);
					inventory.setItem((i_height * 9) + i_width,  item);
					visibleItems.add(item);
					
					index++;
				}
				
			}
		}
		
		inventory.setItem((9 * fromRow) + toColumn, null);
		inventory.setItem((9 * (fromRow + 2)) + toColumn, null);
		if(showUpButton){
			upButton.setAmount(currentRow);
			inventory.setItem((9 * fromRow) + toColumn, upButton);
		}

		if(showDownButton){
			int amount = totalRows - currentRow - height;
			if(amount <= 0) amount = 1;
			
			downButton.setAmount(amount);
			inventory.setItem((9 * (fromRow + 2)) + toColumn, downButton);
		}
	}
	
	
	
	/**
	 * Adds an item to the Scrollable Items
	 * 
	 * @param item
	 */
	public void addItem(ItemStack item){
		this.itemsToScroll.add(item);
		redrawCurrent();
	}
	
	
	/**
	 * Adds all the items.
	 * 
	 * @param items
	 */
	public void addItems(List<ItemStack> items){
		this.itemsToScroll.addAll(items);
		redrawCurrent();
	}
	
	
	/**
	 * Removed an Item to from the Scrollable Items
	 * 
	 * @param item
	 */
	public void removeItem(ItemStack item){
		this.itemsToScroll.remove(item);
		redrawCurrent();
	}
	
	/**
	 * Returns all Items
	 * 
	 * @return
	 */
	public List<ItemStack> getAllItems(){
		return itemsToScroll;
	}
	
	
	/**
	 * Checks if a Scroll button has been pressed.
	 * 
	 * @param item
	 * @return
	 */
	public boolean checkScrollButtons(ItemStack item){
		if(upButton.equals(item)){
			scrollUp();
			return true;
		}

		if(downButton.equals(item)){
			scrollDown();
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Scrolls the view 1 row down
	 */
	public void scrollDown(){
		int width = (toColumn - fromColumn + 1) - 1;		
		int totalRows = (int) Math.ceil((double)itemsToScroll.size() / (double)width);
		int height = inventory.getSize() / 9;
		currentRow ++;

		if((currentRow + height) > totalRows) currentRow --;

		redrawCurrent();
	}
	
	
	/**
	 * Scrolls the view 1 up
	 */
	public void scrollUp(){
		currentRow --;
		if(currentRow < 0) currentRow = 0;
		
		redrawCurrent();
	}


	public void clear() {
		currentRow = 0;
		itemsToScroll.clear();
		visibleItems.clear();
		
		redrawCurrent();
	}

}
