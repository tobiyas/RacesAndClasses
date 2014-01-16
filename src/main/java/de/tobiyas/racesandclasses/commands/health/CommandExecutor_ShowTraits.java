package de.tobiyas.racesandclasses.commands.health;

import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_traits;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
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
			LanguageAPI.sendTranslatedMessage(sender, only_players);
			return true;
		}
		
		Player player = (Player) sender;
		Player playerToSearch = player;
		
		if(args.length > 0){
			String playerName = args[0];
			playerToSearch = Bukkit.getPlayer(playerName);
			if(playerToSearch == null){
				LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
						"player", playerName);
				return true;
			}
		}
		
		TraitInventory inventory = new TraitInventory(playerToSearch);
		player.openInventory(inventory);
		
		LanguageAPI.sendTranslatedMessage(sender, open_traits,
				"player", playerToSearch.getName());
		return true;
	}

}
