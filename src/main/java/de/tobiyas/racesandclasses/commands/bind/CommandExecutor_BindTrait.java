package de.tobiyas.racesandclasses.commands.bind;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;

import static de.tobiyas.racesandclasses.translation.languages.Keys.*;

public class CommandExecutor_BindTrait extends AbstractCommand implements Listener{

	
	private static final String BIND_KEY = ChatColor.AQUA + "Trait: ";
	
	
	public CommandExecutor_BindTrait() {
		super("bindtrait", new String[]{"bs", "bind"});
		
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)) return false;
		RaCPlayer player = RaCPlayerManager.get().getPlayer((Player)sender);
		if(!player.hasPermission("RaC.bind")){
			sender.sendMessage(ChatColor.RED + "You do not have Permission for this command.");
			return true;
		}
		
		
		if(args.length < 1) {
			LanguageAPI.sendTranslatedMessage(player, wrong_command_use,
					"command", "&c " + getCommandName() + " <skill name>  or  /" 
					+ getCommandName() + " list  to list all available Traits.");
			
			return true;
		}
		
		String spellName = StringUtils.join(args, " ");
		
		if("list".equalsIgnoreCase(args[0])){
			String traits = ChatColor.GREEN + "Bindable Traits: ";
			for(Trait trait : player.getTraits()){
				if(trait.isBindable()){
					traits += " " + ChatColor.AQUA + trait.getDisplayName() + ChatColor.GREEN + ",";
				}
			}
			
			sender.sendMessage(traits);
			return true;
		}
		
		Trait selected = null;
		for(Trait trait : player.getTraits()){
			if(trait.getName().equalsIgnoreCase(spellName)
					|| trait.getDisplayName().equalsIgnoreCase(spellName)){
				
				if(trait.isBindable()) {
					selected = trait;
					break;
				}
			}
		}
		
		if(selected == null){
			LanguageAPI.sendTranslatedMessage(sender, no_find_trait);
			return true;
		}
		
		int slotNumber = player.getPlayer().getInventory().getHeldItemSlot();
		ItemStack heldItem = player.getItemInHand();
		
		if(heldItem != null && heldItem.getType() != Material.AIR){
			player.sendTranslatedMessage(held_item_not_air);
			return true;
		}
		
		ItemStack item = new ItemStack(Material.MELON_SEEDS);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(BIND_KEY + selected.getDisplayName());
		List<String> lore = new LinkedList<String>();
		lore.add("Switch the item to this slot to cast the Spell,");
		lore.add("or simply right-click with it.");
		lore.add("");
		lore.add(ChatColor.YELLOW + selected.getPrettyConfiguration());
		
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		
		player.getPlayer().getInventory().setItem(slotNumber, item);
		player.sendMessage(ChatColor.GREEN + selected.getDisplayName() + " Slot: " + slotNumber);
		return true;
	}
	
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent event){
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
					if(! ((TraitWithRestrictions) selected).checkRestrictions(wrapper)){
						LanguageAPI.sendTranslatedMessage(player, restrictions_not_met);
						return;
					}
				}
				
				TraitResults result = selected.triggerOnBind(player);
				event.setCancelled(true);
				
				if(result.isTriggered()){
					LanguageAPI.sendTranslatedMessage(player, trait_toggled, "%NAME%", selected.getDisplayName());
				}else{
					LanguageAPI.sendTranslatedMessage(player, trait_failed, "%NAME%", selected.getDisplayName());
				}
			}
		}
	}
	

	@EventHandler
	public void playerThrowsAway(PlayerDropItemEvent event){
		Item item = event.getItemDrop();
		if(item == null) return;

		ItemStack stack = item.getItemStack();
		if(stack == null) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		if(getBoundTrait(stack, player) == null) return;
		
		item.remove();
	}
	
	@EventHandler
	public void cancleCrafting(CraftItemEvent event){
		Player clicked = (Player)event.getWhoClicked();
		if(clicked == null) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(clicked);
		if(player == null) return;
		
		for(ItemStack item : event.getInventory()){
			if(getBoundTrait(item, player) != null){
				//No crafting of quickslot items
				event.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler
	public void onItemRename(InventoryClickEvent event){
		if(event.getInventory().getType() != InventoryType.ANVIL) return;
		AnvilInventory inv = (AnvilInventory) event.getInventory();
		
		Player clicker = (Player) event.getWhoClicked();
		RaCPlayer player = RaCPlayerManager.get().getPlayer(clicker);
		
		for(ItemStack item : inv){
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
				if(! ((TraitWithRestrictions) selected).checkRestrictions(wrapper)){
					LanguageAPI.sendTranslatedMessage(player, restrictions_not_met);
					return;
				}
			}
			
			TraitResults result = selected.triggerOnBind(player);
			
			if(result.isTriggered()){
				LanguageAPI.sendTranslatedMessage(player, trait_toggled, "%NAME%", selected.getDisplayName());
			}else{
				LanguageAPI.sendTranslatedMessage(player, trait_failed, "%NAME%", selected.getDisplayName());
			}
			
			event.setCancelled(true);
			
			//here we just reset the itemslot in case it failed to reset...
			new BukkitRunnable() {
				@Override
				public void run() {
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

	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		
		List<String> values = new LinkedList<String>();
		if(!(sender instanceof Player)) return values;
		
		if(args.length == 1){
			for(Trait trait : RaCPlayerManager.get().getPlayer((Player) sender).getTraits()){
				if(!trait.isBindable()) continue;
				
				if(trait.getDisplayName().toLowerCase().startsWith(args[0].toLowerCase())){
					values.add(trait.getDisplayName());
				}
			}
		}
		
		return values;
	}
}
