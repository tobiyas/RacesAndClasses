package de.tobiyas.racesandclasses.hotkeys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;

public class HotKeyInventory {

	/**
	 * The Material to use for shortcut Items.
	 */
	private static final Material shortcutMat = Material.SHEARS;
	
	/**
	 * The Max-Durability to have.
	 */
	private static final short maxShortcutDurability = shortcutMat.getMaxDurability();
	
	/**
	 * The Key at start to identify the Item.
	 */
	private static final String BIND_KEY = ChatColor.AQUA + "Trait: ";
	
	
	/**
	 * The Hot-Key Bindings.
	 */
	private final Map<Integer,Trait> traitBindings = new HashMap<Integer, Trait>();
	
	
	/**
	 * The Old Hotkey Bar.
	 */
	private final Map<Integer,ItemStack> oldHotkeyBar = new HashMap<Integer, ItemStack>();
	
	
	/**
	 * The Player how is the Owner of the HotKeys.
	 */
	private final RaCPlayer player;
	
	/**
	 * If the Player is in Skill Mode or not.
	 */
	private boolean isInSkillMode = false;
	
	
	public HotKeyInventory(RaCPlayer player) {
		this.player = player;
	}
	
	
	/**
	 * Binds a trait to the slot passed.
	 * 
	 * @param slot to bind to
	 * @param trait to bind.
	 */
	public void bindTrait(int slot, Trait trait){
		//first check if legit trait.
		if(trait == null || !trait.isBindable()) return;
		
		traitBindings.put(slot, trait);
	}
	
	/**
	 * Removes any trait from the Slot.
	 * 
	 * @param slot to clear
	 */
	public void clearSlot(int slot){
		traitBindings.remove(slot);
	}
	
	
	/**
	 * Clears all Trait slots.
	 */
	public void clearAllSlots(){
		traitBindings.clear();
	}
	
	
	/**
	 * Call this regularly to update playerInventory View.
	 */
	public void updatePlayerInventory(){
		if(!isInSkillMode) return; //nothing to update.
		if(traitBindings.isEmpty()) return; //nothing to update.
		
		for(Entry<Integer,Trait> entry : traitBindings.entrySet()){
			int slot = entry.getKey();
			Trait trait = entry.getValue();
			
			ItemStack item = player.getPlayer().getInventory().getItem(slot);
			if(item == null || item.getType() != shortcutMat) continue;
			
			if(trait instanceof TraitWithRestrictions){
				TraitWithRestrictions res = (TraitWithRestrictions) trait;
				
				int maxCD = res.getMaxUplinkTime();
				if(maxCD > 0){
					String cooldownName = "trait." + trait.getDisplayName();
					int cd = CooldownApi.getCooldownOfPlayer(player.getName(), cooldownName);
					
					if(cd > 0){
						float percent = (float)cd / (float)maxCD;
						float val = maxShortcutDurability * percent;
						if(val <= 0) val = 1; if(val >= maxShortcutDurability) val = maxShortcutDurability - 1;
						
						item.setDurability((short) val);
					}
				}
			}
		}
	}
	
	/**
	 * Changes the Hotbar to the build Inv.
	 */
	public void changeToBuildInv(){
		if(!isInSkillMode) return;
		
		Player player = this.player.getPlayer();
		if(player == null) return;
		
		//first clear the old ones.
		for(int i = 0; i < 9; i++) player.getInventory().setItem(0, null);
		
		//now refill with the old ones.
		for(Entry<Integer,ItemStack> entry : oldHotkeyBar.entrySet()){
			int slot = entry.getKey();
			ItemStack item = entry.getValue();
			
			player.getInventory().setItem(slot, item);
		}
		
		oldHotkeyBar.clear();
	}
	
	/**
	 * Change the Build-Menu to the Skill Menu.
	 */
	public void changeToSkillInv(){
		if(isInSkillMode) return;
		
		Player player = this.player.getPlayer();
		if(player == null) return;
		
		//remove in case...
		oldHotkeyBar.clear();
		
		//first save the old items
		for(int i = 0; i < 9; i++){
			ItemStack item = player.getInventory().getItem(i);
			if(item != null) oldHotkeyBar.put(i, item);
		}
		
		//now set the Items to the quickslot bar.
		for(int i = 0; i < 9; i++){
			ItemStack item = generateItem(traitBindings.get(i));
			player.getInventory().setItem(i, item == null ? getEmptyItem() : item);
		}
	}
	
	
	/**
	 * Generates an Item to the Trait.
	 * 
	 * @param trait to generate to
	 * 
	 * @return the item or null if null is passed.
	 */
	public static ItemStack generateItem(Trait trait){
		if(trait == null) return null;
		
		ItemStack item = new ItemStack(shortcutMat);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(BIND_KEY + trait.getDisplayName());
		
		List<String> lore = new LinkedList<String>();
		lore.add("Switch the item to this slot to cast the Spell,");
		lore.add("or simply right-click with it.");
		lore.add("");
		lore.add(ChatColor.YELLOW + trait.getPrettyConfiguration());
		
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		
		return item;
	}
	
	
	/**
	 * Generates an Empty Slot itme.
	 * 
	 * @return the item for an empty Slot.
	 */
	public static ItemStack getEmptyItem(){
		ItemStack item = new ItemStack(Material.FLINT);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(BIND_KEY + "EMPTY");
		
		List<String> lore = new LinkedList<String>();
		lore.add("Put an Skill to this Slot to to fill it.");
		
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		
		return item;
	}
	
	
}
