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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;

public class CommandExecutor_BindSkill extends AbstractCommand implements Listener{

	
	private static final String BIND_KEY = ChatColor.AQUA + "Skill: ";
	
	
	public CommandExecutor_BindSkill() {
		super("bindskill", new String[]{"bs", "bind"});
		
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
		
		
		if(args.length < 1) return false;
		String spellName = StringUtils.join(args, " ");
		
		Trait selected = null;
		for(Trait trait : player.getTraits()){
			if(trait.getName().equalsIgnoreCase(spellName)
					|| trait.getDisplayName().equalsIgnoreCase(spellName)){
				selected = trait;
				break;
			}
		}
		
		if(selected == null){
			sender.sendMessage(ChatColor.RED + "Could not find this Trait.");
			return true;
		}
		
		int slotNumber = player.getPlayer().getInventory().getHeldItemSlot();
		ItemStack heldItem = player.getItemInHand();
		
		if(heldItem != null && heldItem.getType() != Material.AIR){
			player.sendTranslatedMessage(Keys.held_item_not_air);
			return true;
		}
		
		ItemStack item = new ItemStack(Material.ANVIL);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(BIND_KEY + selected.getDisplayName());
		List<String> lore = new LinkedList<String>();
		lore.add("Switch the item to this slot to cast the Spell");
		lore.add("/ use the Trait.");
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		
		player.getPlayer().getInventory().setItem(slotNumber, item);
		player.sendMessage(ChatColor.GREEN + selected.getDisplayName() + " Slot: " + slotNumber);
		return true;
	}
	
	
	@EventHandler
	public void placeBlockForQuickSlotNotPossible(BlockPlaceEvent event){
		ItemStack item = event.getItemInHand();
		
		if(item.getType() != Material.WOOL) return;
		
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		
		String spellName = item.getItemMeta().getDisplayName();
		
		if(!spellName.startsWith(BIND_KEY)) return;
		event.setCancelled(true); //you can't place quickslot items
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
		Player pl = event.getPlayer();
		RaCPlayer player = RaCPlayerManager.get().getPlayer(pl);
		
		//only slot change of > 1 are alowed.
		if(Math.abs(event.getNewSlot() - event.getPreviousSlot()) < 2) return;
		
		ItemStack newItem = pl.getInventory().getItem(event.getNewSlot());
		
		Trait selected = getBoundTrait(newItem, player);
		if(selected != null){
			//TODO somehow trigger spell!
			
			EventWrapper wrapper = EventWrapperFactory.buildOnlyWithplayer(pl);
			if(selected.canBeTriggered(wrapper)){
				selected.trigger(wrapper);
				player.sendMessage(ChatColor.GREEN + selected.getDisplayName() + " triggered.");
				event.setCancelled(true);
			}			
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
			if(trait.getDisplayName().equals(displayName)){
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
