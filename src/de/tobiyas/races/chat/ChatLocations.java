package de.tobiyas.races.chat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;


public class ChatLocations {

	public static void chatGlobal(String playerString, String msg, RaceContainer container){
		sendMessageTo(Bukkit.getOnlinePlayers(), playerString, msg, container);
	}
	
	public static void chatWorld(String playerString, String msg, RaceContainer container, World world){
		sendMessageTo((Player[])world.getPlayers().toArray(), playerString, msg, container);
	}
	
	public static void localChat(String playerString, String msg, RaceContainer container, Player player, int range){
		List<Entity> entities = player.getNearbyEntities(range * 2, range * 2, range * 2);
		Set<Player> players = new HashSet<Player>();
		players.add(player);
		for(Entity entity : entities){
			if(entity instanceof Player)
				players.add((Player) entity);
		}
		
		sendMessageTo((Player[])players.toArray(), playerString, msg, container);	
	}
	
	private static void sendMessageTo(Player[] players, String playerString, String msg, RaceContainer container){
		for(Player player : players){
			if(RaceManager.getManager().getRaceOfPlayer(player.getName()) == container){
				player.sendMessage(playerString + msg);
			}else{
				if(Races.getPlugin().interactConfig().getconfig_chatEncrypt())
					player.sendMessage(playerString + LanguageEncrypter.encryptToLanguage(msg));
				else
					player.sendMessage(playerString + msg);
			}
		}
	}
}
