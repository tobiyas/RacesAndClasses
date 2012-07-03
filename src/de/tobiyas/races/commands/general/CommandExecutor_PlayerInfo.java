package de.tobiyas.races.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;

public class CommandExecutor_PlayerInfo implements CommandExecutor {

private Races plugin;
	
	public CommandExecutor_PlayerInfo(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("playerinfo").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /playerinfo.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {

		//TODO think about getting back in
		//if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceInfo)) return true;
		
		Player player = null;
		if(args.length == 0){
			if(sender instanceof Player)
				player = (Player) sender;
			else{
				sender.sendMessage(ChatColor.RED + "Expected a Playername as argument.");
				return true;
			}
				
		}else
			player = Bukkit.getPlayer(args[0]);
		
		if(player == null){
			sender.sendMessage(ChatColor.RED + "The provided Player could not be found.");
			return true;
		}
		
		RaceContainer raceContainer = RaceManager.getManager().getRaceOfPlayer(player.getName());
		ClassContainer classContainer = ClassManager.getInstance().getClassOfPlayer(player.getName());
		String className = "None";
		if(classContainer != null)
			className = classContainer.getName();
		
		
		sender.sendMessage(ChatColor.YELLOW + "=====" + ChatColor.RED + "PLAYER: " + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + "=====");
		sender.sendMessage(ChatColor.YELLOW + "Race: " + ChatColor.RED + raceContainer.getName());
		sender.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.RED + className);
		sender.sendMessage(ChatColor.YELLOW + "---L--O--C--A--T--I--O--N---");
		sender.sendMessage(ChatColor.YELLOW + "World: " + ChatColor.AQUA + player.getWorld().getName());
		
		Location loc = player.getLocation();
		sender.sendMessage(ChatColor.YELLOW + "Position - X:" + ChatColor.AQUA + loc.getBlockX() + ChatColor.YELLOW + " Y:" + 
							ChatColor.AQUA + loc.getBlockY() + ChatColor.YELLOW + " Z:" + ChatColor.AQUA + loc.getBlockZ());
		
		sender.sendMessage(ChatColor.YELLOW + "---O--T--H--E--R---");
		sender.sendMessage(ChatColor.YELLOW + "Item in Hand: " + ChatColor.AQUA + player.getItemInHand().getType().toString());
		return true;
	}

}
