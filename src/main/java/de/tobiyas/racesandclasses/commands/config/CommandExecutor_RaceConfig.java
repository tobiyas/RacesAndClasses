package de.tobiyas.racesandclasses.commands.config;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;

public class CommandExecutor_RaceConfig implements CommandExecutor {

	private RacesAndClasses plugin;
	
	public CommandExecutor_RaceConfig(){
		plugin = RacesAndClasses.getPlugin();
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
			MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
			if(config == null){
				player.sendMessage(ChatColor.RED + "Your config could not be found. Try relogging or contact an Admin.");
				return true;
			}
			
			Map<String, Object> configLines = config.getCurrentConfig(false);
			
			for(String attribute : configLines.keySet()){
				String value = String.valueOf(configLines.get(attribute));
				player.sendMessage(ChatColor.LIGHT_PURPLE + attribute + ChatColor.AQUA + ": " + ChatColor.BLUE + value);
			}
			
			return true;
		}
		
		if(args.length == 2){
			String attribute = args[0];
			String value = args[1];
			
			MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
			if(config == null){
				player.sendMessage(ChatColor.RED + "Your config could not be found. Try relogging or contact an Admin.");
				return true;
			}
			
			boolean worked = config.changeAttribute(attribute, value);
			if(worked){
				player.sendMessage(ChatColor.GREEN + "The Attribute " + ChatColor.LIGHT_PURPLE + attribute + ChatColor.GREEN +
									" has been changed to: " + ChatColor.LIGHT_PURPLE + value);
			}else{
				player.sendMessage(ChatColor.RED + "The Attribute " + ChatColor.LIGHT_PURPLE + attribute + ChatColor.RED +
						" could not be found.");
			}
			
			return true;
		}
		
		
		player.sendMessage(ChatColor.RED + "Wrong usage. Use:" + ChatColor.LIGHT_PURPLE + " /raceconfig <attribute> <value>  or");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "/raceconfig <attribute> <value>  to list yout config");
		return true;
	}

}
