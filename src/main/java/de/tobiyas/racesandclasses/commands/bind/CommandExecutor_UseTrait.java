package de.tobiyas.racesandclasses.commands.bind;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;

public class CommandExecutor_UseTrait extends AbstractCommand implements Listener{

	
	public CommandExecutor_UseTrait() {
		super("usetrait", new String[]{"ut"});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)) return false;
		RaCPlayer player = RaCPlayerManager.get().getPlayer((Player)sender);
		if(!plugin.getPermissionManager().checkPermissions(player.getPlayer(), "RaC.bind")) return true;
		
		
		if(args.length < 1) {
			LanguageAPI.sendTranslatedMessage(player, Keys.wrong_command_use,
					"command", "&c " + getCommandName() + " <skill name>  or  /" 
					+ getCommandName() + " list  to list all available Traits.");
			
			return true;
		}
		
		
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
		
		String spellName = StringUtils.join(args, " ");
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
			sender.sendMessage(ChatColor.RED + "Could not find this Trait.");
			return true;
		}
		
		//trigger the Trait
		selected.triggerOnBind(player);
		player.sendMessage(ChatColor.GREEN + "Triggered.");
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
}
