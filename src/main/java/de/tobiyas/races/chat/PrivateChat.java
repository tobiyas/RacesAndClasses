package de.tobiyas.races.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrivateChat {
	
	public static void sendMessageToPlayer(Player sender, Player reciever, String message){
		reciever.sendMessage(ChatColor.LIGHT_PURPLE + "[Whisper from " + ChatColor.YELLOW + 
				sender.getName() + ChatColor.LIGHT_PURPLE + "]: " + message);
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "[Whisper to " + ChatColor.YELLOW + 
				reciever.getName() + ChatColor.LIGHT_PURPLE + "]: " + message);
	}
}
