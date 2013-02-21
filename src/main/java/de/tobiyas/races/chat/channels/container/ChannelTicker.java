package de.tobiyas.races.chat.channels.container;

import java.util.HashSet;

import org.bukkit.Bukkit;

import de.tobiyas.races.Races;

public class ChannelTicker implements Runnable {

	private Races plugin;
	
	private static ChannelTicker ticker = null;
	private int currentTask;
	private HashSet<ChannelContainer> channels;
	
	
	public ChannelTicker(){
		plugin = Races.getPlugin();
		
		channels = new HashSet<ChannelContainer>();
		currentTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
	}
	
	private void disableIntern(){
		if(ticker != null){
			channels.clear();
			Bukkit.getScheduler().cancelTask(currentTask);
		}
	}
	
	public static void init(){
		if(ticker != null)
			ticker.disableIntern();
		
		ticker = new ChannelTicker();
	}
	
	public static void disable(){
		ticker.disableIntern();
	}
	
	public static void registerChannel(ChannelContainer container){
		ticker.channels.add(container);
	}
	
	public static void unregisterChannel(ChannelContainer container){
		ticker.channels.remove(container);
	}
	
	@Override
	public void run() {
		for(ChannelContainer container : channels){
			if(container == null) 
				channels.remove(container);
			else
				container.tick();
		}
	}

}
