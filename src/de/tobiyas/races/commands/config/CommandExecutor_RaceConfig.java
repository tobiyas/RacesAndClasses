package de.tobiyas.races.commands.config;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.member.MemberConfig;
import de.tobiyas.races.configuration.member.MemberConfigManager;

public class CommandExecutor_RaceConfig implements CommandExecutor {

	private Races plugin;
	
	public CommandExecutor_RaceConfig(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("raceconfig").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /raceconfig.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only Players can use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0){
			player.sendMessage(ChatColor.YELLOW + "=======YOUR CONFIG=======");
			MemberConfig config = MemberConfigManager.getInstance().getConfigOfPlayer(player.getName());
			if(config == null){
				player.sendMessage(ChatColor.RED + "Your config could not be found. Try relogging or contact an Admin.");
				return true;
			}
			
			HashMap<String, Object> configLines = config.getCurrentConfig();
			
			for(String attribute : configLines.keySet()){
				String value = String.valueOf(configLines.get(attribute));
				player.sendMessage(ChatColor.LIGHT_PURPLE + attribute + ChatColor.BLUE + value);
			}
			
			return true;
		}
		
		if(args.length == 2){
			String attribute = args[0];
			String value = args[1];
			
			MemberConfig config = MemberConfigManager.getInstance().getConfigOfPlayer(player.getName());
			if(config == null){
				player.sendMessage(ChatColor.RED + "Your config could not be found. Try relogging or contact an Admin.");
				return true;
			}
			
			boolean worked = config.changeAttribute(attribute, value);
			if(worked)
				player.sendMessage(ChatColor.GREEN + "The Attribute " + ChatColor.LIGHT_PURPLE + attribute + ChatColor.GREEN +
									" has been changed to: " + ChatColor.LIGHT_PURPLE + value);
			else
				player.sendMessage(ChatColor.GREEN + "The Attribute " + ChatColor.LIGHT_PURPLE + attribute + ChatColor.GREEN +
						" could not be found.");
	
			
			return true;
		}
		
		
		player.sendMessage(ChatColor.RED + "Wrong usage. Use: /raceconfig <attribute> <value>");
		return true;
	}

}
