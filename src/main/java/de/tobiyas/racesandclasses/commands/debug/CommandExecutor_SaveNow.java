package de.tobiyas.racesandclasses.commands.debug;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.persistence.file.YAMLPeristanceSaver;

public class CommandExecutor_SaveNow implements CommandExecutor{

	private RacesAndClasses plugin;
	
	public CommandExecutor_SaveNow(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racsave").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racsave.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		boolean async = false;
		if(args.length > 0 && args[0].contains("a")){
			async = true;
		}
		
		long timeBefore = System.currentTimeMillis();
		sender.sendMessage(ChatColor.GREEN + "Flushing data to files NOW " 
				+ ChatColor.LIGHT_PURPLE + (async ? "a" : "") + "synchronusly" 
				+ ChatColor.GREEN + ".");
		YAMLPeristanceSaver.flushNow(async, true);
		
		if(!async){
			long timeTaken = System.currentTimeMillis() - timeBefore;
			sender.sendMessage(ChatColor.GREEN + "Flushing done. Taken " + ChatColor.LIGHT_PURPLE + timeTaken
					+ ChatColor.GREEN + "ms.");
		}
		
		return true;
	}

	
}
