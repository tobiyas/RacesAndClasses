package de.tobiyas.races.commands.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		
		if(args.length == 0){
			PostPage.postPage(sender, 1);
			return true;
		}
		
		String commandString = args[0];
		
		if(commandString.equalsIgnoreCase("testerror")){ //Produces Error! Only for testing!
			if(sender instanceof Player && !((Player)sender).getName().equals("tobiyas")){
				sender.sendMessage(ChatColor.RED + "You may not use this command!");
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "Error is beeing fired!");
			try{
				throw new NullPointerException("useless generated exeption.");
			}catch(NullPointerException e){
				plugin.getDebugLogger().logStackTrace(e);
				sender.sendMessage(ChatColor.GREEN + "Worked!");
				return true;
			}
		}
		
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
		
		if(args.length == 2 && args[0].equalsIgnoreCase("trait")){
			outTraitHelp(sender, args[1]);
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
