package de.tobiyas.races.commands.races;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.TraitsList;

public class CommandExecutor_RaceHelp implements CommandExecutor {
	
	private Races plugin;
	
	public CommandExecutor_RaceHelp(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("racehelp").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racehelp.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(args.length == 2 && args[0].equalsIgnoreCase("traits")){			
			return outTraitHelp(sender, args[1]);
		}
		
		sender.sendMessage(ChatColor.RED + "Not implemented yet.");
		
		return true;
	}
	
	private boolean outTraitHelp(CommandSender sender, String trait){		
		Class<?> clazz = TraitsList.getClassOfTrait(trait);
		if(clazz == null){
			sender.sendMessage(ChatColor.RED + "Trait: " + ChatColor.LIGHT_PURPLE + trait + ChatColor.RED + " not found.");
			return true;
		}
		
		try{
			sender.sendMessage(ChatColor.YELLOW + "===Trait: " + ChatColor.YELLOW + trait + ChatColor.YELLOW + "===");
			clazz.getMethod("pasteHelpForTrait", CommandSender.class).invoke(clazz, sender);
		}catch(Exception e){
			sender.sendMessage(ChatColor.RED + "Trait: " + ChatColor.LIGHT_PURPLE + trait + ChatColor.RED + " has no Help");
			return true;
		}
		
		return true;
	}

}
