/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races.commands.races;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.util.consts.PermissionNode;


public class CommandExecutor_Race implements CommandExecutor {
	private Races plugin;

	public CommandExecutor_Race(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("race").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /race.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this command");
			return true;
		}
			
		if(args.length != 2){
			sender.sendMessage(ChatColor.RED + "Wrong Usage. Use /racehelp if you need help.");
			return true;
		}
			
		Player player = (Player) sender;
		String potentialRace = args[1];
			
		//Select race(only if has no race)
		if(args[0].equalsIgnoreCase("select")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.selectRace)) return true;
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
			if(container == null)
				if(RaceManager.getManager().addPlayerToRace(player.getName(), potentialRace))
					player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialRace);
				else
					player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + potentialRace + ChatColor.RED + " was not found.");
			else
				player.sendMessage(ChatColor.RED + "You already have a race: " + ChatColor.LIGHT_PURPLE + container.getName());
			
			return true;
		}
			
		//Change races (only if has already a race)
		if(args[0].equalsIgnoreCase("change")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeRace)) return true;
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
				
			if(container != null){
				if(potentialRace.equalsIgnoreCase(container.getName())){
					player.sendMessage(ChatColor.RED + "You are already a " + ChatColor.LIGHT_PURPLE + container.getName());
					return true;
				}
					
				if(RaceManager.getManager().changePlayerRace(player.getName(), potentialRace))
					player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialRace);
				else
					player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + potentialRace + ChatColor.RED + " was not found.");
			}else
				player.sendMessage(ChatColor.RED + "You have no Race you could change");
				
				
			return true;
		}
			
		player.sendMessage(ChatColor.RED + "Wrong arguments. Use /racehelp if you need help");
		return true;
	}
}
