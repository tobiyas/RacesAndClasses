package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class HolderInventory extends InventoryView{

	/**
	 * The player that will select a holder
	 */
	private final Player player;
	
	/**
	 * The inventory with the selectable Holders
	 */
	private final Inventory holderInventory;
	
	/**
	 * Plugin to call stuff on like config
	 */
	private static final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Generates an Inventory for the Player and the passed HolderManager
	 * 
	 * @param player
	 * @param holderManager
	 */
	public HolderInventory(Player player, AbstractHolderManager holderManager) {
		super();
		
		this.player = player;
		
		int inventorySize = (int) (Math.ceil((holderManager.getAllHolderNames().size() / 9d)) + 1)  * 9;
		if(inventorySize < 35){
			inventorySize = 36;
		}
		
		String inventoryName = "Select your " + holderManager.getContainerTypeAsString() + ", " + player.getName();
		this.holderInventory = Bukkit.getServer().createInventory(player, inventorySize, inventoryName);//inventorySize);
		
		fillWithHolders(holderManager);
	}
	
	
	/**
	 * Fills the inventory with items representing the holders.
	 * 
	 * @param manager
	 */
	private void fillWithHolders(AbstractHolderManager manager){
		for(String holderName : manager.listAllVisibleHolders()){
			AbstractTraitHolder holder = manager.getHolderByName(holderName);
			if(!hasPermission(holder, manager)) continue;
			
			ItemStack item = new ItemStack(Material.BOOK_AND_QUILL);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(holder.getTag());
			
			List<String> lore = meta.hasLore() ? meta.getLore() : new LinkedList<String>();
			
			lore.add(ChatColor.AQUA + "armor:");
			if(holder.getArmorPerms().size() > 0){
				lore.add(ChatColor.LIGHT_PURPLE + holder.getArmorString());
			}else{
				lore.add(ChatColor.LIGHT_PURPLE + "NONE");
			}
			
			lore.add(ChatColor.AQUA + "traits:");
			
			for(Trait trait: holder.getVisibleTraits()){
				lore.add(ChatColor.DARK_AQUA + trait.getName() + ": " );
				lore.add("	" + ChatColor.YELLOW + trait.getPrettyConfiguration());
			}
			
			meta.setLore(lore);
			item.setItemMeta(meta);
			
			holderInventory.addItem(item);
		}
	}

	
	/**
	 * Checks if a player has the Permission for a holder
	 * 
	 * @param holder to check
	 * @param manager the manager of the holder to check
	 * @return true if the player has access, false otherwise.
	 */
	private boolean hasPermission(AbstractTraitHolder holder, AbstractHolderManager manager) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForRaces()) return true;
		
		String holderPermission = PermissionNode.prePlugin + manager.getContainerTypeAsString() + "." + holder.getName();
		return plugin.getPermissionManager().checkPermissionsSilent(player, holderPermission);
	}


	@Override
	public Inventory getTopInventory() {
		return holderInventory;
	}

	@Override
	public Inventory getBottomInventory() {
		return player.getInventory(); //Bukkit.getServer().createInventory(player, 36);
	}

	@Override
	public HumanEntity getPlayer() {
		return player;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}

}
