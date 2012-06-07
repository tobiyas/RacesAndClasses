package de.tobiyas.races.commands.classes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.util.consts.PermissionNode;

public class CommandExecutor_Class implements CommandExecutor {
	
	private Races plugin;
	
	public CommandExecutor_Class(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("class").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /class.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this command");
			return true;
		}
			
		if(args.length != 2){
			sender.sendMessage(ChatColor.RED + "Wrong Usage. Use /racehelp if you need help.");
			return true;
		}
			
		Player player = (Player) sender;
		String potentialClass = args[1];
			
		//Select class(only if has no race)
		if(args[0].equalsIgnoreCase("select")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.selectClass)) return true;
			ClassContainer container = ClassManager.getInstance().getClassOfPlayer(player.getName());
			if(container == null)
				if(ClassManager.getInstance().addPlayerToClass(player.getName(), potentialClass))
					player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialClass);
				else
					player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
			else
				player.sendMessage(ChatColor.RED + "You already have a class: " + ChatColor.LIGHT_PURPLE + container.getName());
			
			return true;
		}
			
		//Change races (only if has already a race)
		if(args[0].equalsIgnoreCase("change")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeClass)) return true;
			ClassContainer container = ClassManager.getInstance().getClassOfPlayer(player.getName());
				
			if(container != null){
				if(potentialClass.equalsIgnoreCase(container.getName())){
					player.sendMessage(ChatColor.RED + "You are already a " + ChatColor.LIGHT_PURPLE + container.getName());
					return true;
				}
					
				if(ClassManager.getInstance().changePlayerClass(player.getName(), potentialClass))
					player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialClass);
				else
					player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
			}else
				player.sendMessage(ChatColor.RED + "You have no class you could change");
				
				
			return true;
		}
			
		player.sendMessage(ChatColor.RED + "Wrong arguments. Use /racehelp if you need help");
		return true;
	}

}
