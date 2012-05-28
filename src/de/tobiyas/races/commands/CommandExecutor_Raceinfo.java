/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.health.HealthManager;
import de.tobiyas.races.util.consts.PermissionNode;


public class CommandExecutor_Raceinfo implements CommandExecutor {
	private Races plugin;

	public CommandExecutor_Raceinfo(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("raceinfo").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /raceinfo.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (command.getName().equalsIgnoreCase("raceinfo")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You have to be a player to use this command");
				return true;
			}

			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceInfo)) return true;
			Player player = (Player) sender;
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
			
			player.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO" + ChatColor.YELLOW + "=========");
			if(container == null){
				player.sendMessage(ChatColor.YELLOW + "You have no race selected.");
				return true;
			}
			
			double currentHealth = HealthManager.getHealthManager().getHealthOfPlayer(player.getName());
			if(currentHealth != -1){
				player.sendMessage(ChatColor.YELLOW + "Health: " + ChatColor.RED + currentHealth + "/" + container.getRaceMaxHealth());
			}
			
			player.sendMessage(ChatColor.YELLOW + "Your race: " + ChatColor.LIGHT_PURPLE + container.getName());
			player.sendMessage(ChatColor.YELLOW + "Your race tag: " + ChatColor.LIGHT_PURPLE + container.getTag());
			
			player.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "Traits" + ChatColor.YELLOW + "=========");
			
			for(Trait trait : container.getTraits()){
				player.sendMessage(ChatColor.BLUE + trait.getName() + " : " + trait.getValueString());
			}
			
			return true;
		}
		return false;
	}
}
