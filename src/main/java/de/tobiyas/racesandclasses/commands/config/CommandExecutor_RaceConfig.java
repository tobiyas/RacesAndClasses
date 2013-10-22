package de.tobiyas.racesandclasses.commands.config;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
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
			sender.sendMessage(LanguageAPI.translateIgnoreError("only_players").build());
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0){
			String your = LanguageAPI.translateIgnoreError("your").build();
			player.sendMessage(ChatColor.YELLOW + "=======" + your.toUpperCase() + " CONFIG=======");
			MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getName());
			if(config == null){
				player.sendMessage(LanguageAPI.translateIgnoreError("member_config_not_found").build());
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
				player.sendMessage(LanguageAPI.translateIgnoreError("member_config_not_found").build());
				return true;
			}
			
			boolean worked = config.changeAttribute(attribute, value);
			if(worked){
				player.sendMessage(LanguageAPI.translateIgnoreError("member_config_changed")
						.replace("attribute", attribute).replace("value", value).build());
			}else{
				player.sendMessage(LanguageAPI.translateIgnoreError("member_config_attribute_not_found")
						.replace("attribute", attribute).build());
			}
			
			return true;
		}
		
		
		player.sendMessage(LanguageAPI.translateIgnoreError("wrong_command_use")
				.replace("command", "/raceconfig <attribute> <value>").build());
		return true;
	}

}
