package de.tobiyas.racesandclasses.commands.force;

import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to_other;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.something_disabled;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;

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
			LanguageAPI.sendTranslatedMessage(sender, something_disabled,
					"value", "Races");
			return true;
		}
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.forceChange)) return true;
		
		if(args.length < 2){
			LanguageAPI.sendTranslatedMessage(sender, wrong_command_use,
					"command", "/racforcerace <player> <race name>");
			return true;
		}
		
		String playerToChange = args[0];
		String newRace = args[1];
		
		if(Bukkit.getPlayer(playerToChange) == null){
			LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
					"player", playerToChange);
			return true;
		}
		
		RaceManager raceManager = plugin.getRaceManager();
		if(raceManager.getHolderByName(newRace) == null){
			LanguageAPI.sendTranslatedMessage(sender, race_not_exist,
					"race", newRace);
			return true;
		}
		
		if(raceManager.getHolderOfPlayer(playerToChange) == raceManager.getDefaultHolder()){
			raceManager.addPlayerToHolder(playerToChange, newRace, true);
		}else{
			raceManager.changePlayerHolder(playerToChange, newRace, true);
		}
		
		Player player = Bukkit.getPlayer(playerToChange);
		if(player.isOnline()){
			LanguageAPI.sendTranslatedMessage(player, race_changed_to,
					"race", newRace);
		}
		
		LanguageAPI.sendTranslatedMessage(sender, race_changed_to_other,
				"race", newRace, "player", playerToChange);
		return true;
	}

}
