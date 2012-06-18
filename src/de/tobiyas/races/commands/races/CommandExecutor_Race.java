/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races.commands.races;


import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.chat.channels.ChannelManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.util.consts.PermissionNode;


public class CommandExecutor_Race implements CommandExecutor {
	private Races plugin;

	public CommandExecutor_Race(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("race").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /race.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this command");
			return true;
		}
		
		Player player = (Player) sender;
		if(args.length == 0){
			postHelp(player, false);
			return true;
		}
		
		String raceCommand = args[0];
			
		//Select race(only if has no race)
		if(raceCommand.equalsIgnoreCase("select")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.selectRace)) return true;
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "This command needs 1 argument: /race select <racename>");
				return true;
			}
			
			String potentialRace = args[1];
			selectRace(player, potentialRace);
			return true;
		}
			
		//Change races (only if has already a race)
		if(raceCommand.equalsIgnoreCase("change")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeRace)) return true;
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "This command needs 1 argument: /race change <racename>");
				return true;
			}
			
			String potentialRace = args[1];
			changeRace(player, potentialRace);
			return true;
		}
		
		if(raceCommand.equalsIgnoreCase("info")){
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceInfo)) return true;
			raceInfo(player);
			return true;
		}
		
		if(raceCommand.equalsIgnoreCase("list")){
			raceList(player);
			return true;
		}
			
		postHelp(player, true);
		return true;
	}
	
	private void postHelp(Player player, boolean wrongUsage){
		if(wrongUsage)
			player.sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		else
			player.sendMessage(ChatColor.RED + "Use one of the following commands:");
		
		player.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "info");
		player.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "list");
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.changeRace))
			player.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<racename>");
		
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.selectRace))
			player.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<racename>");
	}
	
	private void selectRace(Player player, String newRace){
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		RaceContainer stdContainer = RaceManager.getManager().getDefaultContainer();
		if(container == null || container == stdContainer){
			if(RaceManager.getManager().getRaceByName(newRace) != null && RaceManager.getManager().getRaceByName(newRace) == stdContainer){
				player.sendMessage(ChatColor.RED + "You can't select the default race.");
				return;
			}
				
			if(RaceManager.getManager().addPlayerToRace(player.getName(), newRace)){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + newRace);
				if(plugin.interactConfig().getconfig_channels_enable())
					ChannelManager.GetInstance().playerChangeRace("", player);
			}else
				player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.RED + " was not found.");
		}else
			player.sendMessage(ChatColor.RED + "You already have a race: " + ChatColor.LIGHT_PURPLE + container.getName());
	}
	
	private void changeRace(Player player, String newRace){
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		RaceContainer stdContainer = RaceManager.getManager().getDefaultContainer();
		
		if(container != null && container != RaceManager.getManager().getDefaultContainer()){
			String oldRace = container.getName();
			if(newRace.equalsIgnoreCase(container.getName())){
				player.sendMessage(ChatColor.RED + "You are already a " + ChatColor.LIGHT_PURPLE + container.getName());
				return;
			}
			
			if(RaceManager.getManager().getRaceByName(newRace) != null && RaceManager.getManager().getRaceByName(newRace) == stdContainer){
				player.sendMessage(ChatColor.RED + "You can't select the default race.");
				return;
			}
				
			if(RaceManager.getManager().changePlayerRace(player.getName(), newRace)){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + newRace);
				if(plugin.interactConfig().getconfig_channels_enable())
					ChannelManager.GetInstance().playerChangeRace(oldRace, player);
			}else
				player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.RED + " was not found.");
		}else
			player.sendMessage(ChatColor.RED + "You have no Race you could change");
	}
	
	private void raceInfo(Player player){
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		
		player.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO" + ChatColor.YELLOW + "=========");
		if(container == null){
			player.sendMessage(ChatColor.YELLOW + "You have no race selected.");
			return;
		}
		
		player.sendMessage(ChatColor.YELLOW + "Your race: " + ChatColor.LIGHT_PURPLE + container.getName());
		player.sendMessage(ChatColor.YELLOW + "Your race tag: " + ChatColor.LIGHT_PURPLE + container.getTag());
		player.sendMessage(ChatColor.YELLOW + "Allowed armor: " + ChatColor.LIGHT_PURPLE + container.getArmorString());
		
		player.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "Traits" + ChatColor.YELLOW + "=========");
		
		for(Trait trait : container.getVisibleTraits()){
			player.sendMessage(ChatColor.BLUE + trait.getName() + " : " + trait.getValueString());
		}
	}
	
	private void raceList(Player player){
		ArrayList<String> races = RaceManager.getManager().listAllRaces();
		RaceContainer playerRace = RaceManager.getManager().getRaceOfPlayer(player.getName());
		
		player.sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");
		
		for(String race : races)
			if(playerRace != null && race.equals(playerRace.getName()))
				player.sendMessage(ChatColor.RED + race + ChatColor.YELLOW + "  <-- Your race");
			else	
				player.sendMessage(ChatColor.BLUE + race);
	}
}
