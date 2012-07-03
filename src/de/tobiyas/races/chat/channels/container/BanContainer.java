package de.tobiyas.races.chat.channels.container;

import java.util.HashMap;
import java.util.Set;

import de.tobiyas.util.config.YAMLConfigExtended;;

public class BanContainer {
	
	private HashMap<String, Integer> banned = new HashMap<String, Integer>();

	public BanContainer(){
	}
	
	public BanContainer(YAMLConfigExtended config, String channelPre){
		Set<String> bannedPlayers = config.getYAMLChildren(channelPre + ".banned");
		for(String playerName : bannedPlayers){
			int time = config.getInt(channelPre + ".banned." + playerName);
			banned.put(playerName, time);
		}
	}
	
	public void banPlayer(String playerName, int time){
		if(!banned.containsKey(playerName))
			banned.put(playerName, time);
	}
	
	public void unbanPlayer(String playerName){
		for(String name : banned.keySet())
			if(playerName.equalsIgnoreCase(name))
				banned.remove(name);
	}
	
	public void saveContainer(YAMLConfigExtended config, String channelPre){
		for(String name : banned.keySet()){
			int time = banned.get(name);
			config.set(channelPre + ".banned." + name, time);
		}
	}
	
	public int isBanned(String playerName){
		for(String name : banned.keySet()){
			if(playerName.equalsIgnoreCase(name))
				return banned.get(name);
		}
		
		return -1;
	}
	
	
	
	public void tick(){
		for(String name : banned.keySet()){
			int duration = banned.get(name);
			if(duration == Integer.MAX_VALUE) continue;
			duration --;
			if(duration < 0)
				banned.remove(name);
			else
				banned.put(name, duration);
		}
	}
}
