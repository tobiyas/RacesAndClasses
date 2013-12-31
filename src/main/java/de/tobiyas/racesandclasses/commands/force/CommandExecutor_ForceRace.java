package de.tobiyas.racesandclasses.commands.force;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_ForceRace implements CommandExecutor {

	/**
	 * The plugin called stuff upon
	 */
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_ForceRace() {
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racforcerace").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racforcerace.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces()){
			sender.sendMessage(LanguageAPI.translateIgnoreError("something_disabled")
					.replace("value", "Races")
					.build());
			return true;
		}
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.forceChange)) return true;
		
		if(args.length < 2){
			sender.sendMessage(LanguageAPI.translateIgnoreError("wrong_command_use")
					.replace("command", "/racforcerace <player> <race name>").build());
			return true;
		}
		
		String playerToChange = args[0];
		String newRace = args[1];
		
		if(Bukkit.getPlayer(playerToChange) == null){
			sender.sendMessage(LanguageAPI.translateIgnoreError("player_not_exist")
					.replace("player", playerToChange).build());
			return true;
		}
		
		RaceManager raceManager = plugin.getRaceManager();
		if(raceManager.getHolderByName(newRace) == null){
			sender.sendMessage(LanguageAPI.translateIgnoreError("race_not_exist")
					.replace("race", newRace).build());
			return true;
		}
		
		if(raceManager.getHolderOfPlayer(playerToChange) == raceManager.getDefaultHolder()){
			raceManager.addPlayerToHolder(playerToChange, newRace, true);
		}else{
			raceManager.changePlayerHolder(playerToChange, newRace, true);
		}
		
		Player player = Bukkit.getPlayer(playerToChange);
		if(player.isOnline()){
			player.sendMessage(LanguageAPI.translateIgnoreError("race_changed_to")
					.replace("race", newRace).build());
		}
		
		player.sendMessage(LanguageAPI.translateIgnoreError("race_changed_to_other")
				.replace("race", newRace).replace("player", playerToChange).build());
		return true;
	}

}
