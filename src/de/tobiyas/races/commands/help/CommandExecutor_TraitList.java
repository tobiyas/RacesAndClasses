package de.tobiyas.races.commands.help;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.TraitsList;

public class CommandExecutor_TraitList implements CommandExecutor {

	private Races plugin;

	public CommandExecutor_TraitList(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("traitlist").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /traitlist.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String page = "0";
		if(args.length == 1)
			page = args[0];
		
		HashSet<String> category = TraitsList.getCategory(page);
		
		sender.sendMessage(ChatColor.YELLOW + "======" + ChatColor.LIGHT_PURPLE + "Trait-List PAGE: " + page + ChatColor.YELLOW + "======");
		for(String trait : category)
			sender.sendMessage(ChatColor.LIGHT_PURPLE + trait);
		
		return true;
	}

}
