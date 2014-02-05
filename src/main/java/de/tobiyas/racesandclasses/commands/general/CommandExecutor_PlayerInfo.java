package de.tobiyas.racesandclasses.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public class CommandExecutor_PlayerInfo implements CommandExecutor {

private RacesAndClasses plugin;
	
	public CommandExecutor_PlayerInfo(){
		plugin = RacesAndClasses.getPlugin();

		String command = "playerinfo";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {

		//TODO think about getting back in
		//if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceInfo)) return true;
		
		Player player = null;
		if(args.length == 0){
			if(sender instanceof Player){
				player = (Player) sender;
			}else{
				sender.sendMessage(ChatColor.RED + LanguageAPI.translateIgnoreError("needs_1_arg")
						.replace("command", "/playerinfo <playername>")
						.build());
				return true;
			}
				
		}else{
			player = Bukkit.getPlayer(args[0]);
		}
			
		if(player == null){
			sender.sendMessage(ChatColor.RED + LanguageAPI.translateIgnoreError("player_not_exist")
					.replace("player", args[0])
					.build());
			return true;
		}
		
		AbstractTraitHolder raceContainer = plugin.getRaceManager().getHolderOfPlayer(player.getName());
		AbstractTraitHolder classContainer = plugin.getClassManager().getHolderOfPlayer(player.getName());
		String className = "None";
		String raceName = "None";
		if(classContainer != null){
			className = classContainer.getName();
		}
		
		if(raceContainer != null){
			raceName = raceContainer.getName();
		}
		
		
		sender.sendMessage(ChatColor.YELLOW + "=====" + ChatColor.RED + "PLAYER: " + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + "=====");
		sender.sendMessage(ChatColor.YELLOW + "Race: " + ChatColor.RED + raceName);
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
