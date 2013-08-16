package de.tobiyas.racesandclasses.commands.health;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.traitgui.TraitInventory;

public class CommandExecutor_ShowTraits implements CommandExecutor {

	private RacesAndClasses plugin;
	
	
	public CommandExecutor_ShowTraits() {
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("showtraits").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /showtraits.");
		}
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "[RaC] Only players can use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		Player playerToSearch = player;
		
		if(args.length > 0){
			String playerName = args[0];
			playerToSearch = Bukkit.getPlayer(playerName);
			if(playerToSearch == null){
				sender.sendMessage(ChatColor.RED + "[Rac]" + " Could not find player: " + ChatColor.LIGHT_PURPLE + playerName + ChatColor.RED + ".");
				return true;
			}
		}
		
		TraitInventory inventory = new TraitInventory(playerToSearch);
		player.openInventory(inventory);
		
		player.sendMessage(ChatColor.GREEN + "[RaC] Opening Traits of " + ChatColor.LIGHT_PURPLE + playerToSearch.getName() 
				+ ChatColor.GREEN + ".");
		return true;
	}

}
