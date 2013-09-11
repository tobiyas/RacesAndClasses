package de.tobiyas.racesandclasses.racbuilder.gui.stats;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;

public class StringSelectionInterface extends AbstractStatSelectionInterface {

	/**
	 * The Item Representing the StringValue
	 */
	protected final ItemStack stringValueSelector;
	
	/**
	 * The String value wanted
	 */
	protected String value;

	
	/**
	 * A constructor with all needed args + predefined value to enter
	 * 
	 * @param player
	 * @param parent
	 * @param selectionInventoryName
	 * @param config
	 * @param key
	 * @param predifined
	 */
	public StringSelectionInterface(Player player,
			BasicSelectionInterface parent, Map<String, Object> config, String key) {
		
		super(player, parent, "Edit String by clicking the Item", config, key);

		String predifined = "";
		try{
			predifined = (String) config.get(key);
		}catch(Exception exp){}
		
		value = predifined;
		
		stringValueSelector = generateItem(Material.BEDROCK, ChatColor.RED + "Click to Edit", 
				ChatColor.LIGHT_PURPLE + "Value : " + String.valueOf(value));
		
		redraw();
	}
	
	
	private void redraw(){
		ItemMeta meta = stringValueSelector.getItemMeta();
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.LIGHT_PURPLE + "Value: " + String.valueOf(value));
		meta.setLore(lore);
		stringValueSelector.setItemMeta(meta);
		
		selectionInventory.clear();
		selectionInventory.setItem(4, stringValueSelector);
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void readFirstWordsOfPlayer(PlayerCommandPreprocessEvent event){
		if(event.getPlayer() != player) return;
		
		String message = event.getMessage();
		message = message.substring(1, message.length()); //remove first char. This is always a /
		
		value = message;
		
		event.setCancelled(true);
		player.sendMessage(ChatColor.RED + "[RAC] Saved your String: " + ChatColor.LIGHT_PURPLE + message + ChatColor.RED + ".");
		
		scheduleReopen();
	}
	
	/**
	 * Reopens the View.
	 */
	private void scheduleReopen(){
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				player.openInventory(StringSelectionInterface.this);
				redraw();
			}
		}, 1);
	}
	

	@Override
	protected Object unparseValue() {
		return value;
	}

	
	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		if(this.stringValueSelector.equals(item)){
			this.isOpeningNewInv = true;
			closeAndReturnToParent(); //Note that we only are closing..
			
			player.sendMessage(ChatColor.RED + "[RaC] Enter a new String. Use " 
					+ ChatColor.LIGHT_PURPLE + "/<anyString>" + ChatColor.RED);
		}
	}

	
	@Override
	protected void onControlItemPressed(ItemStack item) {
	}
	

	
}
