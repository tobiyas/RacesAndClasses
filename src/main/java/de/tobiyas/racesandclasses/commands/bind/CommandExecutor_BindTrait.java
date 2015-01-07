package de.tobiyas.racesandclasses.commands.bind;

import static de.tobiyas.racesandclasses.translation.languages.Keys.held_item_not_air;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_find_trait;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.hotkeys.HotKeyView;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.util.player.PlayerUtils;

public class CommandExecutor_BindTrait extends AbstractCommand implements Listener{

	
	private static final String BIND_KEY = ChatColor.AQUA + "Trait: ";
	
	/**
	 * The Material to bind.
	 */
	private static final Material BindMat = Material.SHEARS;
	
	
	public CommandExecutor_BindTrait() {
		super("bindtrait", new String[]{"bt", "bind"});
		
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
	}


	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(args.length == 1 && sender.isOp() && args[0].equals("reset")){
			for(Player player : PlayerUtils.getOnlinePlayers()){
				RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
				racPlayer.getHotkeyInventroy().changeToBuildInv();
			}
			
			return true;
		}
		
		if(!(sender instanceof Player)) return false;
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_hotkeysEnabled()){
			sender.sendMessage(ChatColor.RED + "Hotkeys are Disabled.");
			return true;
		}
		
		boolean useNewBindSystem = plugin.getConfigManager().getGeneralConfig().getConfig_useNewTraitBindSystem();
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer((Player)sender);
		if(!player.hasPermission("RaC.bind")){
			sender.sendMessage(ChatColor.RED + "You do not have Permission for this command.");
			return true;
		}
		
		if(sender.isOp() && args.length == 1 && args[0].equals("resync")){
			player.getPlayer().updateInventory();
			return true;
		}
		
		
		if(useNewBindSystem){
			boolean isBindingMode = player.getHotkeyInventroy().isInSkillMode();
			boolean openView = args.length == 1 && args[0].equalsIgnoreCase("open");
			
			if(openView){
				//opening view
				player.getPlayer().openInventory(new HotKeyView(player));
				return true;
			}
			
			if(isBindingMode){
				player.getHotkeyInventroy().changeToBuildInv();
				player.sendMessage("Change to Build Mode.");
			}else{
				player.getHotkeyInventroy().changeToSkillInv();
				player.sendMessage("Change to Battle Skill.");
			}
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
		
		ItemStack item = generate(selected);
		
		player.getPlayer().getInventory().setItem(slotNumber, item);
		player.sendMessage(ChatColor.GREEN + selected.getDisplayName() + " Slot: " + slotNumber);
		return true;
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
	
	
	
	public static ItemStack generate(Trait trait){
		ItemStack item = new ItemStack(BindMat);
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

}
