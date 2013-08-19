package de.tobiyas.racesandclasses.commands.health;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_RaceHeal implements CommandExecutor {
	
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_RaceHeal(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("raceheal").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /raceheal.");
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
	
		if(args.length == 0){
			healSelf(sender);
			return true;
		}
		
		if(args.length == 1){
			String otherName = args[0];
			healOther(sender, otherName);
			return true;
		}
		
		sender.sendMessage(ChatColor.RED + "Wrong usage. Use: /raceheal [PlayerName]");
		return true;
	}

	
	/**
	 * The Player tries to heal himself.
	 * This only workes for Players using this command,
	 * with Permissions.
	 * 
	 * @param sender that tries to heal himself.
	 */
	private void healSelf(CommandSender sender){
		if(plugin.getPermissionManager().checkPermissions(sender, PermissionNode.healSelf)){
			if(sender instanceof Player){
				Player player = (Player) sender;
				
				double maxHealth = CompatibilityModifier.BukkitPlayer.safeGetMaxHealth(player);
				CompatibilityModifier.BukkitPlayer.safeSetHealth(maxHealth, player);
				player.sendMessage(ChatColor.GREEN + "You have been healed.");
			}else sender.sendMessage(ChatColor.RED + "You have to be a Player to use this command.");
		}
	}
	
	
	/**
	 * The Player tries to heal someone else.
	 * This only works with Permissions and if other
	 * Player is existent and online.
	 * 
	 * @param sender that tries to heal himself.
	 */
	private void healOther(CommandSender sender, String otherName){
		if(plugin.getPermissionManager().checkPermissions(sender, PermissionNode.healOther)){
			Player other = Bukkit.getPlayer(otherName);
			if(other != null && other.isOnline()){
				Player player = (Player) sender;
				double maxHealth = CompatibilityModifier.BukkitPlayer.safeGetMaxHealth(player);
				CompatibilityModifier.BukkitPlayer.safeSetHealth(maxHealth, player);
				
				other.sendMessage(ChatColor.GREEN + "You have been healed from: " + ChatColor.LIGHT_PURPLE + player.getName());
				player.sendMessage(ChatColor.GREEN + "You have healed: " + ChatColor.LIGHT_PURPLE +  other.getName());
			}else{
				sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.LIGHT_PURPLE + otherName + ChatColor.RED + " could not be found.");
			}
		}
	}
}
