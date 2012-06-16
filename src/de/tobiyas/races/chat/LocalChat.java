package de.tobiyas.races.chat;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.util.chat.ChannelLevel;

public class LocalChat {
	
	public static void sendMessageLocaly(Player player, String message, int distance){
		HashSet<Player> players = new HashSet<Player>();
		String localColor = Races.getPlugin().interactConfig().getConfig_localchat_default_color();
		String localFormat = Races.getPlugin().interactConfig().getConfig_localchat_default_format();
		
		ChatFormatter chatFormatter = new ChatFormatter("Local", localColor, ChannelLevel.LocalChannel, localFormat);
		message = chatFormatter.format(player.getName(), message);
		
		Location loc = player.getLocation();
		for(Player tempPlayer : loc.getWorld().getPlayers())
			if(loc.distance(tempPlayer.getLocation()) < distance)
				players.add(tempPlayer);
		
		for(Player reciever : players)
			reciever.sendMessage(message);
	}

}
