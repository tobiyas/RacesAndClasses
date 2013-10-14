package de.tobiyas.racesandclasses.commands.force;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_ForceRace implements CommandExecutor {

	/**
	 * The plugin called stuff upon
	 */
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_ForceRace() {
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racforcerace").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racforcerace.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.forceChange)) return true;
		
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "[RaC] Wrong usage. Use: " + ChatColor.LIGHT_PURPLE + "/racforcerace <player> <race name>");
			return true;
		}
		
		String playerToChange = args[0];
		String newRace = args[1];
		
		if(Bukkit.getPlayer(playerToChange) == null){
			sender.sendMessage(ChatColor.RED + "[RaC] Player: " + ChatColor.LIGHT_PURPLE + playerToChange + ChatColor.RED + " does not exist.");
			return true;
		}
		
		RaceManager raceManager = plugin.getRaceManager();
		if(raceManager.getHolderByName(newRace) == null){
			sender.sendMessage(ChatColor.RED + "[RaC] Race: " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.RED + " does not exist.");
			return true;
		}
		
		if(raceManager.getHolderOfPlayer(playerToChange) == raceManager.getDefaultHolder()){
			raceManager.addPlayerToHolder(playerToChange, newRace, true);
		}else{
			raceManager.changePlayerHolder(playerToChange, newRace, true);
		}
		
		Player player = Bukkit.getPlayer(playerToChange);
		if(player.isOnline()){
			player.sendMessage(ChatColor.GREEN + "[RaC] Your Race has been changed to: " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.GREEN + ".");
		}
		
		sender.sendMessage(ChatColor.GREEN + "[RaC] Race of " + ChatColor.LIGHT_PURPLE + playerToChange 
				+ ChatColor.GREEN + " changed to: " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.GREEN + ".");
		
		return true;
	}

}
