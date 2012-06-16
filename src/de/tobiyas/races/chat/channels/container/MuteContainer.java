package de.tobiyas.races.chat.channels.container;

import java.util.HashMap;
import java.util.Set;

import de.tobiyas.races.configuration.global.YAMLConfigExtended;

public class MuteContainer {

	private HashMap<String, Integer> muted = new HashMap<String, Integer>();
	
	public MuteContainer(){
	}
	
	public MuteContainer(YAMLConfigExtended config, String channelPre){
		Set<String> mutedPlayers = config.getYAMLChildren(channelPre + ".muted");
		for(String playerName : mutedPlayers){
			int time = config.getInt(channelPre + ".muted." + playerName);
			muted.put(playerName, time);
		}
	}
	
	public void mutePlayer(String playerName, int time){
		if(!muted.containsKey(playerName))
			muted.put(playerName, time);
	}
	
	public void unmutePlayer(String playerName){
		for(String name : muted.keySet())
			if(playerName.equalsIgnoreCase(name))
				muted.remove(name);
	}
	
	public void saveContainer(YAMLConfigExtended config, String channelPre){
		for(String name : muted.keySet()){
			int time = muted.get(name);
			config.set(channelPre + ".muted." + name, time);
		}
	}
	
	public int isMuted(String playerName){
		for(String name : muted.keySet()){
			if(playerName.equalsIgnoreCase(name))
				return muted.get(name);
		}
		
		return -1;
	}
	
	public void tick(){
		for(String name : muted.keySet()){
			int duration = muted.get(name);
			if(duration == Integer.MAX_VALUE) continue;
			duration --;
			if(duration < 0)
				muted.remove(name);
			else
				muted.put(name, duration);
		}
	}
}
