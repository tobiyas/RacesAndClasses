package de.tobiyas.racesandclasses.commands.bind;

import static de.tobiyas.racesandclasses.translation.languages.Keys.trait_failed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.trait_toggled;

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
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitRegionChecker;

public class CommandExecutor_UseTrait extends AbstractCommand implements Listener{

	
	public CommandExecutor_UseTrait() {
		super("usetrait", new String[]{"ut"});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)) return false;
		RaCPlayer player = RaCPlayerManager.get().getPlayer((Player)sender);
		if(!plugin.getPermissionManager().checkPermissions(player.getPlayer(), "RaC.use")) return true;
		
		
		if(args.length < 1) {
			LanguageAPI.sendTranslatedMessage(player, Keys.wrong_command_use,
					"command", "&c " + getCommandNames().iterator().next() + " <skill name>  or  /" 
					+ getCommandNames().iterator().next() + " list  to list all available Traits.");
			
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
		
		if(TraitRegionChecker.isInDisabledLocation(player.getLocation())){
			LanguageAPI.sendTranslatedMessage(player, Keys.in_restricted_area);
			return true;
		}
		
		//trigger the Trait
		TraitResults result = selected.triggerOnBind(player);
		if(result.isTriggered()){
			LanguageAPI.sendTranslatedMessage(player, trait_toggled, "name", selected.getDisplayName());
		}else{
			LanguageAPI.sendTranslatedMessage(player, trait_failed, "name", selected.getDisplayName());
		}
		
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
