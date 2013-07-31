package de.tobiyas.racesandclasses.commands.help;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;

public class CommandExecutor_TraitList implements CommandExecutor {

	private RacesAndClasses plugin;

	public CommandExecutor_TraitList(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("traitlist").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /traitlist.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(args.length == 0){
			postAllCategorys(sender);
			return true;
		}
		
		String page = "0";
		if(args.length == 1)
			page = args[0];
		
		HashSet<String> category = TraitsList.getCategory(page);
		
		sender.sendMessage(ChatColor.YELLOW + "======" + ChatColor.LIGHT_PURPLE + "Trait-List PAGE: " + page + ChatColor.YELLOW + "======");
		if(category == null)
			return true;
		for(String trait : category)
			sender.sendMessage(ChatColor.LIGHT_PURPLE + trait);
		
		return true;
	}
	
	private void postAllCategorys(CommandSender sender){
		sender.sendMessage(ChatColor.YELLOW + "======" + ChatColor.LIGHT_PURPLE + "Trait-Categories:" + ChatColor.YELLOW + "======");
		
		HashSet<String> categories = TraitsList.getAllCategories();
		for(String category : categories){
			sender.sendMessage(ChatColor.AQUA + category);
		}
	}

}
