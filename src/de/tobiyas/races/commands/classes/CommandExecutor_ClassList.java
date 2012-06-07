package de.tobiyas.races.commands.classes;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;

public class CommandExecutor_ClassList implements CommandExecutor {
	
	private Races plugin;
	
	public CommandExecutor_ClassList(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("classlist").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /classlist.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		sender.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "Classes" + ChatColor.YELLOW + " =====");
		
		LinkedList<String> classes = ClassManager.getInstance().getClassNames();
		if(classes.size() == 0){
			sender.sendMessage(ChatColor.RED + "No Classes in the list.");
			return true;
		}
		
		for(String classe : classes )
			sender.sendMessage(ChatColor.BLUE + classe);
		
		return true;
	}

}
