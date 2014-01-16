package de.tobiyas.racesandclasses.commands.force;

import static de.tobiyas.racesandclasses.translation.languages.Keys.class_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.class_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_ForceClass implements CommandExecutor {

	/**
	 * The plugin called stuff upon
	 */
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_ForceClass() {
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racforceclass").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racforceclass.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if(!plugin.getConfigManager().getGeneralConfig().isConfig_classes_enable()){
			LanguageAPI.sendTranslatedMessage(sender, "something_disabled",
					"value", "Class");
			return true;
		}
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.forceChange)) return true;
		
		if(args.length < 2){
			LanguageAPI.sendTranslatedMessage(sender, wrong_command_use,
					"command", "/racforceclass <player> <class name>");
			return true;
		}
		
		String playerToChange = args[0];
		String newClass = args[1];
		
		if(Bukkit.getPlayer(playerToChange) == null){
			LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
					"player", playerToChange);
			return true;
		}
		
		ClassManager classManager = plugin.getClassManager();
		if(classManager.getHolderByName(newClass) == null){
			LanguageAPI.sendTranslatedMessage(sender, class_not_exist,
					"class", newClass);
			return true;
		}
		
		if(classManager.getHolderOfPlayer(playerToChange) == classManager.getDefaultHolder()){
			classManager.addPlayerToHolder(playerToChange, newClass, true);
		}else{
			classManager.changePlayerHolder(playerToChange, newClass, true);
		}
		
		Player player = Bukkit.getPlayer(playerToChange);
		if(player.isOnline()){
			LanguageAPI.sendTranslatedMessage(player, class_changed_to,
					"class", newClass);
		}
		
		LanguageAPI.sendTranslatedMessage(sender, "class_changed_to_other",
				"player", player.getName(), "class", newClass);
		
		return true;
	}

}
