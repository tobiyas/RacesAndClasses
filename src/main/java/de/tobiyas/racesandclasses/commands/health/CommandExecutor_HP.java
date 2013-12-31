package de.tobiyas.racesandclasses.commands.health;

import static de.tobiyas.racesandclasses.translation.languages.Keys.no_healthcontainer_found;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;

public class CommandExecutor_HP implements CommandExecutor {

	private RacesAndClasses plugin;
	
	public CommandExecutor_HP(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("playerhealth").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /playerhealth.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(LanguageAPI.translateIgnoreError(only_players)
					.build());
			return true;
		}
		
		Player player = (Player) sender;
		if(!plugin.getPlayerManager().displayHealth(player.getName())){
			sender.sendMessage(LanguageAPI.translateIgnoreError(no_healthcontainer_found)
					.build());
		}
		
		plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getManaManager().outputManaToPlayer();
		plugin.getPlayerManager().getPlayerLevelManager(player.getName()).forceDisplay();
		return true;
	}

}
