package de.tobiyas.races.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;

public class CommandExecutor_RaceList implements CommandExecutor {
	
	private Races plugin;
	
	public CommandExecutor_RaceList(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("racelist").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racelist.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		ArrayList<String> races = RaceManager.getManager().listAllRaces();
		RaceContainer playerRace = RaceManager.getManager().getRaceOfPlayer(sender.getName());
		
		sender.sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");
		
		for(String race : races)
			if(playerRace != null && race.equals(playerRace.getName()))
				sender.sendMessage(ChatColor.RED + race + ChatColor.YELLOW + "  <-- Your race");
			else	
				sender.sendMessage(ChatColor.BLUE + race);
	
		return true;
	}

}
