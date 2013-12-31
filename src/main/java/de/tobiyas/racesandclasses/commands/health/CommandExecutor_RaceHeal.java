package de.tobiyas.racesandclasses.commands.health;

import static de.tobiyas.racesandclasses.translation.languages.Keys.healed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.healed_by;
import static de.tobiyas.racesandclasses.translation.languages.Keys.healed_other;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
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
		
		sender.sendMessage(LanguageAPI.translateIgnoreError(wrong_command_use)
				.replace("command", "/raceheal [PlayerName]")
				.build());
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
				
				plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getManaManager().fillMana(10000);
				
				sender.sendMessage(LanguageAPI.translateIgnoreError(healed)
						.build());
			}else{
				sender.sendMessage(LanguageAPI.translateIgnoreError(only_players)
						.build());
			}
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
				
				plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getManaManager().fillMana(10000);
				
				sender.sendMessage(LanguageAPI.translateIgnoreError(healed_other)
						.replace("player", other.getName())
						.build());
				
				other.sendMessage(LanguageAPI.translateIgnoreError(healed_by)
						.replace("player", player.getName())
						.build());
			}else{
				sender.sendMessage(LanguageAPI.translateIgnoreError(player_not_exist)
						.replace("player", otherName)
						.build());
			}
		}
	}
}
