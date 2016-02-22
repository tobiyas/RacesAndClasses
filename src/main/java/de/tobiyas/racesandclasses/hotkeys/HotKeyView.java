package de.tobiyas.racesandclasses.hotkeys;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.bind.CommandExecutor_BindTrait;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;

public class HotKeyView extends BasicSelectionInterface {

	/**
	 * The RaC Player that contains this.
	 */
	private final RaCPlayer racPlayer;
	
	/**
	 * The Map of quickslot items.
	 */
	private final Map<Integer,ItemStack> emptyItems = new HashMap<Integer, ItemStack>();
	
	/**
	 * The Currently selected Trait.
	 */
	private Trait selectedTrait = null;
	
	/**
	 * If the player was in Skillmode before opening.
	 */
	private final boolean skillModeBefore;
	
	
	public HotKeyView(Player player) {
		this(RaCPlayerManager.get().getPlayer(player));
	}
	
	public HotKeyView(RaCPlayer player) {
		super(player.getPlayer(), null, "Your skills", "All Skills", RacesAndClasses.getPlugin());

		this.skillModeBefore = player.getHotkeyInventory().isInSkillMode();
		player.getHotkeyInventory().changeToBuildInv();
		
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		for(int i = 0; i < 9; i ++){

			ItemStack item = null;
			if(disabled.contains(i)){
				item = generateItem(Material.ARROW, ChatColor.RED + "DISABLED", "This slot is disabled!");
			}else{
				item = generateItem(Material.WOOL, ChatColor.GREEN + "EMPTY Slot" , "slot:" + i);
			}
			
			emptyItems.put(i, item);
		}
		
		this.racPlayer = player;
		redraw();
		
	}
	
	
	/**
	 * Redraws the View.
	 */
	private void redraw(){
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		
		this.getTopInventory().clear();
		this.getBottomInventory().clear();
		
		//first get the traits we may use.
		List<Trait> traits = racPlayer.getTraits();
		Iterator<Trait> it =  traits.iterator();
		while(it.hasNext()){
			Trait trait = it.next();
			if(!trait.isBindable()) {
				it.remove();
				continue;
			}
			
			if(trait instanceof TraitWithRestrictions){
				TraitWithRestrictions restrictions = (TraitWithRestrictions) trait;
				int playerLevel = racPlayer.getLevelManager().getCurrentLevel();
				
				if(!restrictions.isInLevelRange(playerLevel)){
					it.remove();
					continue;
				}
			}
		}
		
		//now sort.
		Collections.sort(traits, new Comparator<Trait>() {
			@Override
			public int compare(Trait o1, Trait o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});
		
		//pin on Window.
		for(int i = 0; i < 27; i++){
			if( i >= traits.size() ) break;
			
			Trait trait = traits.get(i);
			ItemStack item = CommandExecutor_BindTrait.generate(trait);
			getTopInventory().setItem(i, item);
		}
		
		//post empty Items first so they can be overdrawn.
		for(Entry<Integer,ItemStack> entry : emptyItems.entrySet()){			
			int index = entry.getKey();
			
			ItemStack item = entry.getValue().clone();
			getBottomInventory().setItem(index, item);
		}
		
		//post current Traits.
		for(Entry<Integer, Trait> entry : racPlayer.getHotkeyInventory().getBindings().entrySet()){
			int slot = entry.getKey();
			if(disabled.contains(slot)) continue;
			
			Trait bound = entry.getValue();
			
			getBottomInventory().setItem(slot, CommandExecutor_BindTrait.generate(bound));
		}
		
		this.setCursor(selectedTrait == null ? null : CommandExecutor_BindTrait.generate(selectedTrait));
	}
	

	@Override
	protected boolean onBackPressed() {
		return true;
	}

	@Override
	protected void onAcceptPressed() {
	}

	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		Trait trait = Listener_HotKey.getBoundTrait(item, racPlayer);
		
		if(trait != null){
			setCursor(item.clone());
			selectedTrait = trait;
		}
		
		redraw();
	}

	@EventHandler
	@Override
	public void onInterfaceInteract(InventoryClickEvent event) {
		if(event.getView() != this) return;
		
		//block all SHIFT clicks. Just to be sure...
		if(event.isShiftClick()){
			event.setCancelled(true);
			
			redraw();
			return;
		}
		
		super.onInterfaceInteract(event);
	}
	
	
	@EventHandler
	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getView() != this) return;
		this.setCursor(null);
		
		super.onInventoryClose(event);
		
		if(skillModeBefore){
			HotKeyInventory inv = racPlayer.getHotkeyInventory();
			inv.changeToSkillInv();
		}
	}
	
	
	@Override
	protected void onControlItemPressed(ItemStack item) {
		int index = -1;
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		
		for(int i = 0; i < 9; i++){
			ItemStack toCheck = getBottomInventory().getItem(i);
			if(toCheck.isSimilar(item) && !disabled.contains(i)){
				index = i;
			}
		}
		
		//We can have index 0-8.
		if( index >= 0 ){
			racPlayer.getHotkeyInventory().clearSlot(index);
			if(selectedTrait != null) racPlayer.getHotkeyInventory().bindTrait(index, selectedTrait);
			
			selectedTrait = null;
			setCursor(null);
		}
		
		
		redraw();
	}
	
	
	@Override
	protected void scheduleOpeningOfParent() {
		racPlayer.getHotkeyInventory().forceUpdateOfInv();
		
		super.scheduleOpeningOfParent();
	}
	
	

	
}
