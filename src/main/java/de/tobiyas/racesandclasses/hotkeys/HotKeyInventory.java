package de.tobiyas.racesandclasses.hotkeys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.util.config.YAMLConfigExtended;

public class HotKeyInventory {

	private static final String CONFIG_SEPERATOR = String.valueOf('|');
	private static final String CONFIG_BINDINGS_PATH = "bindings";
	
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
		
		loadFromFile();
	}
	
	/**
	 * Loads the HotKeyInventory Bindings from the File.
	 */
	public void loadFromFile(){
		traitBindings.clear();
		
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		List<String> bindings = config.getStringList(CONFIG_BINDINGS_PATH);
		
		if(!bindings.isEmpty()){
			for(String binding : bindings){
				String[] split = binding.split(Pattern.quote(CONFIG_SEPERATOR));
				if(split.length != 2) continue;
				
				try{
					int key = Integer.parseInt(split[0]);
					String displayname = split[1];
					
					for(Trait trait : player.getTraits()){
						if(trait.getDisplayName().equals(displayname)){
							this.traitBindings.put(key, trait);
						}
					}
				}catch(Throwable exp){}
			}
		}
	}
	
	/**
	 * Saves the Bindings.
	 */
	public void save(){
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		
		List<String> bindings = new LinkedList<String>();
		for(Entry<Integer,Trait> entry : traitBindings.entrySet()){
			String toSave = entry.getKey() + CONFIG_SEPERATOR + entry.getValue().getDisplayName();
			bindings.add(toSave);
		}
		
		if(!bindings.isEmpty()){
			config.set(CONFIG_BINDINGS_PATH, bindings);
		}
		
		
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
		save();
	}
	
	/**
	 * Removes any trait from the Slot.
	 * 
	 * @param slot to clear
	 */
	public void clearSlot(int slot){
		if(!traitBindings.containsKey(slot)) return;
		
		traitBindings.remove(slot);
		save();
	}
	
	
	/**
	 * Clears all Trait slots.
	 */
	public void clearAllSlots(){
		traitBindings.clear();
		save();
	}
	
	
	/**
	 * Call this regularly to update playerInventory View.
	 */
	public void updatePlayerInventory(){
		if(!player.isOnline()) return;
		
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
					
					if(cd >= 0){
						float percent = (float)cd / (float)maxCD;
						float val = maxShortcutDurability * percent;
						if(val <= 0) val = 1; if(val >= maxShortcutDurability) val = maxShortcutDurability - 1;
						
						item.setDurability((short) val);
					}
					
					if(cd < 0){
						item.setDurability((short) 0);
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
		if(!player.isOnline()) return; //can't set offline players items!
		
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		
		//first clear the old ones.
		for(int i = 0; i < 9; i++) {
			if(!disabled.contains(i)) player.getInventory().setItem(i, null);
		}
		
		//now refill with the old ones.
		for(Entry<Integer,ItemStack> entry : oldHotkeyBar.entrySet()){
			int slot = entry.getKey();
			ItemStack item = entry.getValue();
			
			if(!disabled.contains(slot)) continue;
			
			player.getInventory().setItem(slot, item);
		}
		
		oldHotkeyBar.clear();

		isInSkillMode = false;
	}
	
	/**
	 * Change the Build-Menu to the Skill Menu.
	 */
	public void changeToSkillInv(){
		if(isInSkillMode) return;
		
		Player player = this.player.getPlayer();
		if(player == null) return;
		
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		
		//remove in case...
		oldHotkeyBar.clear();
		
		//first save the old items
		for(int i = 0; i < 9; i++){
			if(disabled.contains(i)) continue;
			
			ItemStack item = player.getInventory().getItem(i);
			if(item != null) oldHotkeyBar.put(i, item.clone());
		}
		
		//now set the Items to the quickslot bar.
		for(int i = 0; i < 9; i++){
			if(disabled.contains(i)) continue;
			
			ItemStack item = generateItem(traitBindings.get(i));
			player.getInventory().setItem(i, item == null ? getEmptyItem() : item);
		}
		
		isInSkillMode = true;
	}
	
	
	/**
	 * This forces reseting all Mats when in Skil mode.
	 */
	public void forceUpdateOfInv(){
		if(!isInSkillMode) return;
		
		Player player = this.player.getPlayer();
		if(player == null) return;
		
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		
		//now set the Items to the quickslot bar.
		for(int i = 0; i < 9; i++){
			if(disabled.contains(i)) continue;
			
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
	
	
	/**
	 * Returns true if in Building mode.
	 * 
	 * @return true if in building mode.
	 */
	public boolean isInBuildingMode(){
		return !isInSkillMode;
	}
	
	/**
	 * Returns true if in Skill mode.
	 * 
	 * @return true if in Skill mode.
	 */
	public boolean isInSkillMode(){
		return isInSkillMode;
	}


	/**
	 * Returns a copy of the Bindings.
	 * 
	 * @return bindings.
	 */
	public Map<Integer,Trait> getBindings() {
		return new HashMap<Integer,Trait>(this.traitBindings);
	}
	
	
}
