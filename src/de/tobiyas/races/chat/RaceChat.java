package de.tobiyas.races.chat;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.util.consts.PermissionNode;

public class RaceChat {

	public static void sendRaceMessage(Player player, String message){
		Races plugin = Races.getPlugin();
		
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		if(container == null){
			player.sendMessage(ChatColor.RED + "You have no Race selected yet.");
			return;
		}
		
		LinkedList<String> members = RaceManager.getManager().getAllPlayerOfRace(container);
		
		String[] permCheck = {PermissionNode.raceChatBasic, PermissionNode.raceChatRead};
		
		for(String member : members){
			Player memberPlayer = Bukkit.getPlayer(member);
			if(memberPlayer != null && plugin.getPermissionManager().hasAnyPermissionSilent(player, permCheck))
				memberPlayer.sendMessage(ChatColor.AQUA + "[RaceChat] " + ChatColor.RED + player.getName() + ChatColor.WHITE + ": " + message);
			
		}
	}
	
	public static String modifyMessageToPlayer(String player, String message){
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player);
		if(container == null){
			return message;
		}
		return container.getTag() + player + ": " + message;
	}
}
