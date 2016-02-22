package de.tobiyas.racesandclasses.hotkeys;

import static de.tobiyas.racesandclasses.translation.languages.Keys.trait_failed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.trait_toggled;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitRegionChecker;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class Listener_HotKey implements Listener {

	
	private static final String BIND_KEY = ChatColor.AQUA + "Trait: ";
	
	
	public Listener_HotKey() {
		RacesAndClasses.getPlugin().registerEvents(this);
	}
	
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerInteract(PlayerInteractEvent event){
		//we need to ignore canceled since other plugins / Stuff can also cancel.
		
		ItemStack item = event.getItem();
		if(item == null) return;
		
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		
		String spellName = item.getItemMeta().getDisplayName();
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		
		if(!spellName.startsWith(BIND_KEY)) return;
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Trait selected = getBoundTrait(item, player);
			if(selected != null){
				event.setCancelled(true);
				
				EventWrapper wrapper = EventWrapperFactory.buildOnlyWithplayer(player);
				if(selected instanceof TraitWithRestrictions){
				
				TraitRestriction restriction = ((TraitWithRestrictions) selected).checkRestrictions(wrapper);
				if(restriction != TraitRestriction.None){
						LanguageAPI.sendTranslatedMessage(player, restriction.translation());
						return;
					}
				}
				
				if(TraitRegionChecker.isInDisabledLocation(player.getLocation())){
					LanguageAPI.sendTranslatedMessage(player, Keys.in_restricted_area);
					return;
				}
				
				TraitResults result = selected.triggerOnBind(player);
				event.setCancelled(true);
				
				if(result.isTriggered()){
					LanguageAPI.sendTranslatedMessage(player, trait_toggled, "name", selected.getDisplayName());
				}else{
					LanguageAPI.sendTranslatedMessage(player, trait_failed, "name", selected.getDisplayName());
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerDead(PlayerDeathEvent event){
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(event.getEntity());
		if(pl.getHotkeyInventory().isInBuildingMode()) return;
		
		pl.getHotkeyInventory().changeToBuildInv();
		ItemStack empty = HotKeyInventory.getEmptyItem();
		Iterator<ItemStack> it = event.getDrops().iterator();
		
		while(it.hasNext()){
			ItemStack item = it.next();
			if(item == null || item.getType() == Material.AIR) continue;
			
			if(getBoundTrait(item, pl) != null || item.isSimilar(empty)) it.remove();
		}
	}
	
	
	@EventHandler
	public void playerRespawn(PlayerRespawnEvent event){
		if(event.getPlayer() == null) return;
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(event.getPlayer());
		if(pl == null) return;
		
		final HotKeyInventory inv = pl.getHotkeyInventory();
		//should not happen... But when players are dead they are online.. wtf...
		if(inv == null) return; 
		
		if(inv.isInSkillMode()) return;
		new DebugBukkitRunnable("HotKeyListenerInvUpdate") {
			@Override
			protected void runIntern() {
				inv.forceUpdateOfInv();
			}
		}.runTaskLater(RacesAndClasses.getPlugin(), 1);
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void inventoryMove(InventoryClickEvent event){
		if(event.isCancelled()) return;
		if(event.getView() instanceof HotKeyView) return;
		if(!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		
		if(racPlayer.getHotkeyInventory().isInBuildingMode()) return;
		
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		if(disabled.contains(event.getSlot())) return;
		
		final InventoryView view = event.getView();
		if(event.getRawSlot() <= 9) return;
		
		int slot = event.getSlot();
		if(!racPlayer.getHotkeyInventory().getBindings().containsKey(slot)) return;
		
		if(event.getClick() == ClickType.NUMBER_KEY || slot < 9){
			event.setCancelled(true);
			
			final ItemStack toSet = event.getCursor();

			if(toSet == null || toSet.getType() == Material.AIR) return;
			
			//try setting the slot if needed.
			//so we schedule this to the end of this tick.
			new DebugBukkitRunnable("HotKeyListener"){
				@Override
				protected void runIntern() {
					view.setCursor(toSet);
				}
			}.runTask(RacesAndClasses.getPlugin());
			
			if(event.getWhoClicked() instanceof Player){
				resyncInv((Player) event.getWhoClicked());
			}
		}
	}
	
	/**
	 * Schedules a Player Inv Resync.
	 * 
	 * @param player to resync
	 */
	private void resyncInv(final Player player){
		new DebugBukkitRunnable("HotKeyInvSyncer"){
			@SuppressWarnings("deprecation")
			@Override
			protected void runIntern() {
				try{
					player.updateInventory();
				}catch(Throwable exp){}
			}
		}.runTaskLater(RacesAndClasses.getPlugin(), 1);
	}



	@EventHandler
	public void shearEvent(PlayerShearEntityEvent event){
		Player player = event.getPlayer();
		ItemStack inHand = player.getItemInHand();
		if(inHand == null) return;
		
		Trait trait = getBoundTrait(inHand, RaCPlayerManager.get().getPlayer(player));
		if(trait != null) event.setCancelled(true);
	}
	

	@EventHandler
	public void playerThrowsAway(PlayerDropItemEvent event){
		Item item = event.getItemDrop();
		if(item == null) return;

		ItemStack stack = item.getItemStack();
		if(stack == null) return;
		
		if(stack.isSimilar(HotKeyInventory.getEmptyItem())) event.setCancelled(true);
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		if(getBoundTrait(stack, player) == null) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void cancleCrafting(CraftItemEvent event){
		Player clicked = (Player)event.getWhoClicked();
		if(clicked == null) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(clicked);
		if(player == null) return;
		
		
		for(ItemStack item : event.getInventory()){
			if(item == null) continue;
			
			if(item.isSimilar(HotKeyInventory.getEmptyItem())) {
				event.setCancelled(true);
				break;
			}
			
			if(getBoundTrait(item, player) != null){
				//No crafting of quickslot items
				event.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.isCancelled()) return;
		
		ItemStack inHand = event.getPlayer().getItemInHand();
		Trait trait = getBoundTrait(inHand, RaCPlayerManager.get().getPlayer(event.getPlayer()));
		
		if(trait != null) event.setCancelled(true);
		if(inHand.isSimilar(HotKeyInventory.getEmptyItem())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onItemRename(InventoryClickEvent event){
		if(event.getInventory().getType() != InventoryType.ANVIL) return;
		AnvilInventory inv = (AnvilInventory) event.getInventory();
		
		Player clicker = (Player) event.getWhoClicked();
		RaCPlayer player = RaCPlayerManager.get().getPlayer(clicker);
		
		for(ItemStack item : inv){
			if(item == null) continue;
			
			if(item.isSimilar(HotKeyInventory.getEmptyItem())) {
				event.setCancelled(true);
				break;
			}
			
			if(getBoundTrait(item, player) != null){
				//No crafting of quickslot items
				event.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler
	public void onTraitUse(PlayerItemHeldEvent event){
		final Player pl = event.getPlayer();
		final int oldSlot = event.getPreviousSlot();
		final int newSlot = event.getNewSlot();
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(pl);
		
		//only slot change of > 1 are alowed.
		if(Math.abs(newSlot - oldSlot) < 2) return;
		
		ItemStack newItem = pl.getInventory().getItem(event.getNewSlot());
		
		Trait selected = getBoundTrait(newItem, player);
		if(selected != null){
			event.setCancelled(true);
			
			EventWrapper wrapper = EventWrapperFactory.buildOnlyWithplayer(pl);
			if(selected instanceof TraitWithRestrictions){
				TraitRestriction restriction =  ((TraitWithRestrictions) selected).checkRestrictions(wrapper);
				if(restriction != TraitRestriction.None){
					LanguageAPI.sendTranslatedMessage(player, restriction.translation());
					return;
				}
			}
			
			if(TraitRegionChecker.isInDisabledLocation(player.getLocation())){
				LanguageAPI.sendTranslatedMessage(player, Keys.in_restricted_area);
				return;
			}
			
			TraitResults result = selected.triggerOnBind(player);
			if(result.isTriggered()){
				LanguageAPI.sendTranslatedMessage(player, trait_toggled, "name", selected.getDisplayName());
			}else{
				LanguageAPI.sendTranslatedMessage(player, trait_failed, "name", selected.getDisplayName());
			}
			
			event.setCancelled(true);
			
			//here we just reset the itemslot in case it failed to reset...
			new DebugBukkitRunnable("ListenerHotKeyUpdateSlots"){
				@Override
				protected void runIntern() {
					pl.getInventory().setHeldItemSlot(pl.getInventory().getHeldItemSlot());						
				}
			}.runTaskLater(RacesAndClasses.getPlugin(), 1);
		}
	}
	
	
	/**
	 * Returns the Trait associated with this Item and Player.
	 * If not combinable or nothing found, null is returned.
	 * 
	 * @param item to check
	 * @param player to check.
	 * 
	 * @return Trait to check.
	 */
	public static Trait getBoundTrait(ItemStack item, RaCPlayer player){
		if(item == null || player == null) return null;
		
		if(!item.hasItemMeta()) return null;
		if(!item.getItemMeta().hasDisplayName()) return null;
		
		String displayName = item.getItemMeta().getDisplayName();
		if(!displayName.startsWith(BIND_KEY)) return null;
		displayName = displayName.replace(BIND_KEY, "");
		
		for(Trait trait : player.getTraits()){
			if(trait.getDisplayName().equalsIgnoreCase(displayName)){
				return trait;
			}
		}
		
		return null;
	}
	
	
}
