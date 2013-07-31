package de.tobiyas.racesandclasses.commands.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;

public class CommandExecutor_RaceHelp implements CommandExecutor {
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_RaceHelp(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racehelp").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racehelp.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(args.length == 0){
			PostPage.postPage(sender, 1);
			return true;
		}
		
		String commandString = args[0];
		
		if(commandString.equalsIgnoreCase("page")){
			int page = 1;
			if(args.length > 1)
				try{
					page = Integer.valueOf(args[1]);
				}catch(NumberFormatException e){
					sender.sendMessage(ChatColor.RED + "The Page-Number must be an Integer value.");
					return true;
				}
			PostPage.postPage(sender, page);
			return true;
		}
		
		if(commandString.equalsIgnoreCase("trait")){
			if(args.length > 1)
				outTraitHelp(sender, args[1]);
			else
				sender.sendMessage(ChatColor.RED + "The command: " + ChatColor.AQUA + "/racehelp trait <trait-Name>" + ChatColor.RED + " needs an trait-Name as argument!");
			return true;
		}
		
		PostPage.postPage(sender, commandString);	
		return true;
	}
	
	private void outTraitHelp(CommandSender sender, String trait){		
		Class<?> clazz = TraitsList.getClassOfTrait(trait);
		if(clazz == null){
			sender.sendMessage(ChatColor.RED + "Trait: " + ChatColor.LIGHT_PURPLE + trait + ChatColor.RED + " not found.");
			return;
		}
		
		try{
			sender.sendMessage(ChatColor.YELLOW + "===Trait: " + ChatColor.YELLOW + trait + ChatColor.YELLOW + "===");
			clazz.getMethod("pasteHelpForTrait", CommandSender.class).invoke(clazz, sender);
		}catch(Exception e){
			sender.sendMessage(ChatColor.RED + "Trait: " + ChatColor.LIGHT_PURPLE + trait + ChatColor.RED + " has no Help");
			return;
		}
		
		return;
	}

}
