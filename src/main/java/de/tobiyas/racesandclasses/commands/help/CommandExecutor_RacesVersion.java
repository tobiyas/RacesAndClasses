package de.tobiyas.racesandclasses.commands.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class CommandExecutor_RacesVersion implements CommandExecutor{
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_RacesVersion(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racesversion").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racesversion.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		sender.sendMessage(ChatColor.YELLOW + "The Current Version of Races: " + ChatColor.RED + plugin.getDescription().getVersion());
		
		if(Consts.currentDevStage.contains("A")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.RED + "ALPHA" + ChatColor.YELLOW + 
								" stage. Not all Featurs are yet implemented.");
		}
		
			
		if(Consts.currentDevStage.contains("B")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.DARK_PURPLE + "BETA" + ChatColor.YELLOW + 
								" stage. Main Featurs are all running. Bugs are possible");
		}
			
		if(Consts.currentDevStage.contains("R")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.GREEN + "RELEASE" + ChatColor.YELLOW + 
								" stage. All Features should work without Errors.");
		}
		
			
		if(Consts.currentDevStage.contains("D")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.AQUA + "DEVELOPEMENT" + 
								ChatColor.YELLOW + " stage. Some Features are not completed and will be completed soon.");
		}
			
		if(Consts.currentDevStage.contains("E")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.LIGHT_PURPLE + "EXPERIMENTAL" + ChatColor.YELLOW + 
								" stage. Changes can be rejected and will not be stable.");
		}
			
		if(Consts.currentDevStage.contains("S")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.GREEN + "STABLE" + ChatColor.YELLOW + 
								" stage. All Features should work without Errors.");
		}
		
		if(Consts.currentDevStage.contains("T")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.YELLOW + "TESTING" + ChatColor.YELLOW + 
					" stage. All features are there. Bugs will probably occure. No Garantee.");
		}
		
		sender.sendMessage(ChatColor.YELLOW + "This Plugin is designed and implemented by: " + ChatColor.LIGHT_PURPLE + "Tobiyas" 
				+ ChatColor.YELLOW + ".");
			
		return true;
	}

}
